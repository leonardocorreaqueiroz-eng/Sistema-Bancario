package main.exceptions;

public class ContaException extends ValidacaoException {
    public ContaException(String message) {
        super(message);
    }
    public ContaException(){
        super("Conta não encontrada!");
    }
}
