package medicontigo.backend.controlador;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import medicontigo.backend.dto.TarifaRequest;
import medicontigo.backend.dto.TarifaResponse;
import medicontigo.backend.servicio.TarifaService;

@RestController
@RequestMapping("/api/centros/{centroId}/tarifas")
public class TarifaController {

    private final TarifaService servicio;

    public TarifaController(TarifaService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<TarifaResponse> listar(@PathVariable int centroId) {
        return servicio.listar(centroId);
    }

    @GetMapping("/{especialidadId}")
    public TarifaResponse obtener(
            @PathVariable int centroId,
            @PathVariable int especialidadId) {

        return servicio.obtener(centroId, especialidadId);
    }

    @PutMapping("/{especialidadId}")
    public TarifaResponse guardar(
            @PathVariable int centroId,
            @PathVariable int especialidadId,
            @RequestBody TarifaRequest solicitud) {

        return servicio.guardar(centroId, especialidadId, solicitud);
    }
}
