package medicontigo.backend.seguridad;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

// HU-18: lista de tokens cerrados (logout). Cada entrada se descarta al vencer su token.
@Service
public class TokenRevocadoService {

    private final Map<String, Long> revocados = new ConcurrentHashMap<>();

    public void revocar(String jti, Date expiracion) {
        if (jti == null) {
            return;
        }
        limpiarVencidos();
        long vence = expiracion == null ? Long.MAX_VALUE : expiracion.getTime();
        revocados.put(jti, vence);
    }

    public boolean estaRevocado(String jti) {
        return jti != null && revocados.containsKey(jti);
    }

    private void limpiarVencidos() {
        long ahora = System.currentTimeMillis();
        revocados.values().removeIf(vence -> vence < ahora);
    }
}
