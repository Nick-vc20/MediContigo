
package medicontigo.backend.controlador;

import medicontigo.backend.modelo.CentroMedico;
import medicontigo.backend.servicio.CentroMedicoService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/centros-medicos")
public class CentroMedicoController {

    private final CentroMedicoService centroMedicoService;

    public CentroMedicoController(CentroMedicoService centroMedicoService) {
        this.centroMedicoService = centroMedicoService;
    }

    @PostMapping
    public ResponseEntity<?> registrarCentroMedico(@RequestBody CentroMedico centro) {
        try {
            CentroMedico registrado = centroMedicoService.registrarCentroMedico(centro);
            return ResponseEntity.status(HttpStatus.CREATED).body(registrado);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }
}
