package medicontigo.backend.controlador;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import medicontigo.backend.dto.OcupacionRequest;
import medicontigo.backend.dto.OcupacionResponse;
import medicontigo.backend.servicio.OcupacionService;

/**
 * HU-10 (10.2): endpoint del administrador para actualizar la ocupación de un centro.
 * Queda bajo /api/admin/**, así que SeguridadConfig (HU-18) ya exige un token con rol ADMIN.
 */
@RestController
@RequestMapping("/api/admin/centros")
public class OcupacionController {

    private final OcupacionService servicio;

    public OcupacionController(OcupacionService servicio) {
        this.servicio = servicio;
    }

    // PATCH /api/admin/centros/{id}/ocupacion   body: { "nivel": "BAJO" | "MEDIO" | "ALTO" }
    @PatchMapping("/{id}/ocupacion")
    public OcupacionResponse actualizar(@PathVariable int id, @RequestBody OcupacionRequest solicitud) {
        return servicio.actualizar(id, solicitud.nivel());
    }

    // Mismo formato de error {status, message} que usan los demás endpoints del administrador
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> manejar(ResponseStatusException e) {
        HttpStatusCode estado = e.getStatusCode();
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("status", estado.value());
        cuerpo.put("message", e.getReason());
        return ResponseEntity.status(estado).body(cuerpo);
    }
}
