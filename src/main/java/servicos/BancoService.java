package servicos;

import modelos.*;
import org.hibernate.Session;
import util.HibernateUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static main.repository.ClienteRepository.buscarPorCpf;
import static main.repository.TransferenciaRepository.transacao;


public class BancoService {


    public Conta criarConta(Cliente cliente, TipoConta tipo) {
        return new Conta(cliente,tipo,LocalDate.now());
    }

    public boolean cpfExiste(String cpf) {

        return buscarPorCpf(cpf) != null;

    }

//    public boolean cpfExiste(String cpf) {
//        return contas.stream()
//                .anyMatch(c -> c.getCliente().getCpf().equals(cpf));
//    }
//
//    public Conta buscarConta(int numero) {
//        return contas.stream()
//                .filter(c -> c.getNumero() == numero)
//                .findFirst()
//                .orElse(null);
//    }

    public HoraData getTime(){
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();
        return new HoraData(date ,time);
    }

    public boolean transferir(int origem, int destino, BigDecimal valor,TipoMovimentacao tipo) {
      return transacao(origem,destino,valor,tipo,getTime());

    }

    public List<Conta> listarContas(String cpf) {
        try (Session session = HibernateUtil.fcCliente.openSession()) {
            return session.createQuery("from Conta c " +
                            "where c.cliente.cpf = :cpf", Conta.class)
                    .setParameter("cpf",cpf).list();
        }
    }


}