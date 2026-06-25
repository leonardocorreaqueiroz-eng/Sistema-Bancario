package servicos;

import main.movimentacoes.Aplicacao;
import main.movimentacoes.Rendimentos;
import modelos.Conta;
import modelos.TipoConta;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static main.repository.AplicacaoRepository.listarAplicacoes;

public class AplicacaoDeRendimentos {
    public static void aplicarRendimentos(Conta contaAtual){
        Transaction transaction = null;
        try (Session session = HibernateUtil.fcCliente.openSession()) {
            Conta conta = session.find(Conta.class, contaAtual.getNumero());
            if (conta == null) {
                return;
            }

            if (conta.getTipoConta().equals(TipoConta.INVESTIMENTO)){
                transaction = session.beginTransaction();
                List<Aplicacao> aplicacaos = listarAplicacoes(session,conta);
                if (aplicacaos.isEmpty()) {
                    transaction.rollback();
                    return;
                }
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
                transaction.commit();
            }
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }
}
