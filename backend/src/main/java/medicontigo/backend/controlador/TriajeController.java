package medicontigo.backend.controlador;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import medicontigo.backend.dto.TriajeRequest;
import medicontigo.backend.dto.TriajeResponse;
import medicontigo.backend.servicio.TriajeService;

@RestController
@RequestMapping("/api/triaje")
@CrossOrigin(origins = "http://localhost:5173")
public class TriajeController {

    private final TriajeService triajeService;

    public TriajeController(TriajeService triajeService) {
        this.triajeService = triajeService;
    }

    @PostMapping
    public ResponseEntity<TriajeResponse> evaluarCuestionario(
            @RequestBody TriajeRequest solicitud
    ) {
        TriajeResponse respuesta = triajeService.evaluar(solicitud);
        return ResponseEntity.ok(respuesta);
    }
}
