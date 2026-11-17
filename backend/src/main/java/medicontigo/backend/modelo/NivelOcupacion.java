package medicontigo.backend.modelo;

/**
 * HU-10: niveles de ocupación que el administrador puede informar para un establecimiento.
 * En la base de datos se guarda el nombre del valor (BAJO, MEDIO, ALTO) en centros_medicos.nivel_ocupacion.
 */
public enum NivelOcupacion {

    BAJO("Ocupación baja", "Sin espera"),
    MEDIO("Ocupación media", "Espera regular"),
    ALTO("Ocupación alta", "Colapsado");

    private final String etiqueta;
    private final String descripcion;

    NivelOcupacion(String etiqueta, String descripcion) {
        this.etiqueta = etiqueta;
        this.descripcion = descripcion;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public String getDescripcion() {
        return descripcion;
    }

    /** Acepta mayúsculas o minúsculas y espacios alrededor ("alto", " ALTO "). */
    public static NivelOcupacion desde(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Debes indicar el nivel de ocupación");
        }
        try {
            return valueOf(valor.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Nivel de ocupación no válido. Usa BAJO, MEDIO o ALTO");
        }
    }
}
