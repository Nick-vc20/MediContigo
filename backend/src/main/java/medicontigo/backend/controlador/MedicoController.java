package medicontigo.backend.controlador;

import medicontigo.backend.dto.MedicoRequest;
import medicontigo.backend.dto.MedicoResponse;
import medicontigo.backend.servicio.MedicoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/centros/{centroId}/medicos")
public class MedicoController {
    private final MedicoService servicio;

    public MedicoController(MedicoService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<MedicoResponse> listar(@PathVariable int centroId,
                                        @RequestParam(defaultValue = "false") boolean incluirInactivos) {
        return servicio.listar(centroId, incluirInactivos);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MedicoResponse registrar(@PathVariable int centroId, @RequestBody MedicoRequest solicitud) {
        return servicio.registrar(centroId, solicitud);
    }

    @PutMapping("/{medicoId}")
    public MedicoResponse actualizar(@PathVariable int centroId, @PathVariable int medicoId,
                                      @RequestBody MedicoRequest solicitud) {
        return servicio.actualizar(centroId, medicoId, solicitud);
    }

    @DeleteMapping("/{medicoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desactivar(@PathVariable int centroId, @PathVariable int medicoId) {
        servicio.desactivar(centroId, medicoId);
    }
}
