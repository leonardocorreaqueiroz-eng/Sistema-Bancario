package servicos;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import modelos.Conta;
import servicos.TipoMovimentacao;

@Entity
public class Extrato {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Embedded
    private HoraData dataHora;
    private double valor;

    @ManyToOne
    @JoinColumn(name = "conta_origem_numero")
    private Conta contaOrigem;
    @ManyToOne
    @JoinColumn(name = "conta_destino_numero")

    private Conta contaDestino;

    private TipoMovimentacao tipo;

    public Extrato() {}

    public Extrato(HoraData dataHora, double valor, Conta contaOrigem, Conta contaDestino) {
        this.dataHora = dataHora;
        this.valor = valor;
        this.contaOrigem = contaOrigem;
        this.contaDestino = contaDestino;
    }

    public HoraData getDataHora() {
        return dataHora;
    }

    public double getValor() {
        return valor;
    }

    void setValor(Double rendimento) {
        valor = rendimento;
    }

    public void setTipo(TipoMovimentacao tipo) {
        this.tipo = tipo;
    }
}