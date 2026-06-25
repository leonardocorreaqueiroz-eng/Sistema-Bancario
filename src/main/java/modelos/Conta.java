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
import java.math.RoundingMode;
import java.time.LocalDate;

import static modelos.RegrasDeBanco.TAXA_SAQUE;


@Entity
public  class Conta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int numero;
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;
    @Column(precision = 19, scale = 2)
    private BigDecimal saldo = BigDecimal.ZERO;
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
        BigDecimal total = valor.add(valor.multiply(TAXA_SAQUE)) ;
        if (valor.compareTo(BigDecimal.ZERO) <= 0 || saldo.compareTo(total) < 0) return false;
        return sacarTransferencia(total);
    }

    public boolean depositar(BigDecimal valor) {
        if (valor.compareTo(BigDecimal.ZERO) <= 0) return false;
        saldo= saldo.add(valor.setScale(2, RoundingMode.HALF_EVEN));
        return true;
    }

    public boolean transferir(BigDecimal valor, Conta destino) {
        if (!sacarTransferencia(valor)) return false;
        depositarTransferencia(valor, destino);
        return true;
    }

    private static void depositarTransferencia(BigDecimal valor, Conta destino) {
        destino.saldo = destino.saldo.add(valor);
    }

    private boolean sacarTransferencia(BigDecimal valor) {
        if (valor.compareTo(BigDecimal.ZERO) <= 0 || saldo.compareTo(valor) < 0) return false;
        saldo = saldo.subtract(valor);
        return true;
    }


    public TipoConta getTipoConta() {
        return tipoConta;
    }

    public Cliente getCliente() {
        return cliente;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (!(obj instanceof Conta conta))
            return false;

        return numero == conta.numero;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(numero);
    }
}