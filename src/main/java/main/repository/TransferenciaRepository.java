package main.repository;

import jakarta.persistence.PersistenceException;
import main.exceptions.AplicacaoException;
import main.exceptions.ContaException;
import main.exceptions.MovimentacaoException;
import main.exceptions.SaldoInsuficienteException;
import main.exceptions.ValorInvalidoException;
import main.movimentacoes.Aplicacao;
import main.movimentacoes.Transferencia;
import modelos.Conta;
import org.hibernate.Session;
import org.hibernate.Transaction;
import servicos.HoraData;
import servicos.TipoMovimentacao;
import util.HibernateUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static main.repository.AplicacaoRepository.saqueDasAplicacoes;
import static modelos.RegrasDeBanco.LIMITE_DOC;
import static modelos.RegrasDeBanco.TAXA_INVESTIMENTO;

public class TransferenciaRepository {
   public static void transacao(int origem, int destino, BigDecimal valor,
                            TipoMovimentacao tipo, HoraData getTime) {
       Transaction tx = null;
       try (Session session = HibernateUtil.fcCliente.openSession()) {
           tx = session.beginTransaction();
           Conta contaOrigem = Optional.ofNullable(session.find(Conta.class, origem))
                   .orElseThrow(ContaException::new);
           Conta contaDestino = Optional.ofNullable(session.find(Conta.class, destino))
                   .orElseThrow(ContaException::new);
           validarTransferencia(valor, tipo, getTime, contaOrigem, contaDestino, session);
           session.merge(contaOrigem);
           session.merge(contaDestino);
           session.persist(criarTransferencia(valor,
                   tipo,
                   getTime,
                   contaOrigem,
                   contaDestino));
           tx.commit();
       } catch (ContaException | MovimentacaoException | AplicacaoException
                | SaldoInsuficienteException | ValorInvalidoException cont){
           if (tx != null) tx.rollback();
           throw cont;
       } catch (PersistenceException e) {
           if (tx != null && tx.isActive()) tx.rollback();
           throw e;
       }
}

    private static void validarTransferencia(BigDecimal valor, TipoMovimentacao tipo, HoraData getTime, Conta contaOrigem, Conta contaDestino, Session session) {
        if (contaOrigem.equals(contaDestino)) throw new ContaException("Contas iguais para transferir");

        if (tipo == TipoMovimentacao.DOC && valor.compareTo(LIMITE_DOC) > 0){
            throw new MovimentacaoException("Limite de transferencia excedido por DOC");
        }

        if (tipo == TipoMovimentacao.RESGATE) {
            executarResgate(valor, contaOrigem, session);
        }

        contaOrigem.transferir(valor, contaDestino);

        if (tipo == TipoMovimentacao.APLICACAO){
            Aplicacao aplicacao = criarAplicacao(valor, getTime, contaDestino);
            session.persist(aplicacao);
        }
    }

    private static void executarResgate(BigDecimal valor, Conta contaOrigem, Session session) {
        saqueDasAplicacoes(session, contaOrigem, valor);
    }

    private static Aplicacao criarAplicacao(BigDecimal valor, HoraData getTime, Conta contaDestino) {
        LocalDate dataAplicacao = getTime.getData();
        Aplicacao aplicacao = new Aplicacao(contaDestino,
                valor,
                dataAplicacao,
                dataAplicacao,
                TAXA_INVESTIMENTO);
        aplicacao.setAtivo();
        return aplicacao;
    }

    private static Transferencia criarTransferencia(BigDecimal valor, TipoMovimentacao tipo, HoraData getTime, Conta contaOrigem, Conta contaDestino) {
        Transferencia transferencia = new Transferencia(
                getTime,
                valor,
                contaOrigem,
                contaDestino
        );
        transferencia.setTipo(tipo);
        return transferencia;
    }
}
