package medicontigo.backend.dto;

import java.math.BigDecimal;

import medicontigo.backend.modelo.CentroMedico;

// Datos livianos que necesita el mapa y la lista lateral.
// Se evita devolver la entidad completa para no arrastrar relaciones JPA.
public record CentroMapaResponse(
        int id,
        String nombre,
        String tipo,
        String direccion,
        String telefono,
        BigDecimal latitud,
        BigDecimal longitud,
        double costoConsulta,
        String tiempoEspera,
        String nivelOcupacion,
        double distanciaKm) {

    public static CentroMapaResponse desde(CentroMedico c, double distanciaKm) {
        return new CentroMapaResponse(c.getId(), c.getNombre(), c.getTipo(), c.getDireccion(),
                c.getTelefono(), c.getLatitud(), c.getLongitud(), c.getCostoConsulta(),
                c.getTiempoEspera(), c.getNivelOcupacion(), distanciaKm);
    }
}
