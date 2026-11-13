
package medicontigo.backend.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "resenas")
public class Resena {

    // HU-14: estados del ciclo de moderación
    public static final String PENDIENTE = "PENDIENTE";
    public static final String PUBLICADA = "PUBLICADA";
    public static final String RECHAZADA = "RECHAZADA";
    public static final String OCULTA = "OCULTA";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "centro_id", nullable = false)
    private CentroMedico centro;

    // HU-18: paciente autenticado que publicó la reseña
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    @Column(name = "nombre_paciente", nullable = false, length = 80)
    private String nombrePaciente;

    @Column(nullable = false)
    private int puntuacion;

    @Column(nullable = false, length = 1000)
    private String comentario;

    @Column(nullable = false, length = 20)
    private String estado = "PENDIENTE";

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    // HU-14: alertas del filtro automático de contenido (códigos separados por coma)
    @Column(name = "alertas_contenido", length = 255)
    private String alertasContenido;

    // HU-14: datos de auditoría de la última decisión del administrador
    @Column(name = "motivo_moderacion", length = 255)
    private String motivoModeracion;

    @Column(name = "moderado_por", length = 255)
    private String moderadoPor;

    @Column(name = "fecha_moderacion")
    private LocalDateTime fechaModeracion;

    public Resena() {}

    @PrePersist
    public void antesDeGuardar() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        estado = PENDIENTE;
    }

    // HU-14: registra la decisión del administrador (quién, cuándo y por qué)
    public void moderar(String nuevoEstado, String motivo, String administrador) {
        this.estado = nuevoEstado;
        this.motivoModeracion = motivo;
        this.moderadoPor = administrador;
        this.fechaModeracion = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public CentroMedico getCentro() {
        return centro;
    }

    public void setCentro(CentroMedico centro) {
        this.centro = centro;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public String getNombrePaciente() {
        return nombrePaciente;
    }

    public void setNombrePaciente(String nombrePaciente) {
        this.nombrePaciente = nombrePaciente;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public String getEstado() {
        return estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public List<String> getAlertas() {
        if (alertasContenido == null || alertasContenido.isBlank()) {
            return List.of();
        }
        return Arrays.asList(alertasContenido.split(","));
    }

    public void setAlertas(List<String> alertas) {
        this.alertasContenido = (alertas == null || alertas.isEmpty())
            ? null
            : String.join(",", alertas);
    }

    public boolean tieneAlertas() {
        return !getAlertas().isEmpty();
    }

    public String getMotivoModeracion() {
        return motivoModeracion;
    }

    public String getModeradoPor() {
        return moderadoPor;
    }

    public LocalDateTime getFechaModeracion() {
        return fechaModeracion;
    }
}
