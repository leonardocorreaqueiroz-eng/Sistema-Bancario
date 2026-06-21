package main.repository;

import modelos.Conta;
import modelos.Extrato;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

import java.util.List;

public class ExtratoRepository {

    public static List<Extrato> verExtratos(Conta conta) {
        try (Session session = HibernateUtil.fcCliente.openSession()) {
            return session.createQuery("from Extrato " +
                            "where conta = :conta", Extrato.class)
                    .setParameter("conta", conta).list();
        }
    }

    public static void salvarExtrato(Extrato extrato) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.fcCliente.openSession()) {
            transaction = session.beginTransaction();
            session.persist(extrato);
            transaction.commit();
        } catch (Exception e) {
            System.out.println("Erro ao salvarExtrato: " + e.getMessage());
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }
}
