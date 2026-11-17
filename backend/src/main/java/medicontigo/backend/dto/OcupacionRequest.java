package medicontigo.backend.dto;

// HU-10 (10.2): cuerpo de PATCH /api/admin/centros/{id}/ocupacion -> { "nivel": "ALTO" }
public record OcupacionRequest(String nivel) {
}
