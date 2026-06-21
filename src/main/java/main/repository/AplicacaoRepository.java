package main.repository;

import modelos.Aplicacao;
import modelos.Conta;
import modelos.StatusAplicacao;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

import java.math.BigDecimal;
import java.util.List;

public class AplicacaoRepository {
    public static List<Aplicacao> listarAplicacoes(Conta conta){
        try (Session session = HibernateUtil.fcCliente.openSession()) {
           return session.createQuery("from Aplicacao apl " +
                            "where apl.contaInvestimento = :conta ",Aplicacao.class)
                    .setParameter("conta",conta)
                    .getResultList();
        }
    }

    public static boolean saqueDasAplicacoes(Conta conta, BigDecimal valor){
        try (Session session = HibernateUtil.fcCliente.openSession()) {
            Transaction transaction = session.beginTransaction();
            List<Aplicacao> aplicacoes = listarAplicacoes(conta);

            if (aplicacoes.isEmpty()) {
                transaction.rollback();
                return false;
            }

            BigDecimal totalAplicado = aplicacoes.stream().map(Aplicacao::getValorAplicado)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (valor.compareTo(totalAplicado) > 0){
                transaction.rollback();
                return false;
            }


            BigDecimal restante = valor;
            for (int i = aplicacoes.size() - 1; i >= 0; i--) {
                BigDecimal valorAplicado = aplicacoes.get(i).getValorAplicado();
                BigDecimal novoValor = valorAplicado.subtract(restante);
                if (valorAplicado.compareTo(restante) >= 0) {
                    aplicacoes.get(i).setValorAplicado(novoValor);

                    if (novoValor.compareTo(BigDecimal.valueOf(0.000001)) <= 0){
                        aplicacoes.get(i).setStatusAplicacao(StatusAplicacao.RESGATADA);
                    }

                    restante = BigDecimal.ZERO;
                    break;
                }

                if (novoValor.compareTo(BigDecimal.ZERO) <= 0) {
                    restante = restante.subtract(valorAplicado);
                    aplicacoes.get(i).setStatusAplicacao(StatusAplicacao.RESGATADA);
                }
            }

            if (restante.compareTo(BigDecimal.ZERO) != 0) {
                transaction.rollback();
                return false;
            }
            session.getTransaction().commit();
            return true;
        } catch (Exception e) {
            System.out.println( e.getMessage() );
            return false;
        }
    }

    public static void salvarAplicacao(Aplicacao aplicacao){
        try (Session session = HibernateUtil.fcCliente.openSession()) {
            session.beginTransaction();
            session.persist(aplicacao);
            session.getTransaction().commit();
        } catch(Exception ex){
            System.out.println(ex.getMessage());
        }
    }
}
