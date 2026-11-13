// HU-18 (18.3): protege las páginas privadas del paciente
import { Navigate, useLocation } from "react-router-dom";
import { ultimoMotivoCierre, useSesionPaciente } from "../auth/sesionPaciente";

export default function RutaPaciente({ children }) {
    const sesion = useSesionPaciente();
    const location = useLocation();

    if (!sesion) {
        return (
            <Navigate
                to="/paciente/login"
                replace
                state={{ desde: location.pathname, motivo: ultimoMotivoCierre() }}
            />
        );
    }
    return children;
}
