package medicontigo.backend.servicio;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import medicontigo.backend.dto.PerfilCentroDTO;
import medicontigo.backend.modelo.CentroMedico;
import medicontigo.backend.modelo.Especialidad;
import medicontigo.backend.repositorio.CentroMedicoRepository;

@Service
public class CentroPublicoService {

    @Autowired
    private CentroMedicoRepository repository;

    @Transactional(readOnly = true)
    public PerfilCentroDTO obtenerPerfilPorId(int id) {
        CentroMedico centro = repository.findById(id).orElse(null);
        if (centro == null) return null;

        List<String> nombresEspecialidades = new ArrayList<>();
        
        if (centro.getEspecialidades() != null) {
            for (Especialidad esp : centro.getEspecialidades()) {
                if (esp != null && esp.isEstado() && esp.getNombre() != null) {
                    nombresEspecialidades.add(esp.getNombre());
                }
            }
        }

        return new PerfilCentroDTO(
                centro.getNombre(),
                centro.getDireccion(),
                centro.getCostoConsulta(),
                centro.getTiempoEspera(),
                nombresEspecialidades,
                new ArrayList<>() // Horarios si aplica
        );
    }
}