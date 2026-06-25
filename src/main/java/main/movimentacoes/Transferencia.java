package main.movimentacoes;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import modelos.Conta;
import servicos.HoraData;
import servicos.TipoMovimentacao;

import java.math.BigDecimal;
@Entity
public class Transferencia extends Movimentacao {

    @Embedded
    private HoraData dataHora;
    private BigDecimal valor;

    @ManyToOne
    @JoinColumn(name = "conta_origem_numero")
    private Conta contaOrigem;
    @ManyToOne
    @JoinColumn(name = "conta_destino_numero")

    private Conta contaDestino;
    @Enumerated(EnumType.STRING)
    private TipoMovimentacao tipo;

    public Transferencia() {}

    public Transferencia(HoraData dataHora, BigDecimal valor, Conta contaOrigem, Conta contaDestino) {
        this.dataHora = dataHora;
        this.valor = valor;
        this.contaOrigem = contaOrigem;
        this.contaDestino = contaDestino;
    }

    public HoraData getDataHora() {
        return dataHora;
    }

    public BigDecimal getValor() {
        return valor;
    }

    void setValor(BigDecimal rendimento) {
        valor = rendimento;
    }

    public void setTipo(TipoMovimentacao tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()+"\n"+
                "Conta: " + contaOrigem.getNumero() + " | Valor: " + valor + " | Tipo: " + tipo +
                " | Data: "+dataHora.getData()+" | Hora: " + dataHora.getHora() + "\n";
    }
}
