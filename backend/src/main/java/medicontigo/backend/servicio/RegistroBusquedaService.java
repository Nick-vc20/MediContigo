
package medicontigo.backend.servicio;

import medicontigo.backend.modelo.BusquedaDashboard;
import medicontigo.backend.repositorio.BusquedaDashboardRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistroBusquedaService {

    private final BusquedaDashboardRepository repositorio;

    public RegistroBusquedaService(
            BusquedaDashboardRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Transactional
    public void registrarBusqueda(String especialidad, String zona) {

        if (especialidad == null || especialidad.isBlank()) {
            throw new IllegalArgumentException(
                    "Debe seleccionar una especialidad"
            );
        }

        String nombreEspecialidad = especialidad.trim();

        if (nombreEspecialidad.length() > 150) {
            throw new IllegalArgumentException(
                    "La especialidad supera el limite permitido"
            );
        }

        String zonaGeneral = null;

        if (zona != null && !zona.isBlank()) {
            zonaGeneral = zona.trim();

            if (zonaGeneral.length() > 80) {
                throw new IllegalArgumentException(
                        "La zona supera el limite permitido"
                );
            }
        }

        BusquedaDashboard busqueda = new BusquedaDashboard(
                nombreEspecialidad,
                zonaGeneral
        );

        repositorio.save(busqueda);
    }
}
