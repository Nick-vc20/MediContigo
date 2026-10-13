package medicontigo.backend.servicio;

import medicontigo.backend.dto.*;
import medicontigo.backend.modelo.Especialidad;
import medicontigo.backend.modelo.HorarioEspecialidad;
import medicontigo.backend.repositorio.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class EspecialidadService {
    private final EspecialidadRepository especialidades;
    private final HorarioEspecialidadRepository horarios;
    private final CentroMedicoRepository centros;
    private final MedicoRepository medicos;

    public EspecialidadService(EspecialidadRepository especialidades, HorarioEspecialidadRepository horarios,
                               CentroMedicoRepository centros, MedicoRepository medicos) {
        this.especialidades = especialidades;
        this.horarios = horarios;
        this.centros = centros;
        this.medicos = medicos;
    }

    // ---------- Especialidades ----------

    @Transactional(readOnly = true)
    public List<EspecialidadResponse> listar(int centroId, boolean incluirInactivas) {
        verificarCentro(centroId);
        var lista = incluirInactivas
                ? especialidades.findByCentroIdOrderByNombreAsc(centroId)
                : especialidades.findByCentroIdAndEstadoTrueOrderByNombreAsc(centroId);
        return lista.stream().map(EspecialidadResponse::desde).toList();
    }

    @Transactional
    public EspecialidadResponse crear(int centroId, EspecialidadRequest solicitud) {
        validar(solicitud);
        String nombre = solicitud.nombre().trim();
        var centro = centros.findById(centroId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Centro no encontrado"));
        if (especialidades.existsByCentroIdAndNombreIgnoreCase(centroId, nombre)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe esa especialidad en el centro");
        }
        Especialidad e = new Especialidad();
        e.setCentro(centro);
        e.setNombre(nombre);
        e.setDescripcion(limpiar(solicitud.descripcion()));
        return EspecialidadResponse.desde(especialidades.saveAndFlush(e));
    }

    @Transactional
    public EspecialidadResponse actualizar(int centroId, int id, EspecialidadRequest solicitud) {
        validar(solicitud);
        Especialidad e = buscar(centroId, id);
        String nombre = solicitud.nombre().trim();
        if (especialidades.existsByCentroIdAndNombreIgnoreCaseAndIdNot(centroId, nombre, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe esa especialidad en el centro");
        }
        e.setNombre(nombre);
        e.setDescripcion(limpiar(solicitud.descripcion()));
        return EspecialidadResponse.desde(especialidades.saveAndFlush(e));
    }

    // Baja lógica (activo = false) o reactivación (activo = true)
    @Transactional
    public EspecialidadResponse cambiarEstado(int centroId, int id, boolean activo) {
        Especialidad e = buscar(centroId, id);
        if (!activo) {
            long afectados = medicos.countByEspecialidadIdAndActivoTrue(id);
            if (afectados > 0) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "No se puede dar de baja: hay " + afectados + " médico(s) activo(s) con esta especialidad");
            }
        }
        e.setEstado(activo);
        return EspecialidadResponse.desde(especialidades.saveAndFlush(e));
    }

    // ---------- Bloques horarios ----------

    @Transactional(readOnly = true)
    public List<HorarioResponse> listarHorarios(int centroId, int id) {
        buscar(centroId, id);
        return horarios.findByEspecialidadIdOrderByDiaSemanaAscHoraInicioAsc(id)
                .stream().map(HorarioResponse::desde).toList();
    }

    @Transactional
    public HorarioResponse agregarHorario(int centroId, int id, HorarioRequest solicitud) {
        validar(solicitud);
        Especialidad e = buscar(centroId, id);
        if (!e.isEstado()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se pueden agregar horarios a una especialidad inactiva");
        }
        for (var existente : horarios.findByEspecialidadIdAndDiaSemana(id, solicitud.diaSemana())) {
            // Se solapan si cada uno empieza antes de que termine el otro (bloques contiguos sí se permiten)
            if (solicitud.horaInicio().isBefore(existente.getHoraFin())
                    && solicitud.horaFin().isAfter(existente.getHoraInicio())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "El bloque se solapa con "
                        + existente.getHoraInicio() + " - " + existente.getHoraFin());
            }
        }
        HorarioEspecialidad h = new HorarioEspecialidad();
        h.setEspecialidad(e);
        h.setDiaSemana(solicitud.diaSemana());
        h.setHoraInicio(solicitud.horaInicio());
        h.setHoraFin(solicitud.horaFin());
        return HorarioResponse.desde(horarios.saveAndFlush(h));
    }

    @Transactional
    public void eliminarHorario(int centroId, int id, int horarioId) {
        buscar(centroId, id);
        HorarioEspecialidad h = horarios.findById(horarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Horario no encontrado"));
        if (h.getEspecialidad().getId() != id) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El horario no pertenece a la especialidad");
        }
        horarios.delete(h);
    }

    // ---------- Auxiliares ----------

    private Especialidad buscar(int centroId, int id) {
        Especialidad e = especialidades.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Especialidad no encontrada"));
        if (e.getCentro().getId() != centroId) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La especialidad no pertenece al centro");
        }
        return e;
    }

    private void verificarCentro(int centroId) {
        if (!centros.existsById(centroId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Centro no encontrado");
        }
    }

    private String limpiar(String texto) {
        return (texto == null || texto.isBlank()) ? null : texto.trim();
    }

    private void validar(EspecialidadRequest s) {
        if (s == null || s.nombre() == null || s.nombre().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre es obligatorio");
        }
        if (s.nombre().trim().length() > 100
                || (s.descripcion() != null && s.descripcion().trim().length() > 255)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nombre o descripción demasiado largos");
        }
    }

    private void validar(HorarioRequest s) {
        if (s == null || s.diaSemana() == null || s.horaInicio() == null || s.horaFin() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Día, hora de inicio y hora de fin son obligatorios");
        }
        if (s.diaSemana() < 1 || s.diaSemana() > 7) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El día debe estar entre 1 (lunes) y 7 (domingo)");
        }
        if (!s.horaFin().isAfter(s.horaInicio())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La hora de fin debe ser posterior a la de inicio");
        }
    }
}