package modelos;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;



@Entity
public  class Conta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int numero;
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;
    protected double saldo;
    private String tipoConta;

    public Conta() {}

    public Conta(Cliente cliente, String tipoConta) {
        this.cliente = cliente;
        this.tipoConta = tipoConta;
    }

    public int getNumero() {
        return numero;
    }

    public double getSaldo() {
        return saldo;
    }


    public boolean sacar(double valor) {
        double TAXA_DE_SAQUE = 0.02;
        double total = valor + valor * TAXA_DE_SAQUE;
        if (valor <= 0 || total > saldo) return false;
        saldo -= total;
        return true;
    }

    public boolean depositar(double valor) {
        if (valor <= 0) return false;
        saldo += valor;
        return true;
    }

    public boolean transferir(double valor, Conta destino) {
        if (valor <= 0 || saldo < valor) return false;
        saldo -= valor;
        destino.saldo += valor;
        return true;
    }
    public String getTipoConta() {
        return tipoConta;
    }

    public Cliente getCliente() {
        return cliente;
    }
}