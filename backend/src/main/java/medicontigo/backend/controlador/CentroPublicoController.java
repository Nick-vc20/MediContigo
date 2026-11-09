package medicontigo.backend.controlador;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import medicontigo.backend.dto.PerfilCentroResponse;
import medicontigo.backend.servicio.CentroPublicoService;

// HU-06: consulta publica (sin login) del perfil completo de un centro.
// El CORS se configura en CorsConfig para todos los /api/**.
@RestController
@RequestMapping("/api/publico/centros")
public class CentroPublicoController {

    private final CentroPublicoService servicio;

    public CentroPublicoController(CentroPublicoService servicio) {
        this.servicio = servicio;
    }

    // Si el centro no existe el servicio responde 404
    @GetMapping("/{id}")
    public PerfilCentroResponse obtenerPerfil(@PathVariable int id) {
        return servicio.obtenerPerfil(id);
    }
}
