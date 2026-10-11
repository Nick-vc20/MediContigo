package medicontigo.backend.dto;

// Datos que envía el cliente al registrar o editar un médico.
public record MedicoRequest(String nombreCompleto, String cmp, Integer especialidadId) {}
