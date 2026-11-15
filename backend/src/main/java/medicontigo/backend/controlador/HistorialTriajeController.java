package medicontigo.backend.controlador;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.jsonwebtoken.Claims;
import medicontigo.backend.dto.GuardarTriajeRequest;
import medicontigo.backend.dto.ResultadoTriajeResponse;
import medicontigo.backend.seguridad.InterceptorAutenticacion;
import medicontigo.backend.servicio.HistorialTriajeService;

/**
 * HU-12 (12.3): historial de resultados de triaje del paciente.
 * Ruta privada (/api/paciente/**): exige token con rol PACIENTE (ver SeguridadConfig)
 * y el id del paciente se toma del token, nunca del cliente.
 */
@RestController
@RequestMapping("/api/paciente/triajes")
public class HistorialTriajeController {

    private final HistorialTriajeService historialService;

    public HistorialTriajeController(HistorialTriajeService historialService) {
        this.historialService = historialService;
    }

    // Guardar un resultado de triaje en el historial
    @PostMapping
    public ResponseEntity<ResultadoTriajeResponse> guardar(
            @RequestAttribute(InterceptorAutenticacion.ATRIBUTO_CLAIMS) Claims claims,
            @RequestBody(required = false) GuardarTriajeRequest request) {
        try {
            var resultado = historialService.guardar(pacienteId(claims), request);
            return ResponseEntity.status(HttpStatus.CREATED).body(ResultadoTriajeResponse.desde(resultado));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    // Listar el historial del paciente autenticado
    @GetMapping
    public List<ResultadoTriajeResponse> listar(
            @RequestAttribute(InterceptorAutenticacion.ATRIBUTO_CLAIMS) Claims claims) {
        return historialService.listar(pacienteId(claims)).stream()
            .map(ResultadoTriajeResponse::desde)
            .toList();
    }

    // Detalle de un resultado del historial
    @GetMapping("/{id}")
    public ResultadoTriajeResponse detalle(
            @RequestAttribute(InterceptorAutenticacion.ATRIBUTO_CLAIMS) Claims claims,
            @PathVariable Long id) {
        return ResultadoTriajeResponse.desde(historialService.obtener(pacienteId(claims), id));
    }

    private Long pacienteId(Claims claims) {
        try {
            return Long.valueOf(claims.getSubject());
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sesión no válida");
        }
    }
}