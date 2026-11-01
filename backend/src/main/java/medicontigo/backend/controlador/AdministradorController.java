package medicontigo.backend.controlador;

import java.util.Date;
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

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import medicontigo.backend.servicio.AdministradorService;

@CrossOrigin(origins = "http://localhost:5173") 
@RestController
@RequestMapping("/api/admin")
public class AdministradorController {
    
    @Autowired
    private AdministradorService administradorService;

    private final String CLAVE_SECRETA = "MiClaveSecretaParaElProyectoDeIngenieriaDeSoftware123";

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> credenciales) {
        String correo = credenciales.get("correo");
        String password = credenciales.get("password"); 
        
        boolean esValido = administradorService.validarLogin(correo, password);
        
        Map<String, String> respuesta = new HashMap<>();

        if (esValido) {
            String tokenReal = Jwts.builder()
                .setSubject(correo)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30))
                .signWith(SignatureAlgorithm.HS256, CLAVE_SECRETA)
                .compact();

            respuesta.put("token", tokenReal);
            return ResponseEntity.ok(respuesta);
        } else {
            respuesta.put("mensaje", "Credenciales incorrectas");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(respuesta);
        }
    }
}