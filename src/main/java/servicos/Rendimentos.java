package servicos;

import modelos.Conta;
import modelos.Aplicacao;
import modelos.Extrato;

import modelos.TipoConta;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static main.repository.AplicacaoRepository.listarAplicacoes;

public class Rendimentos {
    private final int contaOrigem;
    private final int contaDestino;

    public Rendimentos(int contaOrigem, int contaDestino) {
        this.contaOrigem = contaOrigem;
        this.contaDestino = contaDestino;
    }

    public void aplicarRendimentos(){
        Transaction transaction = null;
        try (Session session = HibernateUtil.fcCliente.openSession()) {
            Conta origem = session.find(Conta.class, contaOrigem);
            Conta destino = session.find(Conta.class, contaDestino);
            if (origem == null || destino == null) {
                return;
            }
            if (origem.getCliente().getCpf().equals(destino.getCliente().getCpf())) {

                if (origem.getTipoConta().equals(TipoConta.CORRENTE)
                        && destino.getTipoConta().equals(TipoConta.INVESTIMENTO)){
                    transaction = session.beginTransaction();
                    List<Aplicacao> aplicacaos = listarAplicacoes(destino);
                    if (aplicacaos.isEmpty()) {
                        transaction.rollback();
                        return;
                    }
                    for(Aplicacao apl : aplicacaos) {
                        LocalDate data1 = apl.getUltimaCapitalizacao();
                        LocalDate data2 = LocalDate.now();
                        BigDecimal render = apl.getValorAplicado().multiply(apl.getTaxaDiaria());
                        long dias = ChronoUnit.DAYS.between(data1,data2);
                        BigDecimal rendimento = render.multiply(BigDecimal.valueOf(dias));
                        if (dias == 0) continue;
                        destino.depositar(rendimento);
                        Extrato extrato = new Extrato(
                                new HoraData(data2,
                                        LocalTime.now()),
                                rendimento,
                                destino,destino);
                        extrato.setTipo(TipoMovimentacao.RENDIMENTO);
                        session.persist(extrato);
                        apl.setUltimaCapitalizacao(data2);
                        apl.setValorAplicado(apl.getValorAplicado().add(rendimento));
                        session.merge(apl);
                    }
                    transaction.commit();
                }
            }
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }

}