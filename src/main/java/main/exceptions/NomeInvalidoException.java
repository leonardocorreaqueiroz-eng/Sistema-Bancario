package main.exceptions;

public class NomeInvalidoException extends ValidacaoException {
    public NomeInvalidoException() {
        super("Nome inválido!");
    }
}
