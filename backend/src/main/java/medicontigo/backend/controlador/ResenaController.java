
package medicontigo.backend.controlador;

import medicontigo.backend.dto.CrearResenaRequest;
import medicontigo.backend.dto.ResenaResponse;
import medicontigo.backend.modelo.Resena;
import medicontigo.backend.servicio.ResenaService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/publico/centros/{centroId}/resenas")
public class ResenaController {

    private final ResenaService resenaService;

    public ResenaController(ResenaService resenaService) {
        this.resenaService = resenaService;
    }

    // HU-13: Registrar una reseña
    @PostMapping
    public ResponseEntity<ResenaResponse> registrar(
            @PathVariable Integer centroId,
            @RequestBody CrearResenaRequest request) {

        if (request == null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Debes ingresar los datos de la reseña"
            );
        }

        try {
            Resena resena = resenaService.registrarResena(
                centroId,
                request.nombrePaciente(),
                request.puntuacion(),
                request.comentario()
            );

            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResenaResponse.desde(resena));

        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                e.getMessage()
            );
        }
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
