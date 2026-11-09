
import { useState } from "react";
import { BrowserRouter, Routes, Route, Navigate, Link } from "react-router-dom";

import Login from "./components/Login";
import Administrador from "./components/Administrador";
import Especialidades from "./components/Especialidades";
import MapaCentros from "./components/MapaCentros";
import PerfilCentroPublico from "./components/PerfilCentroPublico";

import "./App.css";

export default function App() {

    const [logueado, setLogueado] = useState(
        localStorage.getItem("token") !== null
    );

    return (
        <BrowserRouter>
            <Routes>

                {/* HU-05: Mapa de centros medicos */}
                <Route
                    path="/"
                    element={
                        <div>
                            <div className="mapa-enlace">
                                <Link to="/admin">Ingresar como administrador</Link>
                            </div>
                            <MapaCentros />
                        </div>
                    }
                />

                {/* HU-06: Perfil publico de un centro medico */}
                <Route
                    path="/centro/:id"
                    element={<PerfilCentroPublico />}
                />

                {/* Panel de administradores */}
                <Route
                    path="/admin"
                    element={
                        logueado ? (
                            <>
                                <Link to="/">Ver mapa de centros medicos</Link>

                                <Administrador
                                    alCerrarSesion={() => setLogueado(false)}
                                />

                                <Especialidades centroId={1} />
                            </>
                        ) : (
                            <Login
                                alIniciarSesion={() => setLogueado(true)}
                            />
                        )
                    }
                />

                {/* Redireccion a la pagina principal */}
                <Route
                    path="*"
                    element={<Navigate to="/" replace />}
                />

            </Routes>
        </BrowserRouter>
    );
}
