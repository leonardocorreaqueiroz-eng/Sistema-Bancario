package main.movimentacoes;

import jakarta.persistence.Entity;
import modelos.Conta;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
@Entity
public class Saque extends Movimentacao {

    public  Saque() {}

    public Saque(Conta conta, BigDecimal valor, LocalDate data, LocalTime hora) {
        super(valor,data,hora,conta);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
