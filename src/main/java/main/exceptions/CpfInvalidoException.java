package main.exceptions;

public class CpfInvalidoException extends ValidacaoException {
    public CpfInvalidoException() {
        super("CPF iválido!");
    }
}
