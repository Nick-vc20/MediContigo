package medicontigo.backend.servicio;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import medicontigo.backend.dto.TarifaRequest;
import medicontigo.backend.dto.TarifaResponse;
import medicontigo.backend.modelo.CentroMedico;
import medicontigo.backend.modelo.Especialidad;
import medicontigo.backend.modelo.Tarifa;
import medicontigo.backend.repositorio.CentroMedicoRepository;
import medicontigo.backend.repositorio.EspecialidadRepository;
import medicontigo.backend.repositorio.TarifaRepository;

@Service
public class TarifaService {

    private final TarifaRepository tarifaRepository;
    private final EspecialidadRepository especialidadRepository;
    private final CentroMedicoRepository centroMedicoRepository;

    public TarifaService(
            TarifaRepository tarifaRepository,
            EspecialidadRepository especialidadRepository,
            CentroMedicoRepository centroMedicoRepository) {

        this.tarifaRepository = tarifaRepository;
        this.especialidadRepository = especialidadRepository;
        this.centroMedicoRepository = centroMedicoRepository;
    }

    @Transactional(readOnly = true)
    public List<TarifaResponse> listar(int centroId) {

        validarCentro(centroId);

        return tarifaRepository
                .findByEspecialidadCentroIdOrderByEspecialidadNombreAsc(centroId)
                .stream()
                .map(this::convertirRespuesta)
                .toList();
    }

    @Transactional(readOnly = true)
    public TarifaResponse obtener(int centroId, int especialidadId) {

        validarEspecialidad(centroId, especialidadId);

        Tarifa tarifa = tarifaRepository
                .findByEspecialidadId(especialidadId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "La especialidad no tiene una tarifa registrada"
                ));

        return convertirRespuesta(tarifa);
    }

    @Transactional
    public TarifaResponse guardar(
            int centroId,
            int especialidadId,
            TarifaRequest solicitud) {

        Especialidad especialidad =
                validarEspecialidad(centroId, especialidadId);

        validarPrecio(solicitud);

        Tarifa tarifa = tarifaRepository
                .findByEspecialidadId(especialidadId)
                .orElseGet(Tarifa::new);

        tarifa.setEspecialidad(especialidad);
        tarifa.setPrecioAproximado(solicitud.precioAproximado());
        tarifa.setMoneda("PEN");

        Tarifa tarifaGuardada = tarifaRepository.saveAndFlush(tarifa);

        return convertirRespuesta(tarifaGuardada);
    }

    private void validarCentro(int centroId) {

        if (centroId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El identificador del centro medico no es valido"
            );
        }

        if (!centroMedicoRepository.existsById(centroId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "El centro medico no existe"
            );
        }
    }

    private Especialidad validarEspecialidad(
            int centroId,
            int especialidadId) {

        validarCentro(centroId);

        if (especialidadId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El identificador de la especialidad no es valido"
            );
        }

        Especialidad especialidad = especialidadRepository
                .findById(especialidadId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "La especialidad no existe"
                ));

        if (especialidad.getCentro().getId() != centroId) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La especialidad no pertenece al centro medico indicado"
            );
        }

        return especialidad;
    }

    private void validarPrecio(TarifaRequest solicitud) {

        if (solicitud == null || solicitud.precioAproximado() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El precio aproximado es obligatorio"
            );
        }

        BigDecimal precio = solicitud.precioAproximado();

        if (precio.compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El precio aproximado no puede ser negativo"
            );
        }

        if (precio.scale() > 2) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El precio aproximado debe tener como maximo dos decimales"
            );
        }

        if (precio.compareTo(new BigDecimal("99999999.99")) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El precio aproximado supera el limite permitido"
            );
        }
    }

    private TarifaResponse convertirRespuesta(Tarifa tarifa) {

        Especialidad especialidad = tarifa.getEspecialidad();
        CentroMedico centro = especialidad.getCentro();

        return new TarifaResponse(
                tarifa.getId(),
                centro.getId(),
                especialidad.getId(),
                especialidad.getNombre(),
                tarifa.getPrecioAproximado(),
                tarifa.getMoneda()
        );
    }
}
