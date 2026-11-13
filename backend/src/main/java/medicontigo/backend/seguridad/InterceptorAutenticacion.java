package medicontigo.backend.seguridad;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * HU-18 (18.2 / 18.3): exige un token JWT válido con el rol indicado.
 * - Sin token, token vencido, alterado o cerrado → 401
 * - Token válido pero de otro rol (acceso cruzado)  → 403
 */
public class InterceptorAutenticacion implements HandlerInterceptor {

    public static final String ATRIBUTO_CLAIMS = "medicontigo.claims";

    private final JwtService jwtService;
    private final Rol rolRequerido;

    public InterceptorAutenticacion(JwtService jwtService, Rol rolRequerido) {
        this.jwtService = jwtService;
        this.rolRequerido = rolRequerido;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {

        // Las peticiones preflight de CORS no llevan token
        if (CorsUtils.isPreFlightRequest(request)) {
            return true;
        }

        String cabecera = request.getHeader("Authorization");
        if (cabecera == null || !cabecera.startsWith("Bearer ")) {
            return rechazar(response, HttpStatus.UNAUTHORIZED, "Debes iniciar sesión para continuar");
        }

        try {
            Claims claims = jwtService.validar(cabecera.substring(7).trim());
            if (jwtService.rolDe(claims) != rolRequerido) {
                return rechazar(response, HttpStatus.FORBIDDEN,
                    "No tienes permisos para acceder a este recurso");
            }
            request.setAttribute(ATRIBUTO_CLAIMS, claims);
            return true;
        } catch (TokenInvalidoException e) {
            return rechazar(response, HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    private boolean rechazar(HttpServletResponse response, HttpStatus estado, String mensaje)
            throws IOException {
        response.setStatus(estado.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(
            "{\"status\":" + estado.value() + ",\"message\":\"" + mensaje.replace("\"", "'") + "\"}");
        return false;
    }
}
