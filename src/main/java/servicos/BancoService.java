package servicos;

import main.exceptions.ContaException;
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

    public void cpfExiste(String cpf) {

        buscarPorCpf(cpf).orElseThrow(() -> new ContaException("CPF já cadastrado!"));

    }

    public HoraData getTime(){
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();
        return new HoraData(date ,time);
    }

    public void transferir(int origem, int destino, BigDecimal valor,TipoMovimentacao tipo) {
        transacao(origem,destino,valor,tipo,getTime());
    }

    public List<Conta> listarContas(String cpf) {
        try (Session session = HibernateUtil.fcCliente.openSession()) {
            return session.createQuery("from Conta c " +
                            "where c.cliente.cpf = :cpf", Conta.class)
                    .setParameter("cpf",cpf).list();
        }
    }


}