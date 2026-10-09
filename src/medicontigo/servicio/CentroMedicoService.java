package medicontigo.servicio;

import medicontigo.modelo.CentroMedico;
import java.util.ArrayList;
import java.util.List;

// HU-01: completar validaciones, duplicados y persistencia.
public class CentroMedicoService {
    private final List<CentroMedico> centros = new ArrayList<>();

    public void agregar(CentroMedico centro) {
        centros.add(centro);
    }

    public List<CentroMedico> listar() {
        return new ArrayList<>(centros);
    }




}
