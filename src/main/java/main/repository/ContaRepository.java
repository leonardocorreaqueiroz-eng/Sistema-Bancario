package main.repository;

import modelos.Cliente;
import modelos.Conta;
import modelos.TipoConta;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

import java.util.Optional;

public class ContaRepository {
    public static Conta buscarConta(int numero) {
        try (Session session = HibernateUtil.fcCliente.openSession()) {
            Optional<Conta> conta = Optional.ofNullable(session.find(Conta.class, numero));
            return conta.orElse(null);
        }
    }

    public static boolean cadastrarConta(Cliente cliente, Conta contaCorrente, Conta contaInvestimento) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.fcCliente.openSession()) {
            transaction = session.beginTransaction();
            session.persist(cliente);
            session.persist(contaCorrente);
            session.persist(contaInvestimento);
            transaction.commit();
            return true;
        } catch (Exception ex) {
            System.out.println("Erro ao cadastrarConta: " + ex.getMessage());
            if (transaction != null) {
                transaction.rollback();
            }
        }
        return false;
    }
}
