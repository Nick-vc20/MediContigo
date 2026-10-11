package medicontigo.backend.controlador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import medicontigo.backend.servicio.AdministradorService;

@RestController
@RequestMapping("/api/admin")
public class AdministradorController {
    @Autowired
    private AdministradorService administradorService;

    @PostMapping("/login")
    public String login(@RequestParam String correo, @RequestParam String password) {
        boolean esValido = administradorService.validarLogin(correo, password);
        
        if (esValido) {
            return "¡Login exitoso!";
        } else {
            return "Credenciales incorrectas.";
        }
    }
}
