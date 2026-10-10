package medicontigo.modelo;

public class Administrador {
    private int id;
    private String correo;
    private String contraseña;

    public Administrador(int id, String correo, String contraseña) {
        this.id     = id;
        this.correo = correo;
        this.contraseña = contraseña;
    }

    public int getId() {
        return id;
    }

    public String getCorreo() {
        return correo;
    }


}
