package medicontigo.backend.dto;

import java.util.List;

public class PerfilCentroDTO {
    private String nombreCentro;      // En lugar de 'nombre'
    private String direccionCentro;   // En lugar de 'direccion'
    private double costoConsulta;
    private String tiempoEspera;
    private List<String> especialidades;
    private List<String> resumenHorarios; // En lugar de 'horarios'

    public PerfilCentroDTO(String nombreCentro, String direccionCentro, double costoConsulta, 
                           String tiempoEspera, List<String> especialidades, List<String> resumenHorarios) {
        this.nombreCentro = nombreCentro;
        this.direccionCentro = direccionCentro;
        this.costoConsulta = costoConsulta;
        this.tiempoEspera = tiempoEspera;
        this.especialidades = especialidades;
        this.resumenHorarios = resumenHorarios;
    }

    public String getNombreCentro() { return nombreCentro; }
    public String getDireccionCentro() { return direccionCentro; }
    public double getCostoConsulta() { return costoConsulta; }
    public String getTiempoEspera() { return tiempoEspera; }
    public List<String> getEspecialidades() { return especialidades; }
    public List<String> getResumenHorarios() { return resumenHorarios; }
}