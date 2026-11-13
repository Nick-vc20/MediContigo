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
import medicontigo.backend.dto.CrearResenaRequest;
import medicontigo.backend.dto.MiResenaResponse;
import medicontigo.backend.dto.PacienteResponse;
import medicontigo.backend.dto.ResenaResponse;
import medicontigo.backend.seguridad.InterceptorAutenticacion;
import medicontigo.backend.seguridad.JwtService;
import medicontigo.backend.servicio.PacienteService;
import medicontigo.backend.servicio.ResenaService;

/**
 * HU-18 (18.3): rutas privadas del paciente. Todas exigen un token con rol PACIENTE
 * (ver SeguridadConfig); el id del paciente se toma del token, nunca del cliente.
 */
@RestController
@RequestMapping("/api/paciente")
public class PacienteController {

    private final PacienteService pacienteService;
    private final ResenaService resenaService;
    private final JwtService jwtService;

    public PacienteController(PacienteService pacienteService, ResenaService resenaService,
                              JwtService jwtService) {
        this.pacienteService = pacienteService;
        this.resenaService = resenaService;
        this.jwtService = jwtService;
    }

    @GetMapping("/perfil")
    public PacienteResponse perfil(@RequestAttribute(InterceptorAutenticacion.ATRIBUTO_CLAIMS) Claims claims) {
        return PacienteResponse.desde(pacienteService.obtener(pacienteId(claims)));
    }

    // Reseñas del propio paciente (incluye las pendientes de moderación)
    @GetMapping("/resenas")
    public List<MiResenaResponse> misResenas(
            @RequestAttribute(InterceptorAutenticacion.ATRIBUTO_CLAIMS) Claims claims) {
        return resenaService.obtenerResenasDelPaciente(pacienteId(claims)).stream()
            .map(MiResenaResponse::desde)
            .toList();
    }

    // HU-13 + HU-18: solo un paciente autenticado puede publicar reseñas
    @PostMapping("/centros/{centroId}/resenas")
    public ResponseEntity<ResenaResponse> publicarResena(
            @RequestAttribute(InterceptorAutenticacion.ATRIBUTO_CLAIMS) Claims claims,
            @PathVariable Integer centroId,
            @RequestBody(required = false) CrearResenaRequest request) {

        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debes ingresar los datos de la reseña");
        }
        try {
            var resena = resenaService.registrarResena(
                centroId, pacienteId(claims), request.puntuacion(), request.comentario());
            return ResponseEntity.status(HttpStatus.CREATED).body(ResenaResponse.desde(resena));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    // HU-18: cierre de sesión. El token queda revocado aunque todavía no haya vencido.
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestAttribute(InterceptorAutenticacion.ATRIBUTO_CLAIMS) Claims claims) {
        jwtService.revocar(claims);
        return ResponseEntity.noContent().build();
    }

    private Long pacienteId(Claims claims) {
        try {
            return Long.valueOf(claims.getSubject());
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sesión no válida");
        }
    }
}
