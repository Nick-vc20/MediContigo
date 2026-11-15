package medicontigo.backend.servicio;

import java.util.List;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import medicontigo.backend.dto.GuardarTriajeRequest;
import medicontigo.backend.modelo.Paciente;
import medicontigo.backend.modelo.ResultadoTriaje;
import medicontigo.backend.repositorio.PacienteRepository;
import medicontigo.backend.repositorio.ResultadoTriajeRepository;

// HU-12 (12.3): guardar y listar el historial de resultados de triaje del paciente
@Service
public class HistorialTriajeService {

    private final ResultadoTriajeRepository triajeRepository;
    private final PacienteRepository pacienteRepository;

    public HistorialTriajeService(ResultadoTriajeRepository triajeRepository,
                                  PacienteRepository pacienteRepository) {
        this.triajeRepository = triajeRepository;
        this.pacienteRepository = pacienteRepository;
    }

    @Transactional
    public ResultadoTriaje guardar(Long pacienteId, GuardarTriajeRequest datos) {
        if (datos == null) {
            throw new IllegalArgumentException("Debes enviar el resultado del triaje");
        }

        Paciente paciente = pacienteRepository.findById(pacienteId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "La sesión ya no es válida"));

        String sintomas = limpiar(datos.sintomas());
        if (sintomas.length() < 3 || sintomas.length() > 500) {
            throw new IllegalArgumentException("Los síntomas deben tener entre 3 y 500 caracteres");
        }

        String nivel = limpiar(datos.nivelUrgencia()).toUpperCase(Locale.ROOT);
        if (!ResultadoTriaje.NIVELES_URGENCIA.contains(nivel)) {
            throw new IllegalArgumentException(
                "El nivel de urgencia debe ser BAJA, MEDIA, ALTA o EMERGENCIA");
        }

        String recomendacion = limpiar(datos.recomendacion());
        if (recomendacion.isEmpty() || recomendacion.length() > 1000) {
            throw new IllegalArgumentException("La recomendación debe tener entre 1 y 1000 caracteres");
        }

        String especialidad = limpiar(datos.especialidadRecomendada());
        if (especialidad.length() > 100) {
            throw new IllegalArgumentException("La especialidad no puede superar los 100 caracteres");
        }

        ResultadoTriaje resultado = new ResultadoTriaje();
        resultado.setPaciente(paciente);
        resultado.setSintomas(sintomas);
        resultado.setNivelUrgencia(nivel);
        resultado.setEspecialidadRecomendada(especialidad.isEmpty() ? null : especialidad);
        resultado.setRecomendacion(recomendacion);
        return triajeRepository.save(resultado);
    }

    // Solo los resultados del paciente autenticado, del más reciente al más antiguo
    @Transactional(readOnly = true)
    public List<ResultadoTriaje> listar(Long pacienteId) {
        return triajeRepository.findByPaciente_IdOrderByFechaTriajeDescIdDesc(pacienteId);
    }

    // Detalle de un resultado; si es de otro paciente se responde 404 (no se revela que existe)
    @Transactional(readOnly = true)
    public ResultadoTriaje obtener(Long pacienteId, Long triajeId) {
        return triajeRepository.findByIdAndPaciente_Id(triajeId, pacienteId)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "No se encontró el resultado de triaje"));
    }

    private static String limpiar(String texto) {
        return texto == null ? "" : texto.trim();
    }
}