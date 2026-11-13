
package medicontigo.backend.servicio;

import medicontigo.backend.modelo.CentroMedico;
import medicontigo.backend.modelo.Paciente;
import medicontigo.backend.modelo.Resena;
import medicontigo.backend.repositorio.CentroMedicoRepository;
import medicontigo.backend.repositorio.PacienteRepository;
import medicontigo.backend.repositorio.ResenaRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ResenaService {

    private final ResenaRepository resenaRepository;
    private final CentroMedicoRepository centroRepository;
    private final PacienteRepository pacienteRepository;
    private final FiltroContenidoService filtroContenido;

    public ResenaService(
            ResenaRepository resenaRepository,
            CentroMedicoRepository centroRepository,
            PacienteRepository pacienteRepository,
            FiltroContenidoService filtroContenido) {

        this.resenaRepository = resenaRepository;
        this.centroRepository = centroRepository;
        this.pacienteRepository = pacienteRepository;
        this.filtroContenido = filtroContenido;
    }

    // Registrar una nueva reseña (HU-18: solo pacientes autenticados)
    @Transactional
    public Resena registrarResena(
            Integer centroId,
            Long pacienteId,
            Integer puntuacion,
            String comentario) {

        Paciente paciente = pacienteId == null ? null
            : pacienteRepository.findById(pacienteId).orElse(null);
        if (paciente == null) {
            throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Debes iniciar sesión para publicar una reseña"
            );
        }
        String nombrePaciente = paciente.getNombreCompleto();

        // Validar el centro
        if (centroId == null || centroId <= 0) {
            throw new IllegalArgumentException(
                "El centro médico no es válido"
            );
        }

        // Validar la puntuación
        if (puntuacion == null ||
                puntuacion < 1 || puntuacion > 5) {
            throw new IllegalArgumentException(
                "La calificación debe ser de 1 a 5 estrellas"
            );
        }

        // Validar nombre
        if (nombrePaciente == null ||
                nombrePaciente.trim().length() < 2 ||
                nombrePaciente.trim().length() > 80) {
            throw new IllegalArgumentException(
                "El nombre debe tener entre 2 y 80 caracteres"
            );
        }

        // Validar comentario
        if (comentario == null ||
                comentario.trim().length() < 10 ||
                comentario.trim().length() > 1000) {
            throw new IllegalArgumentException(
                "El comentario debe tener entre 10 y 1000 caracteres"
            );
        }

        // Buscar el centro médico
        CentroMedico centro = centroRepository
                .findById(centroId)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Centro médico no encontrado"
                ));

        // Crear la reseña
        Resena resena = new Resena();

        resena.setCentro(centro);
        resena.setPaciente(paciente);
        resena.setNombrePaciente(nombrePaciente.trim());
        resena.setPuntuacion(puntuacion);
        resena.setComentario(comentario.trim());

        // HU-14: el filtro de contenido marca alertas para el moderador
        resena.setAlertas(filtroContenido.analizar(comentario));

        // Guardar en la base de datos
        return resenaRepository.save(resena);
    }

    // Consultar únicamente las reseñas publicadas
    @Transactional(readOnly = true)
    public List<Resena> obtenerResenasPublicadas(
            Integer centroId) {

        if (centroId == null ||
                !centroRepository.existsById(centroId)) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Centro médico no encontrado"
            );
        }

        return resenaRepository
                .findByCentro_IdAndEstadoOrderByFechaCreacionDesc(
                    centroId,
                    Resena.PUBLICADA
                );
    }

    // HU-18: reseñas publicadas por el paciente autenticado (cualquier estado)
    @Transactional(readOnly = true)
    public List<Resena> obtenerResenasDelPaciente(Long pacienteId) {
        return resenaRepository.buscarDelPaciente(pacienteId);
    }
}
