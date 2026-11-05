
import { useState } from "react";
import Login from "./components/Login";
import Administrador from "./components/Administrador";
import Especialidades from "./components/Especialidades";
import MapaCentros from "./components/MapaCentros";
import "./App.css";

export default function App() {

    const [logueado, setLogueado] = useState(
        localStorage.getItem("token") !== null
    );

    const [mostrarMapa, setMostrarMapa] = useState(false);

    // Mostrar el mapa de centros medicos
    if (mostrarMapa) {
        return (
            <div>
                <button onClick={() => setMostrarMapa(false)}>
                    Volver
                </button>

                <MapaCentros />
            </div>
        );
    }

    // Pantalla de inicio de sesion
    if (!logueado) {
        return (
            <>
                <button onClick={() => setMostrarMapa(true)}>
                    Ver centros medicos cercanos
                </button>

                <Login alIniciarSesion={() => setLogueado(true)} />
            </>
        );
    }

    // Panel del administrador
    return (
        <>
            <button onClick={() => setMostrarMapa(true)}>
                Ver mapa de centros medicos
            </button>

            <Administrador alCerrarSesion={() => setLogueado(false)} />
            <Especialidades centroId={1} />
        </>
    );
}
