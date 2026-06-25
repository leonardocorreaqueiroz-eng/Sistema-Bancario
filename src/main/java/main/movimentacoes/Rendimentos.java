package main.movimentacoes;

import jakarta.persistence.Entity;
import modelos.Conta;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class Rendimentos extends Movimentacao {


    private int diasCapitalizados;
    private BigDecimal taxa;
    private BigDecimal valorAntes;


    public Rendimentos() {}

    public Rendimentos(Conta conta,
                       LocalDate dataAtual,
                       LocalTime hora,
                       int diasCapitalizados,
                       BigDecimal taxa,
                       BigDecimal valorAntes,
                       BigDecimal valorDepois) {
        super(valorDepois,dataAtual,hora,conta);

        this.diasCapitalizados = diasCapitalizados;
        this.taxa = taxa;
        this.valorAntes = valorAntes;

    }

    @Override
    public String toString() {
        return super.toString() + "\nDias Capitalizados: "+diasCapitalizados+" | Taxa: "+taxa+" " +
                "| ValorAntes: "+valorAntes+"\n";
    }
}