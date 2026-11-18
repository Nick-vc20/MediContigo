package medicontigo.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

import medicontigo.backend.controlador.HealthController;

class HealthControllerTest {

    private JdbcTemplate jdbcTemplate;
    private HealthController controlador;

    @BeforeEach
    void preparar() {
        jdbcTemplate = org.mockito.Mockito.mock(JdbcTemplate.class);
        controlador = new HealthController(jdbcTemplate);
    }

    @Test
    void healthDevuelve200CuandoBaseDatosFunciona() {
        when(jdbcTemplate.queryForObject("SELECT 1", Integer.class))
                .thenReturn(1);

        ResponseEntity<?> respuesta = controlador.health();

        assertEquals(200, respuesta.getStatusCode().value());
    }

    @Test
    void healthDevuelve503CuandoBaseDatosFalla() {
        when(jdbcTemplate.queryForObject("SELECT 1", Integer.class))
                .thenThrow(new RuntimeException(new SQLException("Conexion fallida")));

        ResponseEntity<?> respuesta = controlador.health();

        assertEquals(503, respuesta.getStatusCode().value());
    }
}
