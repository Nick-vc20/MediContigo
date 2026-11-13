package medicontigo.backend.controlador;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

// HU-17 / HU-18 / HU-14: respuesta de error uniforme {status, message} para los endpoints del paciente
// y para la moderación de reseñas del administrador
@RestControllerAdvice(assignableTypes = {
    PacienteAuthController.class, PacienteController.class, ModeracionResenaController.class })
public class PacienteErrores {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> manejar(ResponseStatusException e) {
        HttpStatusCode estado = e.getStatusCode();
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("status", estado.value());
        cuerpo.put("message", e.getReason());
        return ResponseEntity.status(estado).body(cuerpo);
    }
}
