
import { useState } from "react";
import { BrowserRouter, Routes, Route, Navigate, Link } from "react-router-dom";

import Login from "./components/Login";
import Administrador from "./components/Administrador";
import Especialidades from "./components/Especialidades";
import ModeracionResenas from "./components/ModeracionResenas";
import MapaCentros from "./components/MapaCentros";
import PerfilCentroPublico from "./components/PerfilCentroPublico";
import RegistroPaciente from "./components/RegistroPaciente";
import LoginPaciente from "./components/LoginPaciente";
import MiCuenta from "./components/MiCuenta";
import RutaPaciente from "./components/RutaPaciente";

// HU-11: Cuestionario de orientación según síntomas
import CuestionarioSintomas from "./components/CuestionarioSintomas";

import { useSesionPaciente } from "./auth/sesionPaciente";

import "./App.css";
import "./paciente.css";

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
                            <nav className="mapa-enlace" aria-label="Acceso de usuarios">

                                {/* HU-11: Acceso al cuestionario de síntomas */}
                                <Link className="mapa-boton" to="/triaje">
                                    Orientación por síntomas
                                </Link>

                                {sesionPaciente ? (
                                    // HU-18: con sesión de paciente se muestra su cuenta
                                    <Link className="mapa-boton principal" to="/paciente/cuenta">
                                        <span aria-hidden="true">👤</span>
                                        Mi cuenta ({sesionPaciente.paciente?.nombreCompleto})
                                    </Link>
                                ) : (
                                    <>
                                        <Link className="mapa-boton principal" to="/paciente/registro">
                                            Registrarme
                                        </Link>
                                        <Link className="mapa-boton" to="/paciente/login">
                                            Ingresar como paciente
                                        </Link>
                                        <Link className="mapa-boton secundario" to="/admin">
                                            Ingresar como administrador
                                        </Link>
                                    </>
                                )}

                            </nav>

                            <MapaCentros />
                        </div>
                    }
                />

                {/* HU-11: Orientación al paciente según sus síntomas */}
                <Route
                    path="/triaje"
                    element={<CuestionarioSintomas />}
                />

                {/* HU-06: Perfil publico de un centro medico */}
                <Route
                    path="/centro/:id"
                    element={<PerfilCentroPublico />}
                />

                {/* HU-17: Registro de paciente */}
                <Route
                    path="/paciente/registro"
                    element={<RegistroPaciente />}
                />

                {/* HU-18: Inicio de sesión del paciente */}
                <Route
                    path="/paciente/login"
                    element={<LoginPaciente />}
                />

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
                                <Link to="/">
                                    Ver mapa de centros medicos
                                </Link>

                                <Administrador
                                    alCerrarSesion={() => setLogueado(false)}
                                />

                                <Especialidades centroId={1} />

                                {/* HU-14: Auditoría y moderación de reseñas */}
                                <div className="panel-contenido">
                                    <ModeracionResenas
                                        alExpirarSesion={() => setLogueado(false)}
                                    />
                                </div>
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
