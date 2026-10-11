package medicontigo.backend.servicio;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import medicontigo.backend.modelo.Administrador;
import medicontigo.backend.repositorio.AdministradorRepository;

@Service
public class AdministradorService {

    @Autowired
    private AdministradorRepository administradorRepository;

    public boolean validarLogin(String correo, String password) {
        Optional<Administrador> adminOpt = administradorRepository.findByCorreo(correo);
        if (adminOpt.isPresent()) {
            Administrador admin = adminOpt.get();
            return admin.getPassword().equals(password);
        }
        return false;
    }
}