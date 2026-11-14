package medicontigo.backend.controlador;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import medicontigo.backend.dto.TriajeRequest;
import medicontigo.backend.servicio.TriajeService;

import java.util.Map;

// HU-11: recepción y validación del cuestionario de síntomas
@RestController
@RequestMapping("/api/triaje")
@CrossOrigin(origins = "http://localhost:5173")
public class TriajeController {

    private final TriajeService triajeService;

    public TriajeController(TriajeService triajeService) {
        this.triajeService = triajeService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> validarCuestionario(
            @RequestBody TriajeRequest solicitud) {

        triajeService.validarRespuestas(solicitud);

        return ResponseEntity.status(HttpStatus.OK).body(
                Map.of(
                        "estado", "VALIDADO",
                        "mensaje", "Las respuestas del cuestionario son validas"
                )
        );
    }
}
