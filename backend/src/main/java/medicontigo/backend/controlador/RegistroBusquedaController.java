
package medicontigo.backend.controlador;

import medicontigo.backend.servicio.RegistroBusquedaService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/eventos/busquedas")
public class RegistroBusquedaController {

    private final RegistroBusquedaService servicio;

    public RegistroBusquedaController(
            RegistroBusquedaService servicio) {
        this.servicio = servicio;
    }


    public record SolicitudBusqueda(
            String especialidad,
            String zona
    ) {}


    @PostMapping
    public ResponseEntity<Void> registrarBusqueda(
            @RequestBody SolicitudBusqueda solicitud) {

        try {
            servicio.registrarBusqueda(
                    solicitud.especialidad(),
                    solicitud.zona()
            );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .build();

        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    e.getMessage()
            );
        }
    }
}
