// HU-18 (18.1 / 18.3): área privada del paciente con cierre de sesión
import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { cerrarSesionPaciente, obtenerMiPerfil, obtenerMisResenas } from "../api/pacientesApi";
import { cerrarSesionLocal, useSesionPaciente } from "../auth/sesionPaciente";
import "../login.css";
import "../paciente.css";

const ESTADOS = {
    PENDIENTE: "Pendiente de revisión",
    PUBLICADA: "Publicada",
    RECHAZADA: "No publicada",
    OCULTA: "Retirada por moderación",
};

function formatearFecha(valor) {
    if (!valor) return "";
    return new Date(valor).toLocaleDateString("es-PE", {
        day: "2-digit",
        month: "short",
        year: "numeric",
    });
}

export default function MiCuenta() {
    const navigate = useNavigate();
    const sesion = useSesionPaciente();
    const [perfil, setPerfil] = useState(sesion?.paciente ?? null);
    const [resenas, setResenas] = useState(null);
    const [error, setError] = useState("");
    const [saliendo, setSaliendo] = useState(false);

    useEffect(() => {
        let activo = true;
        Promise.all([obtenerMiPerfil(), obtenerMisResenas()])
            .then(([datosPerfil, datosResenas]) => {
                if (!activo) return;
                setPerfil(datosPerfil);
                setResenas(datosResenas);
            })
            .catch((err) => {
                // Un 401 cierra la sesión y RutaPaciente redirige al login
                if (activo && err.status !== 401) {
                    setError(err.status ? err.message : "No se pudo conectar con el servidor");
                }
            });
        return () => {
            activo = false;
        };
    }, []);

    async function cerrarSesion() {
        setSaliendo(true);
        try {
            await cerrarSesionPaciente();
        } catch {
            // Aunque el servidor no responda, la sesión local se cierra igual
        }
        cerrarSesionLocal("logout");
        navigate("/", { replace: true });
    }

    return (
        <div>
            <div className="panel-barra">
                <h2>Mi cuenta</h2>
                <button className="boton-salir" onClick={cerrarSesion} disabled={saliendo}>
                    {saliendo ? "Cerrando..." : "Cerrar sesión"}
                </button>
            </div>

            <div className="panel-contenido">
                <Link to="/">← Ver mapa de centros médicos</Link>

                <h1>Hola, {perfil?.nombreCompleto ?? "paciente"}</h1>
                {perfil?.correo && <p className="cuenta-correo">{perfil.correo}</p>}

                {error && (
                    <p className="mensaje-error" role="alert">
                        {error}
                    </p>
                )}

                <section className="panel-tarjeta cuenta-seccion">
                    <h3>Mi historial de triaje</h3>
                    <p className="cuenta-vacio">
                        Aún no tienes resultados de triaje guardados. Aquí aparecerán cuando uses el
                        asistente de triaje por síntomas.
                    </p>
                </section>

                <section className="panel-tarjeta cuenta-seccion">
                    <h3>Mis reseñas</h3>
                    {resenas === null && !error && <p className="cuenta-vacio">Cargando...</p>}
                    {resenas?.length === 0 && (
                        <p className="cuenta-vacio">
                            Todavía no has publicado reseñas. Busca un centro en el mapa y cuéntanos tu experiencia.
                        </p>
                    )}
                    {resenas?.length > 0 && (
                        <ul className="cuenta-resenas">
                            {resenas.map((r) => (
                                <li key={r.id}>
                                    <div className="cuenta-resena-cabecera">
                                        <Link to={`/centro/${r.centroId}`}>{r.centroNombre}</Link>
                                        <span className={`cuenta-estado estado-${r.estado.toLowerCase()}`}>
                                            {ESTADOS[r.estado] ?? r.estado}
                                        </span>
                                    </div>
                                    <div className="cuenta-estrellas" aria-label={`${r.puntuacion} de 5 estrellas`}>
                                        {"★".repeat(r.puntuacion)}
                                        <span>{"★".repeat(5 - r.puntuacion)}</span>
                                    </div>
                                    <p>{r.comentario}</p>
                                    {/* HU-14: motivo del administrador al rechazar u ocultar */}
                                    {r.motivoModeracion && (r.estado === "RECHAZADA" || r.estado === "OCULTA") && (
                                        <p className="cuenta-motivo">Motivo: {r.motivoModeracion}</p>
                                    )}
                                    <small>{formatearFecha(r.fechaCreacion)}</small>
                                </li>
                            ))}
                        </ul>
                    )}
                </section>
            </div>
        </div>
    );
}
