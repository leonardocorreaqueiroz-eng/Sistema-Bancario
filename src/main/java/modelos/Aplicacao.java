package modelos;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;


import java.time.LocalDate;
@Entity
public class Aplicacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Conta contaInvestimento;

    private double valorAplicado;

    private LocalDate dataAplicacao;

    private LocalDate ultimaCapitalizacao;

    private double taxaDiaria;

    public Aplicacao() {}

    public Aplicacao(Conta contaInvestimento, double valorAplicado,LocalDate dataAplicacao,
                     LocalDate ultimaCapitalizacao, double taxaDiaria) {
        this.contaInvestimento = contaInvestimento;
        this.valorAplicado = valorAplicado;
        this.dataAplicacao = dataAplicacao;
        this.ultimaCapitalizacao = ultimaCapitalizacao;
        this.taxaDiaria = taxaDiaria;
    }

    public double getValorAplicado() {
        return valorAplicado;
    }


    public LocalDate getUltimaCapitalizacao() {
        return ultimaCapitalizacao;
    }

    public double getTaxaDiaria() {
        return taxaDiaria;
    }

    public void setUltimaCapitalizacao(LocalDate ultimaCapitalizacao) {
        this.ultimaCapitalizacao = ultimaCapitalizacao;
    }

    public void setValorAplicado(double valorAplicado) {
        this.valorAplicado = valorAplicado;
    }
}