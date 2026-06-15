package servicos;

import jakarta.persistence.Embeddable;

import java.time.LocalDate;
import java.time.LocalTime;

@Embeddable
public class HoraData {
    private LocalDate data;
    private LocalTime hora;

    public HoraData(){}

    public HoraData(LocalDate data, LocalTime hora) {
        this.data = data;
        this.hora = hora;
    }

    public LocalDate getData() {
        return data;
    }

    public LocalTime getHora() {
        return hora;
    }

}