package main;

import modelos.*;
        import org.hibernate.Session;
import org.hibernate.Transaction;
import servicos.BancoService;
import modelos.Extrato;
import servicos.Rendimentos;
import util.HibernateUtil;
import util.Validar;
import servicos.TipoMovimentacao;

import java.math.BigDecimal;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        BancoService banco = new BancoService();
        int numeroContaCorrente;
        int numeroContaInvestimento;
        String CPF;
        boolean rodando = true;
        while (rodando) {
            System.out.println("""
                    ===== MENU =====
                    1 - Criar Conta
                    2 - Entrar
                    3 - Sair
                    """);

            int op = Integer.parseInt(scanner.nextLine());

            switch (op) {

                case 1 -> {
                    System.out.print("Nome: ");
                    String nome = scanner.nextLine();

                    System.out.print("CPF: ");
                    String cpf = scanner.nextLine();

                    if (!Validar.validarNome(nome) || !Validar.validarCpf(cpf)) {
                        System.out.println("Dados inválidos!");
                        break;
                    }

                    if (banco.cpfExiste(cpf)) {
                        System.out.println("CPF já cadastrado!");
                        break;
                    }

                    TipoConta tipo = TipoConta.CORRENTE;
                    TipoConta tipo2 = TipoConta.INVESTIMENTO;
                    Cliente cliente = new Cliente(nome, cpf);
                    Conta contaCorrente = banco.criarConta(cliente, tipo);
                    Conta contaInvestimento = banco.criarConta(cliente, tipo2);
                    try (Session session = HibernateUtil.fcCliente.openSession()) {

                        Transaction tx = session.beginTransaction();
                        session.persist(cliente);
                        session.persist(contaCorrente);
                        session.persist(contaInvestimento);
                        tx.commit();
                    }

                    System.out.println("Conta criada com sucesso!");
                    System.out.println("Número da conta corrente: " + contaCorrente.getNumero());
                    System.out.println("Número da conta de investimento: " + contaInvestimento.getNumero());
                }
                case 2 -> {
                    System.out.println("Digite seu CPF: ");
                    CPF = scanner.nextLine();

                    try (Session session = HibernateUtil.fcCliente.openSession()) {
                        Conta contaCorrente = session.createQuery(
                                        "from Conta c where c.cliente.cpf = :cpf and upper(c.tipoConta) = :tipo",
                                        Conta.class
                                )
                                .setParameter("cpf", CPF)
                                .setParameter("tipo", "CORRENTE")
                                .getSingleResultOrNull();

                        Conta contaInvestimento = session.createQuery(
                                        "from Conta c where c.cliente.cpf = :cpf and upper(c.tipoConta) = :tipo",
                                        Conta.class
                                )
                                .setParameter("cpf", CPF)
                                .setParameter("tipo", "INVESTIMENTO")
                                .getSingleResultOrNull();

                        numeroContaCorrente = contaCorrente != null
                                ? contaCorrente.getNumero()
                                : 0;

                        numeroContaInvestimento = contaInvestimento != null
                                ? contaInvestimento.getNumero()
                                : 0;
                    }
                    boolean acesso;
                    acesso = true;
                    while (acesso) {

                        System.out.println("""
                    0 - Investimento
                    1 - Depositar
                    2 - Sacar
                    3 - Consultar saldo
                    4 - Transferir
                    5 - Listar contas
                    6 - Sair da Conta
                    """);
                        int input = Integer.parseInt(scanner.nextLine());
                        switch (input) {

                            case 0 -> {

                                boolean acesInvest = true;
                                while (acesInvest) {

                                    System.out.println("""
                                1 - Depositar
                                2 - Sacar
                                3 - Consultar saldo
                                4 - Voltar
                                """);

                                    int inputInvest = Integer.parseInt(scanner.nextLine());
                                    switch (inputInvest) {
                                        case 1 -> {
                                            System.out.println("Digite o valor que desja depositar:");
                                            BigDecimal valor = new BigDecimal(scanner.nextLine());
                                            if (banco.transferir(numeroContaCorrente,numeroContaInvestimento,
                                                    valor,TipoMovimentacao.APLICACAO)){
                                                System.out.println("Depósito realizado!");
                                            } else {
                                                System.out.println("Erro ao depositar!");
                                            }
                                        }
                                        case 2 -> {
                                            System.out.println("Digite o valor que desja resgatar:");
                                            BigDecimal valor = new BigDecimal(scanner.nextLine());
                                            if (banco.transferir(numeroContaInvestimento,numeroContaCorrente,
                                                    valor,TipoMovimentacao.RESGATE)){
                                                System.out.println("Saque realizado!");
                                            } else {
                                                System.out.println("Erro ao resgatar!");
                                            }
                                        }
                                        case 3 -> {
                                            try (Session session = HibernateUtil.fcCliente.openSession()) {

                                                Rendimentos rendimentos = new Rendimentos(numeroContaCorrente, numeroContaInvestimento);
                                                rendimentos.aplicarRendimentos();
                                                Conta conta = session.find(Conta.class, numeroContaInvestimento);
                                                System.out.println("R$: " + conta.getSaldo());
                                            }
                                        }
                                        default -> acesInvest = false;
                                    }
                                }
                            }
                            case 1 -> {

                                System.out.print("Valor do depósito: ");
                                BigDecimal valor = new BigDecimal(scanner.nextLine());

                                Conta conta = banco.buscarConta(numeroContaCorrente);

                                if (conta == null) {
                                    System.out.println("Conta não encontrada!");
                                    break;
                                }
                                try(Session session = HibernateUtil.fcCliente.openSession()) {
                                    Transaction tx = session.beginTransaction();

                                    if (conta.depositar(valor)) {
                                        Conta contaDep = session.find(Conta.class, numeroContaCorrente);
                                        contaDep.depositar(valor);
                                        Extrato extrato =  new Extrato(banco.getTime(),valor,contaDep,contaDep);
                                        extrato.setTipo(TipoMovimentacao.DEPOSITO);
                                        session.persist(extrato);
                                        tx.commit();
                                        System.out.println("Depósito realizado!");

                                    } else {
                                        System.out.println("Valor inválido!");
                                    }
                                } catch (Exception e) {
                                    System.out.println(e.getMessage());
                                }
                            }

                            case 2 -> {

                                System.out.print("Valor do saque: ");
                                BigDecimal valor = new BigDecimal(scanner.nextLine());

                                Conta conta = banco.buscarConta(numeroContaCorrente);

                                if (conta == null) {
                                    System.out.println("Conta não encontrada!");
                                    break;
                                }
                                try (Session session = HibernateUtil.fcCliente.openSession()) {

                                    if (conta.sacar(valor)) {
                                        Transaction tx = session.beginTransaction();
                                        Conta contaSaq = session.find(Conta.class, numeroContaCorrente);
                                        contaSaq.sacar(valor);
                                        Extrato extrato =  new Extrato(banco.getTime(),valor,contaSaq,contaSaq);
                                        extrato.setTipo(TipoMovimentacao.SAQUE);
                                        session.persist(extrato);
                                        tx.commit();
                                        System.out.println("Saque realizado!");
                                    } else {
                                        System.out.println("Saldo insuficiente ou valor inválido!");
                                    }
                                } catch (Exception e) {
                                    System.out.println(e.getMessage());
                                }
                            }

                            case 3 -> {

                                Conta conta = banco.buscarConta(numeroContaCorrente);

                                if (conta == null) {
                                    System.out.println("Conta não encontrada!");
                                    break;
                                }
                                try(Session session = HibernateUtil.fcCliente.openSession()) {
                                    Conta contaConsulta = session.find(Conta.class, numeroContaCorrente);
                                    System.out.printf("Saldo: R$ %.2f\n", contaConsulta.getSaldo());
                                } catch (Exception e) {
                                    System.out.println(e.getMessage());
                                }
                            }

                            case 4 -> {

                                System.out.print("Conta destino: ");
                                int destino = Integer.parseInt(scanner.nextLine());

                                System.out.print("Valor: ");
                                BigDecimal valor = new BigDecimal(scanner.nextLine());

                                System.out.println("Selecione a forma de transferencia:");
                                System.out.println("""
                                1 - PIX
                                2 - TED
                                3 - TEF
                                4 - DOC
                                """);
                                int tipoInput = Integer.parseInt(scanner.nextLine());
                                TipoMovimentacao tipo = switch (tipoInput) {
                                    case 1 -> TipoMovimentacao.PIX;
                                    case 2 -> TipoMovimentacao.TED;
                                    case 3 -> TipoMovimentacao.TEF;
                                    case 4 -> TipoMovimentacao.DOC;
                                    default -> throw new IllegalArgumentException("Tipo inválido");
                                };
                                if (banco.transferir(numeroContaCorrente, destino, valor, tipo)) {

                                    System.out.println("Transferência realizada!");
                                } else {
                                    System.out.println("Erro na transferência!");
                                }

                            }

                            case 5 -> {
                                var contas = banco.listarContas(CPF);

                                if (contas.isEmpty()) {
                                    System.out.println("Nenhuma conta cadastrada.");
                                    break;
                                }

                                for (Conta c : contas) {
                                    System.out.printf(
                                            "Conta: %d | Saldo: R$ %.2f | Tipo: %s\n",
                                            c.getNumero(),
                                            c.getSaldo(),
                                            c.getTipoConta()
                                    );
                                }
                            }

                            case 6 -> acesso = false;
                        }
                    }
                }

                case 3 -> {
                    rodando = false;
                    System.out.println("Encerrando sistema...");
                }

                default -> System.out.println("Opção inválida!");
            }
        }
    }
}