package main.repository;

import modelos.Cliente;
import modelos.Conta;
import modelos.TipoConta;
import org.hibernate.Session;
import util.HibernateUtil;

import java.util.Optional;

public class ClienteRepository {
    public static Optional<Conta> buscarPorCpf(String cpf, TipoConta tipoConta) {
        try (Session session = HibernateUtil.fcCliente.openSession()) {
            return Optional.ofNullable(session.createQuery(
                            "from Conta c where c.cliente.cpf = :cpf and c.tipoConta = :tipo",
                            Conta.class)
                    .setParameter("cpf", cpf)
                    .setParameter("tipo", tipoConta)
                    .uniqueResult());
        }
    }
    public static Optional<Cliente> buscarPorCpf(String cpf) {
        try (Session session = HibernateUtil.fcCliente.openSession()) {
            return Optional.ofNullable(session.createQuery(
                            "from Cliente c where c.cpf = :cpf",
                            Cliente.class)
                    .setParameter("cpf", cpf)
                    .uniqueResult());
        }
    }
}
