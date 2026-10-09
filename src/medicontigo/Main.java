package medicontigo;

import medicontigo.modelo.CentroMedico;
import medicontigo.servicio.CentroMedicoService;

public class Main {
    public static void main(String[] args) {

        System.out.println("PRUEBA");

        CentroMedicoService servicio = new CentroMedicoService();

        servicio.agregar(new CentroMedico(1, "Posta contigo", "Posta", "Lima"));

        for (CentroMedico centro : servicio.listar()) {
            System.out.println(centro);
        }
    }



}
