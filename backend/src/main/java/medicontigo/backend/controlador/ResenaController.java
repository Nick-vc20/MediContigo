package medicontigo.backend.controlador;

import medicontigo.backend.dto.ResenaResponse;
import medicontigo.backend.servicio.ResenaService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

// HU-13: consulta pública de reseñas publicadas.
// La publicación de reseñas es privada del paciente (HU-18): POST /api/paciente/centros/{centroId}/resenas
@RestController
@RequestMapping("/api/publico/centros/{centroId}/resenas")
public class ResenaController {

    private final ResenaService resenaService;

    public ResenaController(ResenaService resenaService) {
        this.resenaService = resenaService;
    }

    // Consultar reseñas publicadas
    @GetMapping
    public List<ResenaResponse> listar(
            @PathVariable Integer centroId) {

        return resenaService
            .obtenerResenasPublicadas(centroId)
            .stream()
            .map(ResenaResponse::desde)
            .toList();
    }
}
