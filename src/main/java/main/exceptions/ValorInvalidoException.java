package main.exceptions;

import java.math.BigDecimal;

public class ValorInvalidoException extends ValidacaoException {
    public ValorInvalidoException(BigDecimal valor) {
        super("Valor inválido" + valor);
    }
}
