// HU-17 (17.1 / 17.4): formulario de registro del paciente
import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { registrarPaciente } from "../api/pacientesApi";
import { validarRegistro } from "../utils/validacionPaciente";
import "../login.css";
import "../paciente.css";

export default function RegistroPaciente() {
    const navigate = useNavigate();
    const [datos, setDatos] = useState({
        nombreCompleto: "",
        correo: "",
        contrasena: "",
        confirmacion: "",
        aceptaTratamientoDatos: false,
    });
    const [errores, setErrores] = useState({});
    const [errorGeneral, setErrorGeneral] = useState("");
    const [verContrasena, setVerContrasena] = useState(false);
    const [enviando, setEnviando] = useState(false);

    function cambiar(campo, valor) {
        setDatos((previo) => ({ ...previo, [campo]: valor }));
        setErrores((previo) => ({ ...previo, [campo]: undefined }));
    }

    async function enviar(e) {
        e.preventDefault();
        setErrorGeneral("");

        const encontrados = validarRegistro(datos);
        setErrores(encontrados);
        if (Object.keys(encontrados).length > 0) {
            return;
        }

        setEnviando(true);
        try {
            const paciente = await registrarPaciente({
                nombreCompleto: datos.nombreCompleto.trim(),
                correo: datos.correo.trim(),
                contrasena: datos.contrasena,
                aceptaTratamientoDatos: datos.aceptaTratamientoDatos,
            });
            navigate("/paciente/login", {
                replace: true,
                state: { registrado: true, correo: paciente.correo },
            });
        } catch (err) {
            if (err.status === 409) {
                setErrores({ correo: err.message });
            } else if (err.status === 400) {
                setErrorGeneral(err.message);
            } else {
                setErrorGeneral(err.status ? err.message : "No se pudo conectar con el servidor");
            }
        } finally {
            setEnviando(false);
        }
    }

    function errorDe(campo) {
        return errores[campo] ? (
            <p className="campo-error" id={`registro-${campo}-error`} role="alert">
                {errores[campo]}
            </p>
        ) : null;
    }

    function propsError(campo) {
        return errores[campo]
            ? { "aria-invalid": true, "aria-describedby": `registro-${campo}-error` }
            : {};
    }

    return (
        <div className="login-pagina">
            <div className="login-lado">
                <div className="login-marca">
                    <span className="login-logo" aria-hidden="true"></span>
                    <span className="login-marca-nombre">MediContigo</span>
                </div>

                <h1>Crea tu cuenta de paciente</h1>
                <p>Regístrate gratis con tu correo para aprovechar todo MediContigo.</p>

                <ul className="login-beneficios">
                    <li>Guarda tu historial de triaje</li>
                    <li>Publica reseñas de los centros que visitaste</li>
                    <li>Consulta tus reseñas cuando quieras</li>
                </ul>
            </div>

            <div className="login-formulario">
                <div className="login-caja">
                    <h2>Registro</h2>
                    <p className="subtitulo">Completa tus datos para crear tu cuenta</p>

                    <form onSubmit={enviar} noValidate>
                        <div className="campo">
                            <label htmlFor="registro-nombre">Nombre completo</label>
                            <input
                                id="registro-nombre"
                                type="text"
                                autoComplete="name"
                                placeholder="Ej. Ana Torres"
                                maxLength={80}
                                value={datos.nombreCompleto}
                                onChange={(e) => cambiar("nombreCompleto", e.target.value)}
                                disabled={enviando}
                                {...propsError("nombreCompleto")}
                            />
                            {errorDe("nombreCompleto")}
                        </div>

                        <div className="campo">
                            <label htmlFor="registro-correo">Correo</label>
                            <input
                                id="registro-correo"
                                type="email"
                                autoComplete="email"
                                placeholder="tu@correo.com"
                                maxLength={120}
                                value={datos.correo}
                                onChange={(e) => cambiar("correo", e.target.value)}
                                disabled={enviando}
                                {...propsError("correo")}
                            />
                            {errorDe("correo")}
                        </div>

                        <div className="campo">
                            <label htmlFor="registro-contrasena">Contraseña</label>
                            <div className="campo-password">
                                <input
                                    id="registro-contrasena"
                                    type={verContrasena ? "text" : "password"}
                                    autoComplete="new-password"
                                    placeholder="Mínimo 8 caracteres"
                                    maxLength={72}
                                    value={datos.contrasena}
                                    onChange={(e) => cambiar("contrasena", e.target.value)}
                                    disabled={enviando}
                                    {...propsError("contrasena")}
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
                            {errores.contrasena ? (
                                errorDe("contrasena")
                            ) : (
                                <p className="campo-ayuda">Usa letras y números (mínimo 8 caracteres).</p>
                            )}
                        </div>

                        <div className="campo">
                            <label htmlFor="registro-confirmacion">Confirmar contraseña</label>
                            <input
                                id="registro-confirmacion"
                                type={verContrasena ? "text" : "password"}
                                autoComplete="new-password"
                                placeholder="Repite tu contraseña"
                                maxLength={72}
                                value={datos.confirmacion}
                                onChange={(e) => cambiar("confirmacion", e.target.value)}
                                disabled={enviando}
                                {...propsError("confirmacion")}
                            />
                            {errorDe("confirmacion")}
                        </div>

                        <div className="campo campo-check">
                            <input
                                id="registro-datos"
                                type="checkbox"
                                checked={datos.aceptaTratamientoDatos}
                                onChange={(e) => cambiar("aceptaTratamientoDatos", e.target.checked)}
                                disabled={enviando}
                                {...propsError("aceptaTratamientoDatos")}
                            />
                            <label htmlFor="registro-datos">
                                Acepto el tratamiento de mis datos personales conforme a la
                                Ley N.° 29733, para gestionar mi cuenta, mi historial de triaje y mis reseñas.
                            </label>
                        </div>
                        {errorDe("aceptaTratamientoDatos")}

                        <button type="submit" className="boton" disabled={enviando}>
                            {enviando ? (
                                <>
                                    <span className="boton-spinner" aria-hidden="true"></span>
                                    Creando cuenta...
                                </>
                            ) : (
                                "Crear cuenta"
                            )}
                        </button>
                    </form>

                    {errorGeneral && (
                        <p className="mensaje-error" role="alert">
                            {errorGeneral}
                        </p>
                    )}

                    <p className="login-alterno">
                        ¿Ya tienes cuenta? <Link to="/paciente/login">Inicia sesión</Link>
                    </p>

                    <Link to="/" className="login-volver">
                        ← Volver al mapa de centros
                    </Link>
                </div>
            </div>
        </div>
    );
}
