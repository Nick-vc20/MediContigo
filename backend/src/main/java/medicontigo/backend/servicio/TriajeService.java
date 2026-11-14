package medicontigo.backend.servicio;

import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import medicontigo.backend.dto.TriajeRequest;
import medicontigo.backend.dto.TriajeResponse;

@Service
public class TriajeService {

    private static final Set<String> SINTOMAS = Set.of(
            "DOLOR_CABEZA",
            "DOLOR_ABDOMINAL",
            "FIEBRE",
            "TOS",
            "MALESTAR_GENERAL",
            "OTRO"
    );

    private static final Set<String> DURACIONES = Set.of(
            "MENOS_24_HORAS",
            "UNO_TRES_DIAS",
            "CUATRO_SIETE_DIAS",
            "MAS_SEMANA"
    );

    private static final Set<String> INTENSIDADES = Set.of(
            "LEVE",
            "MODERADA",
            "INTENSA"
    );

    private static final Set<String> SENALES_ALARMA = Set.of(
            "SI",
            "NO",
            "NO_SE"
    );

    private final ReglasTriaje reglasTriaje;

    public TriajeService(ReglasTriaje reglasTriaje) {
        this.reglasTriaje = reglasTriaje;
    }

    public TriajeResponse evaluar(TriajeRequest solicitud) {
        validarRespuestas(solicitud);
        return reglasTriaje.evaluar(solicitud);
    }

    public void validarRespuestas(TriajeRequest solicitud) {

        if (solicitud == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe completar el cuestionario"
            );
        }

        validarOpcion(
                solicitud.sintomaPrincipal(),
                SINTOMAS,
                "El sintoma principal no es valido"
        );

        validarOpcion(
                solicitud.duracion(),
                DURACIONES,
                "La duracion no es valida"
        );

        validarOpcion(
                solicitud.intensidad(),
                INTENSIDADES,
                "La intensidad no es valida"
        );

        validarOpcion(
                solicitud.senalesAlarma(),
                SENALES_ALARMA,
                "La respuesta sobre senales de alarma no es valida"
        );

        if (solicitud.sintomasAdicionales() != null
                && solicitud.sintomasAdicionales().length() > 500) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Los sintomas adicionales no pueden superar 500 caracteres"
            );
        }
    }

    private void validarOpcion(
            String valor,
            Set<String> opcionesPermitidas,
            String mensajeError
    ) {

        if (valor == null || !opcionesPermitidas.contains(valor)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    mensajeError
            );
        }
    }
}
