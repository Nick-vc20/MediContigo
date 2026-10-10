package medicontigo.backend.modelo;

public class Medico {
    private int id;
    private String nombre;
    private String nColegiatura;
    private int especialidadId;

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
