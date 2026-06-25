package util;
import main.movimentacoes.Deposito;
import main.movimentacoes.Movimentacao;
import main.movimentacoes.Rendimentos;
import main.movimentacoes.Saque;
import main.movimentacoes.Transferencia;
import modelos.Cliente;
import modelos.Conta;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import main.movimentacoes.Aplicacao;
import servicos.HoraData;

public class HibernateUtil {
    private final static Configuration cfgCliente = new Configuration()
            .configure().addAnnotatedClass(Cliente.class)
            .addAnnotatedClass(Conta.class)
            .addAnnotatedClass(Deposito.class)
            .addAnnotatedClass(Saque.class)
            .addAnnotatedClass(Movimentacao.class)
            .addAnnotatedClass(Transferencia.class)
            .addAnnotatedClass(Rendimentos.class)
            .addAnnotatedClass(HoraData.class)
            .addAnnotatedClass(Aplicacao.class);

    private final static ServiceRegistry serviceRegistryCliente = new StandardServiceRegistryBuilder()
            .applySettings(cfgCliente.getProperties()).build();

    public static SessionFactory fcCliente = cfgCliente
            .buildSessionFactory(serviceRegistryCliente);

}