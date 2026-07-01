package main.exceptions;

public class ExtratoException extends ValidacaoException {
    public ExtratoException() {
        super("Nenhum extrato foi encontrado.");
    }
}
