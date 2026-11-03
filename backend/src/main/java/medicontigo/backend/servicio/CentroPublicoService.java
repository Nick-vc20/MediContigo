package medicontigo.backend.servicio;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import medicontigo.backend.dto.PerfilCentroDTO;
import medicontigo.backend.modelo.CentroEspecialidad;
import medicontigo.backend.modelo.CentroMedico;
import medicontigo.backend.repositorio.CentroMedicoRepository;

@Service
public class CentroPublicoService {

    @Autowired
    private CentroMedicoRepository repository;

    public PerfilCentroDTO obtenerPerfilPorId(int id) {
        CentroMedico centro = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Centro médico no encontrado"));

        List<String> nombresEspecialidades = new ArrayList<>();
        List<String> resumenHorarios = new ArrayList<>();

        List<CentroEspecialidad> especialidades = centro.getEspecialidades();
        
        for (CentroEspecialidad ce : especialidades) {
            nombresEspecialidades.add(ce.getEspecialidad().getNombre());
            
            String horario = ce.getHorarioResumen();
            if (!resumenHorarios.contains(horario)) {
                resumenHorarios.add(horario);
            }
        }

        return new PerfilCentroDTO(
                centro.getNombre(),
                centro.getDireccion(),
                centro.getCostoConsulta(),
                centro.getTiempoEspera(),
                nombresEspecialidades,
                resumenHorarios
        );
    }
}