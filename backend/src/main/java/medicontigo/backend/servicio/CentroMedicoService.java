
package medicontigo.backend.servicio;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import medicontigo.backend.modelo.CentroMedico;
import medicontigo.backend.repositorio.CentroMedicoRepository;

@Service
public class CentroMedicoService {

    private final CentroMedicoRepository centroMedicoRepository;

    public CentroMedicoService(CentroMedicoRepository centroMedicoRepository) {
        this.centroMedicoRepository = centroMedicoRepository;
    }

    public CentroMedico registrarCentroMedico(CentroMedico centro) {

        if (centro == null) {
            throw new IllegalArgumentException("Debe ingresar los datos del centro medico");
        }

        if (centro.getNombre() == null || centro.getNombre().isBlank()
                || centro.getTipo() == null || centro.getTipo().isBlank()
                || centro.getDireccion() == null || centro.getDireccion().isBlank()) {

            throw new IllegalArgumentException("Todos los campos son obligatorios");
        }

        if (centro.getLatitud() == null || centro.getLongitud() == null) {
            throw new IllegalArgumentException("La ubicacion es obligatoria");
        }

        if (centro.getLatitud().compareTo(new BigDecimal("-90")) < 0
                || centro.getLatitud().compareTo(new BigDecimal("90")) > 0
                || centro.getLongitud().compareTo(new BigDecimal("-180")) < 0
                || centro.getLongitud().compareTo(new BigDecimal("180")) > 0) {

            throw new IllegalArgumentException("Las coordenadas no son validas");
        }

        String nombre = centro.getNombre().trim();
        String tipo = centro.getTipo().trim();
        String direccion = centro.getDireccion().trim();

        if (centroMedicoRepository.existsByNombreIgnoreCaseAndDireccionIgnoreCase(nombre, direccion)) {
            throw new IllegalArgumentException("El centro medico ya esta registrado");
        }

        centro.setNombre(nombre);
        centro.setTipo(tipo);
        centro.setDireccion(direccion);

        return centroMedicoRepository.save(centro);
    }

}
