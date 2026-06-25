package main.repository;

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

    public static void aplicarDeposito(Conta conta, BigDecimal valor, Deposito deposito) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.fcCliente.openSession()) {
            transaction = session.beginTransaction();
            if (!conta.depositar(valor)) {
                System.out.println("Valor invalido!");
                transaction.rollback();
            }
            session.merge(conta);
            session.persist(deposito);
            transaction.commit();
        } catch (Exception ex) {
            System.out.println("Erro ao realizar o depósito: " + ex.getMessage());
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }
    public static void aplicarSaque(Conta conta, BigDecimal valor, Saque saque) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.fcCliente.openSession()) {
            transaction = session.beginTransaction();
            if (!conta.sacar(valor)) {
                System.out.println("Saldo insuficiente ou valor invalido!");
                transaction.rollback();
            }
            session.merge(conta);
            session.persist(saque);
            transaction.commit();
        } catch (Exception ex) {
            System.out.println("Erro ao realizar o saque: " + ex.getMessage());
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }
}
