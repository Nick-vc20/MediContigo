package medicontigo.backend.dto;

import java.util.List;

public class PerfilCentroDTO {
    
    private String nombre;
    private String direccion;
    private double costoConsulta;
    private String tiempoEspera;
    private List<String> especialidades;
    private List<String> horarios;

    public PerfilCentroDTO() {}

    public PerfilCentroDTO(String nombre, String direccion, double costoConsulta, 
                           String tiempoEspera, List<String> especialidades, List<String> horarios) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.costoConsulta = costoConsulta;
        this.tiempoEspera = tiempoEspera;
        this.especialidades = especialidades;
        this.horarios = horarios;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public double getCostoConsulta() { return costoConsulta; }
    public void setCostoConsulta(double costoConsulta) { this.costoConsulta = costoConsulta; }

    public String getTiempoEspera() { return tiempoEspera; }
    public void setTiempoEspera(String tiempoEspera) { this.tiempoEspera = tiempoEspera; }

    public List<String> getEspecialidades() { return especialidades; }
    public void setEspecialidades(List<String> especialidades) { this.especialidades = especialidades; }

    public List<String> getHorarios() { return horarios; }
    public void setHorarios(List<String> horarios) { this.horarios = horarios; }
}