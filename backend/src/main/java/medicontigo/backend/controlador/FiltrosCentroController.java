
package medicontigo.backend.controlador;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import medicontigo.backend.servicio.FiltrosCentroService;

@RestController
@RequestMapping("/api/centros/filtrados")
public class FiltrosCentroController {

    private final FiltrosCentroService servicio;

    public FiltrosCentroController(FiltrosCentroService servicio) {
        this.servicio = servicio;
    }

    // Buscar centros aplicando los filtros
    @GetMapping
    public ResponseEntity<?> filtrar(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "5") double radio,
            @RequestParam(required = false) String especialidad,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) Double maxCosto,
            @RequestParam(defaultValue = "false") boolean hoy) {

        try {
            return ResponseEntity.ok(
                servicio.buscar(
                    lat,
                    lng,
                    radio,
                    especialidad,
                    tipo,
                    maxCosto,
                    hoy
                )
            );

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                .body(Map.of("mensaje", ex.getMessage()));
        }
    }

    // Obtener las opciones disponibles para los filtros
    @GetMapping("/opciones")
    public ResponseEntity<?> opciones(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "5") double radio) {

        try {
            return ResponseEntity.ok(
                servicio.opciones(lat, lng, radio)
            );

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest()
                .body(Map.of("mensaje", ex.getMessage()));
        }
    }
}
