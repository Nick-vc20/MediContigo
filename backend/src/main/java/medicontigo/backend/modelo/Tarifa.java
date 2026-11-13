package medicontigo.backend.modelo;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tarifa")
public class Tarifa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "especialidad_id", nullable = false, unique = true)
    private Especialidad especialidad;

    @Column(name = "precio_aproximado", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioAproximado;

    @Column(nullable = false, length = 3)
    private String moneda = "PEN";

    public Tarifa() {
    }

    public int getId() {
        return id;
    }

    public Especialidad getEspecialidad() {
        return especialidad;
    }

    public BigDecimal getPrecioAproximado() {
        return precioAproximado;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setEspecialidad(Especialidad especialidad) {
        this.especialidad = especialidad;
    }

    public void setPrecioAproximado(BigDecimal precioAproximado) {
        this.precioAproximado = precioAproximado;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }
}
