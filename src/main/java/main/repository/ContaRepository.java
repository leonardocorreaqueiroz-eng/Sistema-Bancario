package main.repository;

import jakarta.persistence.PersistenceException;
import main.exceptions.ContaException;
import main.exceptions.SaldoInsuficienteException;
import main.exceptions.ValorInvalidoException;
import main.movimentacoes.Deposito;
import main.movimentacoes.Saque;
import modelos.Cliente;
import modelos.Conta;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

import java.math.BigDecimal;
import java.util.Optional;

public class ContaRepository {
    public static Conta buscarConta(int numero) {
        try (Session session = HibernateUtil.fcCliente.openSession()) {
            return Optional.ofNullable(session.find(Conta.class, numero))
                    .orElseThrow(ContaException::new);
        }
    }

    public static void cadastrarConta(Cliente cliente, Conta contaCorrente, Conta contaInvestimento) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.fcCliente.openSession()) {
            transaction = session.beginTransaction();
            session.persist(cliente);
            session.persist(contaCorrente);
            session.persist(contaInvestimento);
            transaction.commit();
        } catch (PersistenceException ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw ex;
        }
    }

    public static void aplicarDeposito(Conta conta, BigDecimal valor, Deposito deposito) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.fcCliente.openSession()) {
            transaction = session.beginTransaction();
            conta.depositar(valor);
            session.merge(conta);
            session.persist(deposito);
            transaction.commit();
        } catch (PersistenceException | ValorInvalidoException ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw ex;
        }
    }
    public static void aplicarSaque(Conta conta, BigDecimal valor, Saque saque) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.fcCliente.openSession()) {
            transaction = session.beginTransaction();
            conta.sacar(valor);
            session.merge(conta);
            session.persist(saque);
            transaction.commit();
        } catch (PersistenceException | ValorInvalidoException | SaldoInsuficienteException ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw ex;
        }
    }
}
