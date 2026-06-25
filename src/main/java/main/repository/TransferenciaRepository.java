package main.repository;

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

import static main.repository.AplicacaoRepository.saqueDasAplicacoes;
import static modelos.RegrasDeBanco.LIMITE_DOC;
import static modelos.RegrasDeBanco.TAXA_INVESTIMENTO;

public class TransferenciaRepository {
   public static boolean transacao(int origem, int destino, BigDecimal valor,
                            TipoMovimentacao tipo, HoraData getTime) {
       Transaction tx = null;
       try (Session session = HibernateUtil.fcCliente.openSession()) {
           tx = session.beginTransaction();
           Conta contaOrigem = session.find(Conta.class, origem);
           Conta contaDestino = session.find(Conta.class, destino);

           if (contaOrigem == null || contaDestino == null){
               tx.rollback();
               return false;
           }

           if (contaOrigem.equals(contaDestino)) return false;

           if (tipo.equals(TipoMovimentacao.DOC)){
               if (valor.compareTo(LIMITE_DOC) > 0){
                   tx.rollback();
                   return false;
               }
           }

           if (tipo.equals(TipoMovimentacao.RESGATE)) {
               if (!saqueDasAplicacoes(session,contaOrigem,valor)) {
                   tx.rollback();
                   return false;
               }
           }
           if (!contaOrigem.transferir(valor, contaDestino)) {
               tx.rollback();
               return false;
           }

           if (tipo.equals(TipoMovimentacao.APLICACAO)){


               LocalDate dataAplicacao = getTime.getData();
               Aplicacao aplicacao = new Aplicacao(contaDestino,
                       valor,
                       dataAplicacao,
                       dataAplicacao,
                       TAXA_INVESTIMENTO);
               aplicacao.setAtivo();
               session.persist(aplicacao);
           }

           Transferencia transferencia = new Transferencia(
                   getTime,
                   valor,
                   contaOrigem,
                   contaDestino
           );
           session.merge(contaOrigem);
           session.merge(contaDestino);
           session.persist(transferencia);
           transferencia.setTipo(tipo);
           tx.commit();
           return true;
       } catch (Exception e) {
           if (tx != null && tx.isActive()) tx.rollback();
           return false;
       }
   }
}
