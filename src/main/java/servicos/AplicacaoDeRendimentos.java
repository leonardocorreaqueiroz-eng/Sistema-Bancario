package servicos;

import jakarta.persistence.PersistenceException;
import main.exceptions.AplicacaoException;
import main.exceptions.ContaException;
import main.exceptions.ValorInvalidoException;
import main.movimentacoes.Aplicacao;
import main.movimentacoes.Rendimentos;
import modelos.Conta;
import modelos.TipoConta;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static main.repository.AplicacaoRepository.listarAplicacoes;

public class AplicacaoDeRendimentos {
    public static void aplicarRendimentos(Conta contaAtual){
        Transaction transaction = null;
        try (Session session = HibernateUtil.fcCliente.openSession()) {
            Conta conta = Optional.ofNullable(session
                    .find(Conta.class, contaAtual.getNumero())).orElseThrow(ContaException::new);

            if (conta.getTipoConta().equals(TipoConta.INVESTIMENTO)){
                transaction = session.beginTransaction();
                List<Aplicacao> aplicacaos = listarAplicacoes(session,conta);

                realizarRendimentoNasAplicacoes(aplicacaos, conta, session);
                transaction.commit();
            }
        } catch (ContaException | AplicacaoException | PersistenceException
                 | ValorInvalidoException e) {
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }

    private static void realizarRendimentoNasAplicacoes(List<Aplicacao> aplicacaos, Conta conta, Session session) {
        for(Aplicacao apl : aplicacaos) {
            BigDecimal valorAntes = apl.getValorAplicado();
            LocalDate data1 = apl.getUltimaCapitalizacao();
            LocalDate data2 = LocalDate.now();
            BigDecimal render = apl.getValorAplicado().multiply(apl.getTaxaDiaria());
            BigDecimal dias = new BigDecimal(ChronoUnit.DAYS.between(data1,data2));
            BigDecimal rendimento = render.multiply(dias);
            if (dias.compareTo(BigDecimal.ZERO) <= 0) continue;
            apl.aplicarRendimento(data2,rendimento);
            conta.depositar(rendimento);
            Rendimentos rendimentos =  new Rendimentos(
                    conta,data2,LocalTime.now(),dias.intValue(),apl.getTaxaDiaria(),
                    valorAntes,valorAntes.add(rendimento)
            );
            session.merge(conta);
            session.merge(apl);
            session.persist(rendimentos);
        }
    }
}
