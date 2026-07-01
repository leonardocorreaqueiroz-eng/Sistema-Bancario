package main.repository;

import main.exceptions.AplicacaoException;
import main.movimentacoes.Aplicacao;
import modelos.Conta;
import modelos.StatusAplicacao;
import org.hibernate.Session;

import java.math.BigDecimal;
import java.util.List;

public class AplicacaoRepository {
    public static List<Aplicacao> listarAplicacoes(Session session,Conta conta){
           List<Aplicacao> aplicacaos = session.createQuery("from Aplicacao apl " +
                            "where apl.conta = :conta " +
                           "and apl.status = :status",Aplicacao.class)
                    .setParameter("conta",conta)
                    .setParameter("status",StatusAplicacao.ATIVO)
                    .getResultList();
            if (aplicacaos.isEmpty()) {
               throw new AplicacaoException("Nenhuma aplicacao foi encontrada");
            }
            return aplicacaos;
    }

    public static void saqueDasAplicacoes(Session session, Conta conta, BigDecimal valor){
            List<Aplicacao> aplicacoes = listarAplicacoes(session,conta);

            BigDecimal totalAplicado = aplicacoes.stream().map(Aplicacao::getValorAplicado)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (valor.compareTo(totalAplicado) > 0){
                throw new AplicacaoException();
            }


            BigDecimal restante = valor;

            for (int i = aplicacoes.size() - 1; i >= 0; i--) {

                restante = aplicacoes.get(i).resgatar(restante);

                if (restante.compareTo(BigDecimal.ZERO) == 0) {
                    break;
                }
            }

            if (restante.compareTo(BigDecimal.ZERO) != 0) {
                throw new AplicacaoException();
            }
    }
}
