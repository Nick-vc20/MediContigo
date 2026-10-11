package medicontigo.backend.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.*;

@Entity
public class Medico {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    private String nombre;
    private String nColegiatura;
    private int especialidadId;

    public Medico() {
    }

    public Medico(int id, String nombre, String nColegiatura, int especialidadId) {
        this.id = id;
        this.nombre = nombre;
        this.nColegiatura = nColegiatura;
        this.especialidadId = especialidadId;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getnColegiatura() {
        return nColegiatura;
    }

    public int getEspecialidadId() {
        return especialidadId;
    }

    @Override
    public String toString() {
        return nombre + " (Número de colegiatura: " + nColegiatura + ")";
    }
}
