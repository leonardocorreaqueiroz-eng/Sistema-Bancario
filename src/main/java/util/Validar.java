package util;

public class Validar {

    public static boolean validarCpf(String cpf) {
        String valor = cpf.replaceAll("\\D", "");

        if (valor.length() != 11) return false;
        if (valor.matches("^(\\d)\\1{10}$")) return false;

        int base = Integer.parseInt(valor.substring(0, 9));

        int d1 = calculoCpf(base);
        int d2 = calculoCpf(base * 10 + d1);

        return valor.substring(9).equals("" + d1 + d2);
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

    public static boolean validarNome(String nome) {
        return nome != null &&
                nome.matches("[a-zA-ZÀ-ÿ\\s]{2,50}");
    }
}