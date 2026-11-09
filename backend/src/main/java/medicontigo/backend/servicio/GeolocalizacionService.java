package medicontigo.backend.servicio;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import medicontigo.backend.dto.CentroMapaResponse;
import medicontigo.backend.modelo.CentroMedico;
import medicontigo.backend.repositorio.CentroMedicoRepository;

@Service
public class GeolocalizacionService {

    private final CentroMedicoRepository centroMedicoRepository;

    public GeolocalizacionService(CentroMedicoRepository centroMedicoRepository) {
        this.centroMedicoRepository = centroMedicoRepository;
    }
    public List<CentroMapaResponse> buscarCercanos(double lat, double lng, double radio) {

        if (!Double.isFinite(lat) || !Double.isFinite(lng) || !Double.isFinite(radio)
                || lat < -90 || lat > 90 || lng < -180 || lng > 180 || radio <= 0) {
            throw new IllegalArgumentException("Los datos de ubicacion no son validos");
        }

        List<CentroMapaResponse> cercanos = new ArrayList<>();

        for (CentroMedico centro : centroMedicoRepository.findAll()) {

            if (centro.getLatitud() == null || centro.getLongitud() == null) {
                continue;
            }

            double distancia = calcularDistancia(lat, lng,
                    centro.getLatitud().doubleValue(), centro.getLongitud().doubleValue());

            if (distancia <= radio) {
                cercanos.add(CentroMapaResponse.desde(centro, Math.round(distancia * 100.0) / 100.0));
            }
        }

        cercanos.sort(Comparator.comparingDouble(CentroMapaResponse::distanciaKm));
        return cercanos;
    }

    private double calcularDistancia(double lat1, double lng1, double lat2, double lng2) {

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
