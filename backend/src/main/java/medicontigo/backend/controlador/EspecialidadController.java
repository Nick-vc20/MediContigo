package medicontigo.backend.controlador;

import medicontigo.backend.dto.*;
import medicontigo.backend.servicio.EspecialidadService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/centros/{centroId}/especialidades")
public class EspecialidadController {
    private final EspecialidadService servicio;

    public EspecialidadController(EspecialidadService servicio) { this.servicio = servicio; }

    @GetMapping
    public List<EspecialidadResponse> listar(@PathVariable int centroId,
                                             @RequestParam(defaultValue = "false") boolean incluirInactivas) {
        return servicio.listar(centroId, incluirInactivas);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EspecialidadResponse crear(@PathVariable int centroId, @RequestBody EspecialidadRequest solicitud) {
        return servicio.crear(centroId, solicitud);
    }

    @PutMapping("/{id}")
    public EspecialidadResponse actualizar(@PathVariable int centroId, @PathVariable int id,
                                           @RequestBody EspecialidadRequest solicitud) {
        return servicio.actualizar(centroId, id, solicitud);
    }

    @PatchMapping("/{id}/estado")
    public EspecialidadResponse cambiarEstado(@PathVariable int centroId, @PathVariable int id,
                                              @RequestParam boolean activo) {
        return servicio.cambiarEstado(centroId, id, activo);
    }

    @GetMapping("/{id}/horarios")
    public List<HorarioResponse> listarHorarios(@PathVariable int centroId, @PathVariable int id) {
        return servicio.listarHorarios(centroId, id);
    }

    @PostMapping("/{id}/horarios")
    @ResponseStatus(HttpStatus.CREATED)
    public HorarioResponse agregarHorario(@PathVariable int centroId, @PathVariable int id,
                                          @RequestBody HorarioRequest solicitud) {
        return servicio.agregarHorario(centroId, id, solicitud);
    }

    @DeleteMapping("/{id}/horarios/{horarioId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarHorario(@PathVariable int centroId, @PathVariable int id, @PathVariable int horarioId) {
        servicio.eliminarHorario(centroId, id, horarioId);
    }
}