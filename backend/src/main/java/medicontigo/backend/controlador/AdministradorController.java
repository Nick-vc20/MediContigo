package medicontigo.backend.controlador;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import medicontigo.backend.servicio.AdministradorService;

@CrossOrigin(origins = "http://localhost:5173") 
@RestController
@RequestMapping("/api/admin")
public class AdministradorController {
    
    @Autowired
    private AdministradorService administradorService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> credenciales) {
        String correo = credenciales.get("correo");
        String password = credenciales.get("password"); 
        
        System.out.println("DATOS RECIBIDOS DE REACT -> Correo: " + correo + " | Pass: " + password);
        
        boolean esValido = administradorService.validarLogin(correo, password);
        
        System.out.println("¿SE ENCONTRÓ EN LA BASE DE DATOS? -> " + esValido);
        
        Map<String, String> respuesta = new HashMap<>();
        if (esValido) {
            respuesta.put("token", "token-de-prueba-123");
            return ResponseEntity.ok(respuesta);
        } else {
            respuesta.put("mensaje", "Credenciales incorrectas");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(respuesta);
        }
    }
}