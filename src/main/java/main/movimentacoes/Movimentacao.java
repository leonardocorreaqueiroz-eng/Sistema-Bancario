package main.movimentacoes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import modelos.Conta;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Movimentacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(precision = 19, scale = 2)
    private BigDecimal valor;
    private LocalDate data;
    private LocalTime hora;
    @ManyToOne
    @JoinColumn(name = "conta_numero")
    private Conta conta;

    protected Movimentacao() {}

    protected Movimentacao(BigDecimal valor, LocalDate data, LocalTime hora, Conta conta) {
        this.valor = valor;
        this.data = data;
        this.hora = hora;
        this.conta = conta;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public LocalDate getData() {
        return data;
    }

    public LocalTime getHora() {
        return hora;
    }

    public Conta getConta() {
        return conta;
    }

    @Override
    public String toString() {
        return (getClass().getSimpleName()+"\n" +" | Valor "+valor+" | Data: "+ data + " | Hora: " + hora.toString()+"\n");
    }
}
