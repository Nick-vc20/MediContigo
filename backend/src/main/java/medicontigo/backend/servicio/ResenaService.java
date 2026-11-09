
package medicontigo.backend.servicio;

import medicontigo.backend.modelo.CentroMedico;
import medicontigo.backend.modelo.Resena;
import medicontigo.backend.repositorio.CentroMedicoRepository;
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

    public ResenaService(
            ResenaRepository resenaRepository,
            CentroMedicoRepository centroRepository) {

        this.resenaRepository = resenaRepository;
        this.centroRepository = centroRepository;
    }

    // Registrar una nueva reseña
    @Transactional
    public Resena registrarResena(
            Integer centroId,
            String nombrePaciente,
            Integer puntuacion,
            String comentario) {

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
        resena.setNombrePaciente(nombrePaciente.trim());
        resena.setPuntuacion(puntuacion);
        resena.setComentario(comentario.trim());

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
                    "PUBLICADA"
                );
    }
}
