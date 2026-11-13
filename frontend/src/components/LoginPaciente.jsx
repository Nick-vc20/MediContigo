// HU-18 (18.1): inicio de sesión del paciente
import { useState } from "react";
import { Link, Navigate, useLocation, useNavigate } from "react-router-dom";
import { iniciarSesionPaciente } from "../api/pacientesApi";
import { guardarSesionPaciente, useSesionPaciente } from "../auth/sesionPaciente";
import "../login.css";
import "../paciente.css";

export default function LoginPaciente() {
    const navigate = useNavigate();
    const location = useLocation();
    const sesion = useSesionPaciente();
    const estado = location.state ?? {};
    const destino = estado.desde || "/paciente/cuenta";

    const [correo, setCorreo] = useState(estado.correo ?? "");
    const [contrasena, setContrasena] = useState("");
    const [verContrasena, setVerContrasena] = useState(false);
    const [error, setError] = useState("");
    const [cargando, setCargando] = useState(false);
    const [aviso] = useState(() => {
        if (estado.registrado) return "¡Cuenta creada! Ya puedes iniciar sesión.";
        if (estado.motivo === "expirada") {
            return "Tu sesión ha expirado. Inicia sesión nuevamente.";
        }
        return "";
    });

    // Si ya hay sesión, no tiene sentido mostrar el login
    if (sesion && !cargando) {
        return <Navigate to={destino} replace />;
    }

    async function enviar(e) {
        e.preventDefault();
        setError("");
        setCargando(true);
        try {
            const datos = await iniciarSesionPaciente(correo.trim(), contrasena);
            guardarSesionPaciente(datos);
            navigate(destino, { replace: true });
        } catch (err) {
            setError(err.status ? err.message : "No se pudo conectar con el servidor");
            setCargando(false);
        }
    }

    return (
        <div className="login-pagina">
            <div className="login-lado">
                <div className="login-marca">
                    <span className="login-logo" aria-hidden="true"></span>
                    <span className="login-marca-nombre">MediContigo</span>
                </div>

                <h1>Hola de nuevo</h1>
                <p>Inicia sesión para acceder de forma segura a tu historial y a tus reseñas.</p>

                <ul className="login-beneficios">
                    <li>Tu historial de triaje</li>
                    <li>Tus reseñas publicadas</li>
                    <li>Opina sobre los centros que visitaste</li>
                </ul>
            </div>

            <div className="login-formulario">
                <div className="login-caja">
                    <h2>Iniciar sesión</h2>
                    <p className="subtitulo">Ingresa con tu cuenta de paciente</p>

                    {aviso && (
                        <p className={estado.registrado ? "mensaje-exito" : "mensaje-aviso"} role="status">
                            {aviso}
                        </p>
                    )}

                    <form onSubmit={enviar}>
                        <div className="campo">
                            <label htmlFor="paciente-correo">Correo</label>
                            <input
                                id="paciente-correo"
                                type="email"
                                placeholder="tu@correo.com"
                                autoComplete="username"
                                value={correo}
                                onChange={(e) => setCorreo(e.target.value)}
                                required
                            />
                        </div>

                        <div className="campo">
                            <label htmlFor="paciente-contrasena">Contraseña</label>
                            <div className="campo-password">
                                <input
                                    id="paciente-contrasena"
                                    type={verContrasena ? "text" : "password"}
                                    placeholder="Tu contraseña"
                                    autoComplete="current-password"
                                    value={contrasena}
                                    onChange={(e) => setContrasena(e.target.value)}
                                    required
                                />
                                <button
                                    type="button"
                                    className="ver-password"
                                    aria-pressed={verContrasena}
                                    onClick={() => setVerContrasena(!verContrasena)}
                                >
                                    {verContrasena ? "Ocultar" : "Mostrar"}
                                </button>
                            </div>
                        </div>

                        <button type="submit" className="boton" disabled={cargando}>
                            {cargando ? (
                                <>
                                    <span className="boton-spinner" aria-hidden="true"></span>
                                    Entrando...
                                </>
                            ) : (
                                "Entrar"
                            )}
                        </button>
                    </form>

                    {error && (
                        <p className="mensaje-error" role="alert">
                            {error}
                        </p>
                    )}

                    <p className="login-alterno">
                        ¿No tienes cuenta? <Link to="/paciente/registro">Regístrate</Link>
                    </p>

                    <Link to="/" className="login-volver">
                        ← Volver al mapa de centros
                    </Link>
                </div>
            </div>
        </div>
    );
}
