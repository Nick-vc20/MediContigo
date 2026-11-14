package medicontigo.backend.dto;

public record TriajeRequest(
        String sintomaPrincipal,
        String duracion,
        String intensidad,
        String sintomasAdicionales,
        String senalesAlarma
) {
}
