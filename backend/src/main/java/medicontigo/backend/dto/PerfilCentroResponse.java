package medicontigo.backend.dto;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

public record PerfilCentroResponse(
        int id,
        String nombre,
        String tipo,
        String direccion,
        String telefono,
        BigDecimal latitud,
        BigDecimal longitud,
        double costoConsulta,
        String tiempoEspera,
        String nivelOcupacion,
        List<EspecialidadPerfil> especialidades) {

    public record EspecialidadPerfil(
            int id,
            String nombre,
            String descripcion,
            List<HorarioPerfil> horarios,
            List<MedicoPerfil> medicos) {
    }

    public record HorarioPerfil(int diaSemana, LocalTime horaInicio, LocalTime horaFin) {
    }

    public record MedicoPerfil(String nombreCompleto, String cmp) {
    }
}
