package medicontigo.backend.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// HU-17: paciente registrado con correo y contraseña cifrada
@Entity
@Table(name = "pacientes")
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_completo", nullable = false, length = 80)
    private String nombreCompleto;

    @Column(nullable = false, unique = true, length = 120)
    private String correo;

    // Nunca se guarda la contraseña en texto plano: solo el hash BCrypt
    @Column(name = "contrasena_hash", nullable = false, length = 100)
    private String contrasenaHash;

    @Column(name = "acepta_tratamiento_datos", nullable = false)
    private boolean aceptaTratamientoDatos;

    @Column(name = "fecha_aceptacion_datos", nullable = false)
    private LocalDateTime fechaAceptacionDatos;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    public Paciente() {}

    @PrePersist
    public void antesDeGuardar() {
        LocalDateTime ahora = LocalDateTime.now();
        if (fechaRegistro == null) {
            fechaRegistro = ahora;
        }
        if (fechaAceptacionDatos == null) {
            fechaAceptacionDatos = ahora;
        }
    }

    public Long getId() {
        return id;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasenaHash() {
        return contrasenaHash;
    }

    public void setContrasenaHash(String contrasenaHash) {
        this.contrasenaHash = contrasenaHash;
    }

    public boolean isAceptaTratamientoDatos() {
        return aceptaTratamientoDatos;
    }

    public void setAceptaTratamientoDatos(boolean aceptaTratamientoDatos) {
        this.aceptaTratamientoDatos = aceptaTratamientoDatos;
    }

    public LocalDateTime getFechaAceptacionDatos() {
        return fechaAceptacionDatos;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }
}
