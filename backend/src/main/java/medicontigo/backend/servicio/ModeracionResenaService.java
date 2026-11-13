package medicontigo.backend.servicio;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import medicontigo.backend.dto.ResumenModeracionResponse;
import medicontigo.backend.modelo.Resena;
import medicontigo.backend.repositorio.CentroMedicoRepository;
import medicontigo.backend.repositorio.ResenaRepository;

/**
 * HU-14: auditoría y moderación de reseñas por el administrador.
 *
 * Transiciones permitidas:
 *   PENDIENTE  --APROBAR--> PUBLICADA
 *   PENDIENTE  --RECHAZAR-> RECHAZADA
 *   PUBLICADA  --OCULTAR--> OCULTA
 *   OCULTA     --APROBAR--> PUBLICADA   (restaurar)
 *   RECHAZADA  --APROBAR--> PUBLICADA   (reconsiderar)
 */
@Service
public class ModeracionResenaService {

    public static final String APROBAR = "APROBAR";
    public static final String RECHAZAR = "RECHAZAR";
    public static final String OCULTAR = "OCULTAR";

    private static final Set<String> ESTADOS = Set.of(
        Resena.PENDIENTE, Resena.PUBLICADA, Resena.RECHAZADA, Resena.OCULTA);

    private static final int MOTIVO_MIN = 5;
    private static final int MOTIVO_MAX = 255;

    private final ResenaRepository resenaRepository;
    private final CentroMedicoRepository centroRepository;

    public ModeracionResenaService(ResenaRepository resenaRepository,
                                   CentroMedicoRepository centroRepository) {
        this.resenaRepository = resenaRepository;
        this.centroRepository = centroRepository;
    }

    @Transactional(readOnly = true)
    public List<Resena> listar(String estado, Integer centroId, boolean soloConAlertas) {
        String estadoFiltro = null;
        if (estado != null && !estado.isBlank()) {
            estadoFiltro = estado.trim().toUpperCase(Locale.ROOT);
            if (!ESTADOS.contains(estadoFiltro)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Estado no válido. Usa PENDIENTE, PUBLICADA, RECHAZADA u OCULTA");
            }
        }
        if (centroId != null && !centroRepository.existsById(centroId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Centro médico no encontrado");
        }
        return resenaRepository.buscarParaModeracion(estadoFiltro, centroId, soloConAlertas);
    }

    @Transactional(readOnly = true)
    public ResumenModeracionResponse resumen() {
        Map<String, Long> conteo = new HashMap<>();
        for (Object[] fila : resenaRepository.contarPorEstado()) {
            conteo.put((String) fila[0], ((Number) fila[1]).longValue());
        }
        long pendientes = conteo.getOrDefault(Resena.PENDIENTE, 0L);
        long publicadas = conteo.getOrDefault(Resena.PUBLICADA, 0L);
        long rechazadas = conteo.getOrDefault(Resena.RECHAZADA, 0L);
        long ocultas = conteo.getOrDefault(Resena.OCULTA, 0L);
        return new ResumenModeracionResponse(
            pendientes,
            resenaRepository.countByEstadoAndAlertasContenidoIsNotNull(Resena.PENDIENTE),
            publicadas,
            rechazadas,
            ocultas,
            pendientes + publicadas + rechazadas + ocultas);
    }

    @Transactional
    public Resena moderar(Long resenaId, String accion, String motivo,
                          boolean confirmarAlertas, String administrador) {

        if (accion == null || accion.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Debes indicar la acción: APROBAR, RECHAZAR u OCULTAR");
        }
        String accionNormalizada = accion.trim().toUpperCase(Locale.ROOT);

        Resena resena = resenaRepository.findById(resenaId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reseña no encontrada"));

        String motivoLimpio = motivo == null ? null : motivo.trim();
        if (motivoLimpio != null && motivoLimpio.isEmpty()) {
            motivoLimpio = null;
        }
        if (motivoLimpio != null && motivoLimpio.length() > MOTIVO_MAX) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "El motivo no puede superar los " + MOTIVO_MAX + " caracteres");
        }

        String estadoActual = resena.getEstado();
        String nuevoEstado;

        switch (accionNormalizada) {
            case APROBAR -> {
                if (Resena.PUBLICADA.equals(estadoActual)) {
                    throw conflicto("La reseña ya está publicada");
                }
                if (resena.tieneAlertas() && !confirmarAlertas) {
                    throw conflicto("La reseña tiene alertas del filtro de contenido ("
                        + String.join(", ", resena.getAlertas())
                        + "). Confirma que la revisaste para publicarla");
                }
                nuevoEstado = Resena.PUBLICADA;
            }
            case RECHAZAR -> {
                if (!Resena.PENDIENTE.equals(estadoActual)) {
                    throw conflicto("Solo se pueden rechazar reseñas pendientes de revisión");
                }
                exigirMotivo(motivoLimpio);
                nuevoEstado = Resena.RECHAZADA;
            }
            case OCULTAR -> {
                if (!Resena.PUBLICADA.equals(estadoActual)) {
                    throw conflicto("Solo se pueden ocultar reseñas publicadas");
                }
                exigirMotivo(motivoLimpio);
                nuevoEstado = Resena.OCULTA;
            }
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Acción no válida. Usa APROBAR, RECHAZAR u OCULTAR");
        }

        resena.moderar(nuevoEstado, motivoLimpio, administrador);
        return resenaRepository.save(resena);
    }

    private static void exigirMotivo(String motivo) {
        if (motivo == null || motivo.length() < MOTIVO_MIN) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Indica el motivo (mínimo " + MOTIVO_MIN + " caracteres)");
        }
    }

    private static ResponseStatusException conflicto(String mensaje) {
        return new ResponseStatusException(HttpStatus.CONFLICT, mensaje);
    }
}
