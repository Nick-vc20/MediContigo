package medicontigo.backend.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

// HU-12: resultado de triaje guardado en el historial del paciente
@Entity
@Table(name = "historial_triaje")
public class ResultadoTriaje {

    // Niveles de urgencia permitidos (coinciden con el CHECK de schema.sql)
    public static final Set<String> NIVELES_URGENCIA = Set.of("BAJA", "MEDIA", "ALTA", "EMERGENCIA");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @Column(nullable = false, length = 500)
    private String sintomas;

    @Column(name = "nivel_urgencia", nullable = false, length = 20)
    private String nivelUrgencia;

    @Column(name = "especialidad_recomendada", length = 100)
    private String especialidadRecomendada;

    @Column(nullable = false, length = 1000)
    private String recomendacion;

    @Column(name = "fecha_triaje", nullable = false)
    private LocalDateTime fechaTriaje;

    public ResultadoTriaje() {}

    @PrePersist
    public void antesDeGuardar() {
        if (fechaTriaje == null) {
            fechaTriaje = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }

    public Paciente getPaciente() { return paciente; }
    public void setPaciente(Paciente paciente) { this.paciente = paciente; }

    public String getSintomas() { return sintomas; }
    public void setSintomas(String sintomas) { this.sintomas = sintomas; }

    public String getNivelUrgencia() { return nivelUrgencia; }
    public void setNivelUrgencia(String nivelUrgencia) { this.nivelUrgencia = nivelUrgencia; }

    public String getEspecialidadRecomendada() { return especialidadRecomendada; }
    public void setEspecialidadRecomendada(String especialidadRecomendada) {
        this.especialidadRecomendada = especialidadRecomendada;
    }

    public String getRecomendacion() { return recomendacion; }
    public void setRecomendacion(String recomendacion) { this.recomendacion = recomendacion; }

    public LocalDateTime getFechaTriaje() { return fechaTriaje; }
    public void setFechaTriaje(LocalDateTime fechaTriaje) { this.fechaTriaje = fechaTriaje; }
}