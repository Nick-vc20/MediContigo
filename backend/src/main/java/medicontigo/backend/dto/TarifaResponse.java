package medicontigo.backend.dto;

import java.math.BigDecimal;

public record TarifaResponse(
    int id,
    int centroId,
    int especialidadId,
    String especialidadNombre,
    BigDecimal precioAproximado,
    String moneda
) {
}
