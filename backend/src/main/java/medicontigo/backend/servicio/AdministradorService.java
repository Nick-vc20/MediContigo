package medicontigo.backend.servicio;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import medicontigo.backend.modelo.Administrador;
import medicontigo.backend.modelo.CentroMedico;
import medicontigo.backend.repositorio.AdministradorRepository;
import medicontigo.backend.repositorio.CentroMedicoRepository;

@Service
public class AdministradorService {

    @Autowired
    private AdministradorRepository administradorRepository;

    @Autowired
    private CentroMedicoRepository centroMedicoRepository;

    public boolean validarLogin(String correo, String password) {
        Optional<Administrador> adminOpt = administradorRepository.findByCorreo(correo);
        if (adminOpt.isPresent()) {
            Administrador admin = adminOpt.get();
            return admin.getPassword().equals(password);
        }
        return false;
    }

    public CentroMedico actualizarOcupacion(Integer id, String nivel) {
        CentroMedico centro = centroMedicoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Centro no encontrado"));
        centro.setNivelOcupacion(nivel);
        return centroMedicoRepository.save(centro);
    }
}