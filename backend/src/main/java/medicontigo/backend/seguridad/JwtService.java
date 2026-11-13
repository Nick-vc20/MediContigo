package medicontigo.backend.seguridad;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

// HU-18 (18.2): emisión y validación de tokens JWT con rol (ADMIN o PACIENTE)
@Service
public class JwtService {

    public static final String CLAIM_ROL = "rol";

    private final byte[] clave;
    private final long minutosAdmin;
    private final long minutosPaciente;
    private final TokenRevocadoService revocados;

    public JwtService(
            @Value("${jwt.secreto}") String secreto,
            @Value("${jwt.expiracion-admin-minutos:30}") long minutosAdmin,
            @Value("${jwt.expiracion-paciente-minutos:60}") long minutosPaciente,
            TokenRevocadoService revocados) {

        if (secreto == null || secreto.length() < 32) {
            throw new IllegalStateException("jwt.secreto debe tener al menos 32 caracteres");
        }
        this.clave = secreto.getBytes(StandardCharsets.UTF_8);
        this.minutosAdmin = minutosAdmin;
        this.minutosPaciente = minutosPaciente;
        this.revocados = revocados;
    }

    public String generarTokenAdmin(String correo) {
        return generar(correo, Rol.ADMIN, Map.of(), minutosAdmin * 60_000);
    }

    public String generarTokenPaciente(Long pacienteId, String correo) {
        return generar(String.valueOf(pacienteId), Rol.PACIENTE,
            Map.of("correo", correo), minutosPaciente * 60_000);
    }

    // Visible para pruebas: permite emitir tokens con cualquier duración (incluso ya vencidos)
    String generar(String sujeto, Rol rol, Map<String, Object> extras, long duracionMs) {
        long ahora = System.currentTimeMillis();
        return Jwts.builder()
            .addClaims(extras)
            .setId(UUID.randomUUID().toString())
            .setSubject(sujeto)
            .claim(CLAIM_ROL, rol.name())
            .setIssuedAt(new Date(ahora))
            .setExpiration(new Date(ahora + duracionMs))
            .signWith(SignatureAlgorithm.HS256, clave)
            .compact();
    }

    /**
     * Valida firma, vencimiento y revocación (cierre de sesión).
     * Lanza {@link TokenInvalidoException} con un mensaje apto para el usuario.
     */
    public Claims validar(String token) {
        Claims claims;
        try {
            claims = Jwts.parser().setSigningKey(clave).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            throw new TokenInvalidoException("Tu sesión ha expirado. Inicia sesión nuevamente");
        } catch (JwtException | IllegalArgumentException e) {
            throw new TokenInvalidoException("Sesión no válida. Inicia sesión nuevamente");
        }

        if (revocados.estaRevocado(claims.getId())) {
            throw new TokenInvalidoException("La sesión fue cerrada. Inicia sesión nuevamente");
        }
        return claims;
    }

    public Rol rolDe(Claims claims) {
        try {
            return Rol.valueOf(String.valueOf(claims.get(CLAIM_ROL)));
        } catch (IllegalArgumentException e) {
            throw new TokenInvalidoException("Sesión no válida. Inicia sesión nuevamente");
        }
    }

    // Cierre de sesión: el token queda inutilizable aunque aún no haya vencido
    public void revocar(Claims claims) {
        revocados.revocar(claims.getId(), claims.getExpiration());
    }

    public long getMinutosPaciente() {
        return minutosPaciente;
    }
}
