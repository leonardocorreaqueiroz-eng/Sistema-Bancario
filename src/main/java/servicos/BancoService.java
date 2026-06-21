package servicos;

import modelos.*;
import org.hibernate.Session;
import util.HibernateUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


import static main.repository.AplicacaoRepository.salvarAplicacao;
import static main.repository.AplicacaoRepository.saqueDasAplicacoes;
import static main.repository.ClienteRepository.buscarPorCpf;
import static main.repository.ContaRepository.buscarConta;
import static main.repository.ExtratoRepository.salvarExtrato;


public class BancoService {


    public Conta criarConta(Cliente cliente, TipoConta tipo) {
        return new Conta(cliente,tipo,LocalDate.now());
    }

    public boolean cpfExiste(String cpf) {

        return buscarPorCpf(cpf,TipoConta.CORRENTE) != null;

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

            Conta contaOrigem = buscarConta(origem);
            Conta contaDestino = buscarConta(destino);

            if (contaOrigem == null || contaDestino == null){
                return false;
            }
            if (tipo.equals(TipoMovimentacao.DOC)){
                if (valor.compareTo(BigDecimal.valueOf(4999)) > 0){
                    return false;
                }
            }

            if (tipo.equals(TipoMovimentacao.RESGATE)) {
                if (saqueDasAplicacoes(contaOrigem,valor)) return false;
            }
            if (!contaOrigem.transferir(valor, contaDestino)) {
                return false;
            }

            if (tipo.equals(TipoMovimentacao.APLICACAO)){
                HoraData agora = getTime();

                LocalDate dataAplicacao = agora.getData();
                Aplicacao aplicacao = new Aplicacao(contaDestino,
                        valor,
                        dataAplicacao,
                        dataAplicacao,
                        new BigDecimal("0.00042"));
                aplicacao.setStatusAplicacao(StatusAplicacao.ATIVO);
                salvarAplicacao(aplicacao);
            }

            Extrato extrato = new Extrato(
                    getTime(),
                    valor,
                    contaOrigem,
                    contaDestino
            );
            extrato.setTipo(tipo);
            salvarExtrato(extrato);
            return true;
    }

    public List<Conta> listarContas(String cpf) {
        try (Session session = HibernateUtil.fcCliente.openSession()) {
            return session.createQuery("from Conta c " +
                            "where c.cliente.cpf = :cpf", Conta.class)
                    .setParameter("cpf",cpf).list();
        }
    }


}