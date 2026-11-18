package medicontigo.backend.controlador;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    private final JdbcTemplate jdbcTemplate;

    public HealthController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/api/health")
    public ResponseEntity<Map<String, String>> health() {
        try {
            Integer resultado = jdbcTemplate.queryForObject(
                "SELECT 1", Integer.class
            );

            if (Integer.valueOf(1).equals(resultado)) {
                return ResponseEntity.ok(Map.of(
                    "status", "UP",
                    "database", "UP"
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(503).body(Map.of(
                "status", "DOWN",
                "database", "DOWN"
            ));
        }

        return ResponseEntity.status(503).body(Map.of(
            "status", "DOWN",
            "database", "DOWN"
        ));
    }
}
