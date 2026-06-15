package util;
import modelos.Cliente;
import modelos.Conta;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import modelos.Aplicacao;
import servicos.Extrato;
import servicos.HoraData;

public class HibernateUtil {
    private final static Configuration cfgCliente = new Configuration()
            .configure().addAnnotatedClass(Cliente.class)
            .addAnnotatedClass(Conta.class)
            .addAnnotatedClass(Extrato.class)
            .addAnnotatedClass(HoraData.class)
            .addAnnotatedClass(Aplicacao.class);

    private final static ServiceRegistry serviceRegistryCliente = new StandardServiceRegistryBuilder()
            .applySettings(cfgCliente.getProperties()).build();

    public static SessionFactory fcCliente = cfgCliente
            .buildSessionFactory(serviceRegistryCliente);

}