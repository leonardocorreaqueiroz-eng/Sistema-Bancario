package servicos;

import modelos.*;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


public class BancoService {


    public Conta criarConta(Cliente cliente, TipoConta tipo) {
        Conta conta;

        if (tipo == TipoConta.CORRENTE) {
            conta = new Conta(cliente,"Corrente");
        } else {
            conta = new Conta(cliente,"Investimento");
        }

        return conta;
    }

    public boolean cpfExiste(String cpf) {
        try (Session session = HibernateUtil.fcCliente.openSession()) {

            Integer quantidade = session.createQuery("select 1 from Conta c " +
                            "where c.cliente.cpf = :cpf", Integer.class)
                    .setParameter("cpf", cpf).uniqueResult();

            return quantidade != null;
        }
    }
    public Conta buscarConta(int numero) {
        try (Session session = HibernateUtil.fcCliente.openSession()) {
            return session.find(Conta.class, numero);
        }
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

    public boolean transferir(int origem, int destino, double valor,TipoMovimentacao tipo) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.fcCliente.openSession()) {
            transaction = session.beginTransaction();
            Conta contaOrigem = session.find(Conta.class, origem);
            Conta contaDestino = session.find(Conta.class, destino);

            if (contaOrigem == null || contaDestino == null){
                transaction.rollback();
                return false;
            }
            if (tipo.equals(TipoMovimentacao.DOC)){
                if (valor > 4999){
                    transaction.rollback();
                    return false;
                }
            }

            if (tipo.equals(TipoMovimentacao.RESGATE)) {
                List<Aplicacao> aplicacoes = session.createQuery("from Aplicacao apl " +
                                "where apl.contaInvestimento = :conta", Aplicacao.class)
                        .setParameter("conta", contaOrigem).getResultList();

                if (aplicacoes.isEmpty()) {
                    transaction.rollback();
                    return false;
                }

                double totalAplicado = aplicacoes.stream().mapToDouble(Aplicacao::getValorAplicado).sum();

                if (valor > totalAplicado){
                    transaction.rollback();
                    return false;
                }


                double restante = valor;
                for (int i = aplicacoes.size() - 1; i >= 0; i--) {
                    double valorAplicado = aplicacoes.get(i).getValorAplicado();
                    double novoValor = valorAplicado - restante;
                    if (valorAplicado >= restante) {
                        aplicacoes.get(i).setValorAplicado(novoValor);

                        if (novoValor <= 0.000001){
                            session.remove(aplicacoes.get(i));
                        }

                        restante = 0;
                        break;
                    }

                    if (novoValor <= 0) {
                        restante -= valorAplicado;
                        session.remove(aplicacoes.get(i));
                    }
                }
                if (restante != 0) {
                    transaction.rollback();
                    return false;
                }
            }
            if (!contaOrigem.transferir(valor, contaDestino)) {
                transaction.rollback();
                return false;
            }

            if (tipo.equals(TipoMovimentacao.APLICACAO)){
                HoraData agora = getTime();

                LocalDate dataAplicacao = agora.getData();
                Aplicacao aplicacao = new Aplicacao(contaDestino,
                        valor,
                        dataAplicacao,
                        dataAplicacao,
                        0.00042);
                session.persist(aplicacao);
            }

            Extrato extrato = new Extrato(
                    getTime(),
                    valor,
                    contaOrigem,
                    contaDestino
            );
            extrato.setTipo(tipo);

            session.persist(extrato);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.out.println(e.getMessage());
        }
        return false;
    }

    public List<Conta> listarContas(String cpf) {
        try (Session session = HibernateUtil.fcCliente.openSession()) {
            return session.createQuery("from Conta c " +
                            "where c.cliente.cpf = :cpf", Conta.class)
                    .setParameter("cpf",cpf).list();
        }
    }


}