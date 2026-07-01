package main.exceptions;

public class TamanhoIncorretoException extends ValidacaoException {
    public TamanhoIncorretoException(int tamanho) {
        super("CPF deve possuir 11 dígitos. Recebido: " + tamanho);
    }
}
