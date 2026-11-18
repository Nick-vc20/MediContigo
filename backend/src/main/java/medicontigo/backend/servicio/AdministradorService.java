
package medicontigo.backend.servicio;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import medicontigo.backend.modelo.Administrador;
import medicontigo.backend.repositorio.AdministradorRepository;

@Service
public class AdministradorService {

    @Autowired
    private AdministradorRepository administradorRepository;

    @Autowired
    private Environment environment;

    private final BCryptPasswordEncoder cifrador =
            new BCryptPasswordEncoder();

    public boolean validarLogin(String correo, String password) {
        Optional<Administrador> adminOpt =
                administradorRepository.findByCorreo(correo);

        if (adminOpt.isEmpty()) {
            return false;
        }

        Administrador admin = adminOpt.get();

        if (environment.acceptsProfiles(Profiles.of("prod"))) {
            return cifrador.matches(password, admin.getPassword());
        }

        return admin.getPassword().equals(password);
    }
}