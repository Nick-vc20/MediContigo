
package medicontigo.backend.servicio;

import medicontigo.backend.modelo.CentroMedico;
import medicontigo.backend.repositorio.CentroMedicoRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

    @Service
    public class GeolocalizacionService {

        private final CentroMedicoRepository centroMedicoRepository;

        public GeolocalizacionService(CentroMedicoRepository centroMedicoRepository) {
            this.centroMedicoRepository = centroMedicoRepository;
        }

        // Buscar centros medicos dentro de un radio
        public List<CentroMedico> buscarCercanos(double lat, double lng, double radio) {

            if (lat < -90 || lat > 90 || lng < -180 || lng > 180
                    || !Double.isFinite(lat) || !Double.isFinite(lng)
                    || !Double.isFinite(radio) || radio <= 0) {
                throw new IllegalArgumentException("Los datos de ubicacion no son validos");
            }

            List<CentroMedico> centros = centroMedicoRepository.findAll();
            List<CentroMedico> cercanos = new ArrayList<>();

            for (CentroMedico centro : centros) {

                if (centro.getLatitud() == null || centro.getLongitud() == null) {
                    continue;
                }

                double latCentro = centro.getLatitud().doubleValue();
                double lngCentro = centro.getLongitud().doubleValue();

                double distancia = calcularDistancia(lat, lng, latCentro, lngCentro);

                if (distancia <= radio) {
                    cercanos.add(centro);
                }
            }

            return cercanos;
        }

        // Calcular distancia entre dos ubicaciones con Haversine
        private double calcularDistancia(double lat1, double lng1,
                                         double lat2, double lng2) {

            double radioTierra = 6371;

            double diferenciaLat = Math.toRadians(lat2 - lat1);
            double diferenciaLng = Math.toRadians(lng2 - lng1);

            double a = Math.sin(diferenciaLat / 2) * Math.sin(diferenciaLat / 2)
                    + Math.cos(Math.toRadians(lat1))
                    * Math.cos(Math.toRadians(lat2))
                    * Math.sin(diferenciaLng / 2) * Math.sin(diferenciaLng / 2);

            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

            return radioTierra * c;
        }
    }





