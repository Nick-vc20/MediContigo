package medicontigo.backend.modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "centro_medico") 
public class CentroMedico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nombre;
    private String tipo;
    private String direccion;
    public CentroMedico() {
    }

    public CentroMedico(int id, String nombre, String tipo, String direccion) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.direccion = direccion;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public String getDireccion() {
        return direccion;
    }

    @Override
    public String toString() {
        return id + " - " + nombre + " (" + tipo + ") - " + direccion;
    }



}
