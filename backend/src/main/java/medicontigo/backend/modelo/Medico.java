package medicontigo.backend.modelo;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "medicos")
public class Medico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nombre_completo", nullable = false, length = 150)
    private String nombreCompleto;

    @Column(nullable = false, unique = true, length = 30)
    private String cmp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "especialidad_id", nullable = false)
    private Especialidad especialidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "centro_id", nullable = false)
    private CentroMedico centro;

    @Column(nullable = false)
    private boolean activo = true;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDate fechaIngreso = LocalDate.now();

    public Medico() {}

    public Integer getId() { return id; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getCmp() { return cmp; }
    public Especialidad getEspecialidad() { return especialidad; }
    public CentroMedico getCentro() { return centro; }
    public boolean isActivo() { return activo; }
    public LocalDate getFechaIngreso() { return fechaIngreso; }

    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public void setCmp(String cmp) { this.cmp = cmp; }
    public void setEspecialidad(Especialidad especialidad) { this.especialidad = especialidad; }
    public void setCentro(CentroMedico centro) { this.centro = centro; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
