package medicontigo.backend.servicio;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import medicontigo.backend.dto.PerfilCentroResponse;
import medicontigo.backend.dto.PerfilCentroResponse.EspecialidadPerfil;
import medicontigo.backend.dto.PerfilCentroResponse.HorarioPerfil;
import medicontigo.backend.dto.PerfilCentroResponse.MedicoPerfil;
import medicontigo.backend.modelo.CentroMedico;
import medicontigo.backend.modelo.HorarioEspecialidad;
import medicontigo.backend.modelo.Medico;
import medicontigo.backend.repositorio.CentroMedicoRepository;
import medicontigo.backend.repositorio.EspecialidadRepository;
import medicontigo.backend.repositorio.HorarioEspecialidadRepository;
import medicontigo.backend.repositorio.MedicoRepository;

@Service
public class CentroPublicoService {

    private final CentroMedicoRepository centros;
    private final EspecialidadRepository especialidades;
    private final HorarioEspecialidadRepository horarios;
    private final MedicoRepository medicos;

    public CentroPublicoService(CentroMedicoRepository centros, EspecialidadRepository especialidades,
                                HorarioEspecialidadRepository horarios, MedicoRepository medicos) {
        this.centros = centros;
        this.especialidades = especialidades;
        this.horarios = horarios;
        this.medicos = medicos;
    }

    @Transactional(readOnly = true)
    public PerfilCentroResponse obtenerPerfil(int centroId) {
        CentroMedico centro = centros.findById(centroId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Centro no encontrado"));

        // Se consultan los horarios y los medicos del centro una sola vez y se agrupan por especialidad
        Map<Integer, List<HorarioPerfil>> horariosPorEspecialidad = horarios
                .findByEspecialidadCentroIdOrderByDiaSemanaAscHoraInicioAsc(centroId).stream()
                .collect(Collectors.groupingBy(h -> h.getEspecialidad().getId(),
                        Collectors.mapping(this::aHorario, Collectors.toList())));

        Map<Integer, List<MedicoPerfil>> medicosPorEspecialidad = medicos
                .findByCentroIdAndActivoTrue(centroId).stream()
                .collect(Collectors.groupingBy(m -> m.getEspecialidad().getId(),
                        Collectors.mapping(this::aMedico, Collectors.toList())));
        List<EspecialidadPerfil> listaEspecialidades = especialidades
                .findByCentroIdAndEstadoTrueOrderByNombreAsc(centroId).stream()
                .map(e -> new EspecialidadPerfil(e.getId(), e.getNombre(), e.getDescripcion(),
                        horariosPorEspecialidad.getOrDefault(e.getId(), List.of()),
                        medicosPorEspecialidad.getOrDefault(e.getId(), List.of())))
                .toList();

        return new PerfilCentroResponse(centro.getId(), centro.getNombre(), centro.getTipo(),
                centro.getDireccion(), centro.getTelefono(), centro.getLatitud(), centro.getLongitud(),
                centro.getCostoConsulta(), centro.getTiempoEspera(), centro.getNivelOcupacion(),
                java.util.List.copyOf(listaEspecialidades));
    }

    private HorarioPerfil aHorario(HorarioEspecialidad h) {
        return new HorarioPerfil(h.getDiaSemana(), h.getHoraInicio(), h.getHoraFin());
    }

    private MedicoPerfil aMedico(Medico m) {
        return new MedicoPerfil(m.getNombreCompleto(), m.getCmp());
    }
}
