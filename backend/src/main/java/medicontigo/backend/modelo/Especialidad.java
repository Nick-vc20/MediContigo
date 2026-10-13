package medicontigo.backend.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "especialidad")
public class Especialidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "centro_id", nullable = false)
    private CentroMedico centro;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 255)
    private String descripcion;

    @Column(nullable = false)
    private boolean estado = true;

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion = LocalDateTime.now();

    public Especialidad() {}

    @PrePersist
    @PreUpdate
    void actualizarFecha() { this.fechaActualizacion = LocalDateTime.now(); }

    public int getId() { return id; }
    public CentroMedico getCentro() { return centro; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public boolean isEstado() { return estado; }
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }

    public void setCentro(CentroMedico centro) { this.centro = centro; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setEstado(boolean estado) { this.estado = estado; }

    @Override
    public String toString() { return id + " - " + nombre; }
}