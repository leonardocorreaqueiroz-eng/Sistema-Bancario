package main.exceptions;

public class AplicacaoException extends ValidacaoException {
    public AplicacaoException(String message) {
        super(message);
    }
    public AplicacaoException(){
        super("O valor excedeu o total das aplicações!");
    }
}
