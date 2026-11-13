package medicontigo.backend.controlador;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import io.jsonwebtoken.Claims;
import medicontigo.backend.dto.ModerarResenaRequest;
import medicontigo.backend.dto.ResenaAdminResponse;
import medicontigo.backend.dto.ResumenModeracionResponse;
import medicontigo.backend.seguridad.InterceptorAutenticacion;
import medicontigo.backend.servicio.ModeracionResenaService;

/**
 * HU-14 (14.2): API de auditoría y moderación de reseñas.
 * Protegida por el interceptor de rol ADMIN (/api/admin/**, ver SeguridadConfig).
 */
@RestController
@RequestMapping("/api/admin/resenas")
public class ModeracionResenaController {

    private final ModeracionResenaService moderacionService;

    public ModeracionResenaController(ModeracionResenaService moderacionService) {
        this.moderacionService = moderacionService;
    }

    // Listado con filtros opcionales: ?estado=PENDIENTE&centroId=1&conAlertas=true
    @GetMapping
    public List<ResenaAdminResponse> listar(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Integer centroId,
            @RequestParam(defaultValue = "false") boolean conAlertas) {

        return moderacionService.listar(estado, centroId, conAlertas).stream()
            .map(ResenaAdminResponse::desde)
            .toList();
    }

    @GetMapping("/resumen")
    public ResumenModeracionResponse resumen() {
        return moderacionService.resumen();
    }

    // Aprobar, rechazar u ocultar una reseña. El administrador se toma del token.
    @PatchMapping("/{id}/moderacion")
    public ResenaAdminResponse moderar(
            @RequestAttribute(InterceptorAutenticacion.ATRIBUTO_CLAIMS) Claims claims,
            @PathVariable Long id,
            @RequestBody(required = false) ModerarResenaRequest request) {

        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debes indicar la acción de moderación");
        }
        var resena = moderacionService.moderar(
            id,
            request.accion(),
            request.motivo(),
            Boolean.TRUE.equals(request.confirmarAlertas()),
            claims.getSubject());
        return ResenaAdminResponse.desde(resena);
    }
}
