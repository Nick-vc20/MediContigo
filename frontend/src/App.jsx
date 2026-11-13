
import { useState } from "react";
import { BrowserRouter, Routes, Route, Navigate, Link } from "react-router-dom";

import Login from "./components/Login";
import Administrador from "./components/Administrador";
import Especialidades from "./components/Especialidades";
import MapaCentros from "./components/MapaCentros";
import PerfilCentroPublico from "./components/PerfilCentroPublico";
import RegistroPaciente from "./components/RegistroPaciente";
import LoginPaciente from "./components/LoginPaciente";
import MiCuenta from "./components/MiCuenta";
import RutaPaciente from "./components/RutaPaciente";
import { useSesionPaciente } from "./auth/sesionPaciente";

import "./App.css";

export default function App() {

    const [logueado, setLogueado] = useState(
        localStorage.getItem("token") !== null
    );

    // HU-18: sesión del paciente (independiente de la del administrador)
    const sesionPaciente = useSesionPaciente();

    return (
        <BrowserRouter>
            <Routes>

                {/* HU-05: Mapa de centros medicos */}
                <Route
                    path="/"
                    element={
                        <div>
                            <div className="mapa-enlace">
                                {sesionPaciente ? (
                                    <Link to="/paciente/cuenta">
                                        Mi cuenta ({sesionPaciente.paciente?.nombreCompleto})
                                    </Link>
                                ) : (
                                    <>
                                        <Link to="/paciente/login">Ingresar como paciente</Link>
                                        <Link to="/paciente/registro">Registrarme</Link>
                                    </>
                                )}
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

                {/* HU-17: Registro de paciente */}
                <Route path="/paciente/registro" element={<RegistroPaciente />} />

                {/* HU-18: Inicio de sesión del paciente */}
                <Route path="/paciente/login" element={<LoginPaciente />} />

                {/* HU-18: Rutas privadas del paciente (historial y reseñas) */}
                <Route
                    path="/paciente/cuenta"
                    element={
                        <RutaPaciente>
                            <MiCuenta />
                        </RutaPaciente>
                    }
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
