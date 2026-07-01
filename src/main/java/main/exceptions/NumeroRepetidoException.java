package main.exceptions;

public class NumeroRepetidoException extends ValidacaoException {
    public NumeroRepetidoException() {
        super("CPF não pode conter todos os números iguais.");
    }
}
