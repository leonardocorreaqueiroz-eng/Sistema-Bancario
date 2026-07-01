package main.exceptions;

public class SaldoInsuficienteException extends ValidacaoException {
    public SaldoInsuficienteException() {
        super("Saldo insuficiente");
    }
}
