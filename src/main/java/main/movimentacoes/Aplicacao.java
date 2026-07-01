package main.movimentacoes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import modelos.Conta;
import modelos.StatusAplicacao;


import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class Aplicacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "conta")
    private Conta conta;
    private BigDecimal valorAplicado;
    private LocalDate dataAplicacao;
    private LocalDate ultimaCapitalizacao;
    private BigDecimal taxaDiaria;
    @Enumerated(EnumType.STRING)
    private StatusAplicacao status;

    public Aplicacao() {
    }

    public Aplicacao(Conta contaInvestimento,
                     BigDecimal valorAplicado,
                     LocalDate dataAplicacao,
                     LocalDate ultimaCapitalizacao,
                     BigDecimal taxaDiaria) {
        this.conta = contaInvestimento;
        this.valorAplicado = valorAplicado;
        this.dataAplicacao = dataAplicacao;
        this.ultimaCapitalizacao = ultimaCapitalizacao;
        this.taxaDiaria = taxaDiaria;
    }

    public void aplicarRendimento(LocalDate ultimaCapitalizacao,
                                  BigDecimal valorAplicado) {
        this.ultimaCapitalizacao = ultimaCapitalizacao;
        this.valorAplicado = valorAplicado;
    }

    public BigDecimal resgatar(BigDecimal valor){

        if (valorAplicado.compareTo(valor) >= 0) {

            valorAplicado = valorAplicado.subtract(valor);

            if (valorAplicado.compareTo(BigDecimal.ZERO) <= 0) {
                status = StatusAplicacao.RESGATADA;
            }

            return BigDecimal.ZERO;
        }

        BigDecimal restante = valor.subtract(valorAplicado);

        valorAplicado = BigDecimal.ZERO;
        status = StatusAplicacao.RESGATADA;

        return restante;
    }

    public void setAtivo() {
        this.status = StatusAplicacao.ATIVO;
    }

    public BigDecimal getValorAplicado() {
        return valorAplicado;
    }


    public LocalDate getUltimaCapitalizacao() {
        return ultimaCapitalizacao;
    }

    public BigDecimal getTaxaDiaria() {
        return taxaDiaria;
    }

}



