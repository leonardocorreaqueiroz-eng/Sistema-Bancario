package main.repository;

import main.movimentacoes.Movimentacao;
import org.hibernate.Session;
import util.HibernateUtil;

import java.util.List;

public class ExtratoRepository {

    public static List<Movimentacao> verExtratos(String cpf) {
        try (Session session = HibernateUtil.fcCliente.openSession()) {
            return session.createQuery("from Movimentacao m where m.conta.cliente.cpf = :cpf " +
                            "order by m.data desc, m.hora desc", Movimentacao.class)
                    .setParameter("cpf", cpf).list();
        }
    }
}
