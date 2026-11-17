package medicontigo.backend.servicio;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import medicontigo.backend.dto.OcupacionResponse;
import medicontigo.backend.modelo.CentroMedico;
import medicontigo.backend.modelo.NivelOcupacion;
import medicontigo.backend.repositorio.CentroMedicoRepository;

/**
 * HU-10 (10.2): actualización rápida del nivel de ocupación de un establecimiento.
 *
 * El cambio se confirma en la base de datos antes de responder. El mapa (HU-05) y el perfil
 * público (HU-06) leen ese mismo campo en cada petición, por eso reflejan el nuevo nivel
 * de inmediato, sin caché ni procesos intermedios.
 */
@Service
public class OcupacionService {

    private final CentroMedicoRepository centros;

    public OcupacionService(CentroMedicoRepository centros) {
        this.centros = centros;
    }

    @Transactional
    public OcupacionResponse actualizar(int centroId, String nivelSolicitado) {
        NivelOcupacion nivel = interpretar(nivelSolicitado);

        CentroMedico centro = centros.findById(centroId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Centro no encontrado"));

        centro.setNivelOcupacion(nivel.name());
        centros.saveAndFlush(centro);

        return OcupacionResponse.desde(centro, nivel);
    }

    private NivelOcupacion interpretar(String valor) {
        try {
            return NivelOcupacion.desde(valor);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
