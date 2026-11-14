package medicontigo.backend.dto;

import java.math.BigDecimal;

public record TarifaRequest(
    BigDecimal precioAproximado
) {
}
