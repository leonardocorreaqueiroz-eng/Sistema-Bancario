package modelos;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.math.BigDecimal;
import java.time.LocalDate;


@Entity
public  class Conta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int numero;
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;
    @Column(precision = 19, scale = 2)
    protected BigDecimal saldo = BigDecimal.ZERO;
    @Enumerated(EnumType.STRING)
    private TipoConta tipoConta;
    private LocalDate dataDeCriacao;

    public Conta() {}

    public Conta(Cliente cliente, TipoConta tipoConta, LocalDate dataDeCriacao) {
        this.cliente = cliente;
        this.tipoConta = tipoConta;
        this.dataDeCriacao = dataDeCriacao;
    }

    public int getNumero() {
        return numero;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }


    public boolean sacar(BigDecimal valor) {
        BigDecimal TAXA_DE_SAQUE = BigDecimal.valueOf(0.02);
        BigDecimal total = valor.add(valor.multiply(TAXA_DE_SAQUE)) ;
        if (valor.compareTo(BigDecimal.ZERO) <= 0 || saldo.compareTo(total) < 0) return false;
        saldo = saldo.subtract(total);
        return true;
    }

    public boolean depositar(BigDecimal valor) {
        if (valor.compareTo(BigDecimal.ZERO) <= 0) return false;
        saldo= saldo.add(valor);
        return true;
    }

    public boolean transferir(BigDecimal valor, Conta destino) {
        if (valor.compareTo(BigDecimal.ZERO) <= 0 || saldo.compareTo(valor) < 0) return false;
        saldo = saldo.subtract(valor);
        destino.saldo = destino.saldo.add(valor);
        return true;
    }
    public String getTipoConta() {
        return tipoConta;
    }

    public Cliente getCliente() {
        return cliente;
    }
}