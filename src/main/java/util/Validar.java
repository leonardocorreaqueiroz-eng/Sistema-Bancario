package util;

import main.exceptions.CpfInvalidoException;
import main.exceptions.NomeInvalidoException;
import main.exceptions.NumeroRepetidoException;
import main.exceptions.TamanhoIncorretoException;

public class Validar {

    public static void validarCpf(String cpf) {
        String valor = cpf.replaceAll("\\D", "");

        if (valor.length() != 11){
            throw new TamanhoIncorretoException(valor.length());
        }
        if (valor.matches("^(\\d)\\1{10}$"))
            throw new NumeroRepetidoException();

        int base = Integer.parseInt(valor.substring(0, 9));

        int d1 = calculoCpf(base);
        int d2 = calculoCpf(base * 10 + d1);

        if (!valor.substring(9).equals("" + d1 + d2)){
            throw new CpfInvalidoException();
        }
    }

    private static int calculoCpf(int num) {
        int soma = 0;

        for (int i = 2; i < 11; i++) {
            soma += (num % 10) * i;
            num /= 10;
        }

        int resto = soma % 11;
        return (resto > 1) ? (11 - resto) : 0;
    }

    public static void validarNome(String nome) {
        if (nome != null && !nome.matches("[a-zA-ZÀ-ÿ\\s]{2,50}"))
            throw new NomeInvalidoException();
    }
}