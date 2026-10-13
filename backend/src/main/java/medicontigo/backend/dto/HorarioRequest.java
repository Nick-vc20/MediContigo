package medicontigo.backend.dto;
import java.time.LocalTime;
public record HorarioRequest(Integer diaSemana, LocalTime horaInicio, LocalTime horaFin) {}
