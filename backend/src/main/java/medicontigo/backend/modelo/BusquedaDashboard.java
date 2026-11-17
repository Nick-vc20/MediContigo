
package medicontigo.backend.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "busquedas_dashboard")
public class BusquedaDashboard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String especialidad;

    @Column(length = 80)
    private String zona;

    @Column(name = "fecha_busqueda", nullable = false)
    private LocalDateTime fechaBusqueda;

    public BusquedaDashboard() {
    }

    public BusquedaDashboard(String especialidad, String zona) {
        this.especialidad = especialidad;
        this.zona = zona;
    }

    @PrePersist
    public void asignarFecha() {
        if (fechaBusqueda == null) {
            fechaBusqueda = LocalDateTime.now(
                    ZoneId.of("America/Lima")
            );
        }
    }

    public Long getId() {
        return id;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getZona() {
        return zona;
    }

    public void setZona(String zona) {
        this.zona = zona;
    }

    public LocalDateTime getFechaBusqueda() {
        return fechaBusqueda;
    }

    public void setFechaBusqueda(LocalDateTime fechaBusqueda) {
        this.fechaBusqueda = fechaBusqueda;
    }
}
