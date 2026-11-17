
package medicontigo.backend.servicio;

import medicontigo.backend.modelo.Paciente;
import medicontigo.backend.modelo.ResultadoTriaje;
import medicontigo.backend.repositorio.PacienteRepository;
import medicontigo.backend.repositorio.ResultadoTriajeRepository;
import medicontigo.backend.modelo.BusquedaDashboard;
import medicontigo.backend.repositorio.BusquedaDashboardRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class DashboardService {

    private final PacienteRepository pacienteRepository;
    private final ResultadoTriajeRepository triajeRepository;
    private final BusquedaDashboardRepository busquedaRepository;


    public DashboardService(
            PacienteRepository pacienteRepository,
            ResultadoTriajeRepository triajeRepository,
            BusquedaDashboardRepository busquedaRepository) {

        this.pacienteRepository = pacienteRepository;
        this.triajeRepository = triajeRepository;
        this.busquedaRepository = busquedaRepository;
    }

    public Map<String, Object> obtenerEstadisticas() {
        return obtenerEstadisticas(null, null);
    }

    public Map<String, Object> obtenerEstadisticas(
            LocalDate desde, LocalDate hasta) {

        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new IllegalArgumentException(
                    "La fecha inicial no puede ser mayor que la final"
            );
        }


        List<Paciente> pacientes = pacienteRepository.findAll();

        long totalPacientes = pacientes.stream()
                .filter(p -> p.getFechaRegistro() != null)
                .filter(p -> estaEnRango(
                        p.getFechaRegistro().toLocalDate(),
                        desde, hasta
                ))
                .count();


        List<ResultadoTriaje> triajes = triajeRepository.findAll();

        Map<String, Long> triajesPorDia = new TreeMap<>();
        Map<String, Long> especialidades = new TreeMap<>();

        long totalTriajes = 0;

        for (ResultadoTriaje triaje : triajes) {

            if (triaje.getFechaTriaje() == null) {
                continue;
            }

            LocalDate fecha = triaje.getFechaTriaje().toLocalDate();

            if (!estaEnRango(fecha, desde, hasta)) {
                continue;
            }

            totalTriajes++;


            triajesPorDia.merge(
                    fecha.toString(), 1L, Long::sum
            );


            String especialidad = triaje.getEspecialidadRecomendada();

            if (especialidad != null && !especialidad.isBlank()) {
                especialidades.merge(
                        especialidad.trim(), 1L, Long::sum
                );
            }
        }


        // HU-15: Estadisticas de busquedas realizadas
        List<BusquedaDashboard> busquedas = busquedaRepository.findAll();

        Map<String, Long> busquedasPorEspecialidad = new TreeMap<>();
        Map<String, Long> busquedasPorDia = new TreeMap<>();
        Map<String, Long> busquedasPorZona = new TreeMap<>();

        long totalBusquedas = 0;

        for (BusquedaDashboard busqueda : busquedas) {

            if (busqueda.getFechaBusqueda() == null) {
                continue;
            }

            LocalDate fecha = busqueda.getFechaBusqueda().toLocalDate();

            if (!estaEnRango(fecha, desde, hasta)) {
                continue;
            }

            totalBusquedas++;

            busquedasPorDia.merge(
                    fecha.toString(), 1L, Long::sum
            );

            String especialidadBuscada = busqueda.getEspecialidad();

            if (especialidadBuscada != null &&
                    !especialidadBuscada.isBlank()) {

                busquedasPorEspecialidad.merge(
                        especialidadBuscada.trim(), 1L, Long::sum
                );
            }

            String zona = busqueda.getZona();

            if (zona != null && !zona.isBlank()) {
                busquedasPorZona.merge(
                        zona.trim(), 1L, Long::sum
                );
            }
        }

        Map<String, Object> estadisticas = new LinkedHashMap<>();

        estadisticas.put("totalPacientes", totalPacientes);
        estadisticas.put("totalTriajes", totalTriajes);
        estadisticas.put("triajesPorDia", triajesPorDia);
        estadisticas.put("especialidadesRecomendadas", especialidades);
        estadisticas.put("totalBusquedas", totalBusquedas);
        estadisticas.put(
                "busquedasPorEspecialidad",
                busquedasPorEspecialidad
        );
        estadisticas.put("busquedasPorDia", busquedasPorDia);
        estadisticas.put("busquedasPorZona", busquedasPorZona);


        return estadisticas;
    }


    private boolean estaEnRango(
            LocalDate fecha, LocalDate desde, LocalDate hasta) {

        if (desde != null && fecha.isBefore(desde)) {
            return false;
        }

        if (hasta != null && fecha.isAfter(hasta)) {
            return false;
        }

        return true;
    }
}
