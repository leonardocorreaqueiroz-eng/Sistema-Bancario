package modelos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;


import java.math.BigDecimal;
import java.time.LocalDate;
@Entity
public class Aplicacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Conta contaInvestimento;
    @Column(precision = 19, scale = 8)
    private BigDecimal valorAplicado;

    private LocalDate dataAplicacao;

    private LocalDate ultimaCapitalizacao;
    @Column(precision = 19, scale = 2)
    private BigDecimal taxaDiaria;
    private StatusAplicacao status;

    public Aplicacao() {}

    public Aplicacao(Conta contaInvestimento,
                     BigDecimal valorAplicado,
                     LocalDate dataAplicacao,
                     LocalDate ultimaCapitalizacao,
                     BigDecimal taxaDiaria) {
        this.contaInvestimento = contaInvestimento;
        this.valorAplicado = valorAplicado;
        this.dataAplicacao = dataAplicacao;
        this.ultimaCapitalizacao = ultimaCapitalizacao;
        this.taxaDiaria = taxaDiaria;
    }

    public void setStatusAplicacao(StatusAplicacao status) {
        this.status = status;
    }

    public StatusAplicacao getStatusAplicacao() {
        return status;
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

    public void setUltimaCapitalizacao(LocalDate ultimaCapitalizacao) {
        this.ultimaCapitalizacao = ultimaCapitalizacao;
    }

    public void setValorAplicado(BigDecimal valorAplicado) {
        this.valorAplicado = valorAplicado;
    }
}