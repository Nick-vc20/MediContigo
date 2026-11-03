package medicontigo.backend.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "centro_especialidad")
public class CentroEspecialidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "horario_resumen")
    private String horarioResumen;

    private boolean estado;

    @ManyToOne
    @JoinColumn(name = "id_centro_medico")
    private CentroMedico centroMedico;

    @ManyToOne
    @JoinColumn(name = "id_especialidad")
    private Especialidad especialidad;

    public CentroEspecialidad() {
    }

    public CentroEspecialidad(String horarioResumen, boolean estado, CentroMedico centroMedico, Especialidad especialidad) {
        this.horarioResumen = horarioResumen;
        this.estado = estado;
        this.centroMedico = centroMedico;
        this.especialidad = especialidad;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHorarioResumen() {
        return horarioResumen;
    }

    public void setHorarioResumen(String horarioResumen) {
        this.horarioResumen = horarioResumen;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public CentroMedico getCentroMedico() {
        return centroMedico;
    }

    public void setCentroMedico(CentroMedico centroMedico) {
        this.centroMedico = centroMedico;
    }

    public Especialidad getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(Especialidad especialidad) {
        this.especialidad = especialidad;
    }
}