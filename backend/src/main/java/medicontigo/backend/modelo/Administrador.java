package medicontigo.backend.modelo;
import jakarta.persistence.*;
@Entity
@Table(name = "administrador") // Le dice a Java que use la tabla exacta de tu schema.sql
public class Administrador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "correo")
    private String correo;

    // Si en tu SQL pusiste "contrasena" pero en Java lo llamas "password":
    @Column(name = "contrasena") 
    private String password;
    
    public Administrador(){
    }
    public Administrador(int id, String correo, String password) {
   
        this.id     = id;
        this.correo = correo;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public String getCorreo() {
        return correo;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setCorreo(String correo) {
        this.correo = correo;
    }
    public void setId(int id) {
        this.id = id;
    }
}
