package medicontigo.backend.servicio;

import medicontigo.backend.dto.MedicoRequest;
import medicontigo.backend.dto.MedicoResponse;
import medicontigo.backend.modelo.Medico;
import medicontigo.backend.modelo.Especialidad;
import medicontigo.backend.repositorio.CentroMedicoRepository;
import medicontigo.backend.repositorio.EspecialidadRepository;
import medicontigo.backend.repositorio.MedicoRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class MedicoService {
    private final MedicoRepository medicos;
    private final CentroMedicoRepository centros;
    private final EspecialidadRepository especialidades;

    public MedicoService(MedicoRepository medicos, CentroMedicoRepository centros,
                         EspecialidadRepository especialidades) {
        this.medicos = medicos;
        this.centros = centros;
        this.especialidades = especialidades;
    }

    @Transactional(readOnly = true)
    public List<MedicoResponse> listar(int centroId, boolean incluirInactivos) {
        verificarCentro(centroId);
        var encontrados = incluirInactivos
                ? medicos.findByCentroId(centroId)
                : medicos.findByCentroIdAndActivoTrue(centroId);
        return encontrados.stream().map(MedicoResponse::desde).toList();
    }

    @Transactional
    public MedicoResponse registrar(int centroId, MedicoRequest solicitud) {
        validar(solicitud);
        String cmp = solicitud.cmp().trim();
        if (medicos.existsByCmp(cmp)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El código CMP ingresado ya está registrado en la plataforma");
        }
        var centro = centros.findById(centroId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Centro no encontrado"));
        var especialidad = especialidades.findById(solicitud.especialidadId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Especialidad no encontrada"));
        validarEspecialidad(especialidad, centroId);
        Medico medico = new Medico();
        medico.setNombreCompleto(solicitud.nombreCompleto().trim());
        medico.setCmp(cmp);
        medico.setCentro(centro);
        medico.setEspecialidad(especialidad);
        try {
            return MedicoResponse.desde(medicos.saveAndFlush(medico));
        } catch (DataIntegrityViolationException error) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El CMP ya está registrado", error);
        }
    }

    @Transactional
    public MedicoResponse actualizar(int centroId, int medicoId, MedicoRequest solicitud) {
        validar(solicitud);
        Medico medico = buscar(centroId, medicoId);
        String cmp = solicitud.cmp().trim();
        if (medicos.existsByCmpAndIdNot(cmp, medicoId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El código CMP ingresado ya está registrado en la plataforma");
        }
        var especialidad = especialidades.findById(solicitud.especialidadId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Especialidad no encontrada"));
        validarEspecialidad(especialidad, centroId);
        medico.setNombreCompleto(solicitud.nombreCompleto().trim());
        medico.setCmp(cmp);
        medico.setEspecialidad(especialidad);
        try {
            return MedicoResponse.desde(medicos.saveAndFlush(medico));
        } catch (DataIntegrityViolationException error) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El CMP ya está registrado", error);
        }
    }

    @Transactional
    public void desactivar(int centroId, int medicoId) {
        Medico medico = buscar(centroId, medicoId);
        medico.setActivo(false); // Borrado lógico: NO se elimina la fila.
        medicos.save(medico);
    }

    private Medico buscar(int centroId, int medicoId) {
        Medico medico = medicos.findById(medicoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Médico no encontrado"));
        if (medico.getCentro().getId() != centroId) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El médico no pertenece al centro");
        }
        return medico;
    }

    private void verificarCentro(int centroId) {
        if (!centros.existsById(centroId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Centro no encontrado");
        }
    }

    private void validarEspecialidad(Especialidad especialidad, int centroId) {
       if (especialidad.getCentro().getId() != centroId) {
           throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La especialidad no pertenece a este centro");
       }
       if (!especialidad.isEstado()) {
           throw new ResponseStatusException(HttpStatus.CONFLICT, "La especialidad está inactiva");
       }
   }

    private void validar(MedicoRequest solicitud) {
        if (solicitud == null || solicitud.nombreCompleto() == null || solicitud.nombreCompleto().isBlank()
                || solicitud.cmp() == null || solicitud.cmp().isBlank()
                || solicitud.especialidadId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nombre, CMP y especialidad son obligatorios");
        }
        if (solicitud.nombreCompleto().trim().length() > 150 || solicitud.cmp().trim().length() > 30) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nombre o CMP demasiado largo");
        }
    }
}
