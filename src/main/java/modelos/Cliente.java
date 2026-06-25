package modelos;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    @Column(unique = true)
    private String cpf;
    private LocalDate dataDeCriacao;
    protected Cliente() {}

    public Cliente(String nome, String cpf, LocalDate dataDeCriacao) {
        this.nome = nome;
        this.cpf = cpf;
        this.dataDeCriacao = dataDeCriacao;
    }

    // getters


    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }
}