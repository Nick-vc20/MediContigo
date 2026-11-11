
package medicontigo.backend.servicio;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import medicontigo.backend.dto.CentroMapaResponse;
import medicontigo.backend.modelo.Especialidad;
import medicontigo.backend.repositorio.EspecialidadRepository;
import medicontigo.backend.repositorio.HorarioEspecialidadRepository;

@Service
public class FiltrosCentroService {

    private final GeolocalizacionService geolocalizacion;
    private final EspecialidadRepository especialidades;
    private final HorarioEspecialidadRepository horarios;

    public FiltrosCentroService(
            GeolocalizacionService geolocalizacion,
            EspecialidadRepository especialidades,
            HorarioEspecialidadRepository horarios) {
        this.geolocalizacion = geolocalizacion;
        this.especialidades = especialidades;
        this.horarios = horarios;
    }

    @Transactional(readOnly = true)
    public List<CentroMapaResponse> buscar(
            double lat,
            double lng,
            double radio,
            String especialidad,
            String tipo,
            Double maxCosto,
            boolean hoy) {

        if (maxCosto != null &&
                (!Double.isFinite(maxCosto) || maxCosto < 0)) {
            throw new IllegalArgumentException(
                "El costo máximo debe ser válido y no negativo"
            );
        }

        String especialidadFiltro = normalizar(especialidad);
        String tipoFiltro = normalizar(tipo);

        int diaHoy = LocalDate.now(
            ZoneId.of("America/Lima")
        ).getDayOfWeek().getValue();

        List<CentroMapaResponse> resultado = new ArrayList<>();

        for (CentroMapaResponse centro :
                geolocalizacion.buscarCercanos(lat, lng, radio)) {

            // Filtrar por tipo de establecimiento
            if (!tipoFiltro.isEmpty() &&
                    !normalizar(centro.tipo()).equals(tipoFiltro)) {
                continue;
            }

            // Filtrar por costo máximo
            if (maxCosto != null &&
                    (centro.costoConsulta() <= 0 ||
                     centro.costoConsulta() > maxCosto)) {
                continue;
            }

            // Sin filtros de especialidad ni horario
            if (especialidadFiltro.isEmpty() && !hoy) {
                resultado.add(centro);
                continue;
            }

            // Verificar especialidades activas
            List<Especialidad> disponibles =
                especialidades.findByCentroIdAndEstadoTrueOrderByNombreAsc(
                    centro.id()
                );

            boolean coincide = disponibles.stream().anyMatch(esp -> {

                boolean nombreValido =
                    esp.getNombre() != null &&
                    (especialidadFiltro.isEmpty() ||
                     normalizar(esp.getNombre()).equals(especialidadFiltro));

                if (!nombreValido) {
                    return false;
                }

                // Si se solicita atención hoy, verificar horario
                return !hoy ||
                    !horarios.findByEspecialidadIdAndDiaSemana(
                        esp.getId(), diaHoy
                    ).isEmpty();
            });

            if (coincide) {
                resultado.add(centro);
            }
        }

        return resultado;
    }

    @Transactional(readOnly = true)
    public Map<String, List<String>> opciones(
            double lat, double lng, double radio) {

        Set<String> tipos =
            new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

        Set<String> nombres =
            new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

        for (CentroMapaResponse centro :
                geolocalizacion.buscarCercanos(lat, lng, radio)) {

            if (centro.tipo() != null &&
                    !centro.tipo().isBlank()) {
                tipos.add(centro.tipo().trim());
            }

            for (Especialidad esp :
                    especialidades.findByCentroIdAndEstadoTrueOrderByNombreAsc(
                        centro.id())) {

                if (esp.getNombre() != null &&
                        !esp.getNombre().isBlank()) {
                    nombres.add(esp.getNombre().trim());
                }
            }
        }

        return Map.of(
            "tipos", List.copyOf(tipos),
            "especialidades", List.copyOf(nombres)
        );
    }

    private static String normalizar(String texto) {
        if (texto == null) return "";

        return Normalizer.normalize(
                texto.trim(), Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "")
            .toLowerCase(Locale.ROOT);
    }
}
