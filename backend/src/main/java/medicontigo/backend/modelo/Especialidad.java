package medicontigo.backend.modelo;
import jakarta.persistence.*;

@Entity
@Table(name = "especialidad") // Asegúrate de que coincida con tu schema.sql
public class Especialidad {

    @Id // <-- Agrega esta línea
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nombre;
    private boolean estado;
    public Especialidad() {
    }
    public Especialidad(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return id + " - " + nombre;
    }





}
