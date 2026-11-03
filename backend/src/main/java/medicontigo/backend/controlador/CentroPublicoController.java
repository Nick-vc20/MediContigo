package medicontigo.backend.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import medicontigo.backend.dto.PerfilCentroDTO;
import medicontigo.backend.servicio.CentroPublicoService;

@RestController
@RequestMapping("/api/publico/centros")
@CrossOrigin(origins = "*")
public class CentroPublicoController {

    @Autowired
    private CentroPublicoService servicio;

    @GetMapping("/{id}")
    public ResponseEntity<PerfilCentroDTO> obtenerPerfilConsolidado(@PathVariable int id) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}