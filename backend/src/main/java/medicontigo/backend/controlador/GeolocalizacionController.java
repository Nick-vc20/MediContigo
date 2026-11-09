package medicontigo.backend.controlador;

import medicontigo.backend.dto.CentroMapaResponse;
import medicontigo.backend.servicio.GeolocalizacionService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/centros")
public class GeolocalizacionController {

    private final GeolocalizacionService geolocalizacionService;

    public GeolocalizacionController(GeolocalizacionService geolocalizacionService) {
        this.geolocalizacionService = geolocalizacionService;
    }

    // Buscar centros medicos cercanos
    @GetMapping
    public ResponseEntity<?> buscarCentros(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "5") double radio) {

        try {
            List<CentroMapaResponse> centros =
                    geolocalizacionService.buscarCercanos(lat, lng, radio);

            return ResponseEntity.ok(centros);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("mensaje", e.getMessage()));
        }
    }
}
