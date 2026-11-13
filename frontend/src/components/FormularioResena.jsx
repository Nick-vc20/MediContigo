import "../resenas.css";
import "../paciente.css";
import { useState } from "react";
import { Link, useLocation } from "react-router-dom";
import { registrarResena } from "../api/resenasApi";
import { useSesionPaciente } from "../auth/sesionPaciente";

export default function FormularioResena({ centroId }) {

    // HU-18: solo un paciente con sesión iniciada puede publicar reseñas
    const sesion = useSesionPaciente();
    const location = useLocation();

    const [puntuacion, setPuntuacion] = useState(0);
    const [comentario, setComentario] = useState("");

    const [enviando, setEnviando] = useState(false);
    const [error, setError] = useState("");
    const [exito, setExito] = useState("");

    async function enviarResena(e) {
        e.preventDefault();

        setError("");
        setExito("");

        if (puntuacion < 1 || puntuacion > 5) {
            setError("Selecciona una calificación de 1 a 5 estrellas.");
            return;
        }

        if (comentario.trim().length < 10) {
            setError("El comentario debe tener al menos 10 caracteres.");
            return;
        }

        setEnviando(true);

        try {
            await registrarResena(centroId, {
                puntuacion,
                comentario: comentario.trim()
            });

            setExito(
                "¡Reseña registrada correctamente! " +
                "Tu opinión está pendiente de revisión."
            );

            setPuntuacion(0);
            setComentario("");

        } catch (err) {
            setError(
                err.message || "No se pudo registrar la reseña."
            );
        } finally {
            setEnviando(false);
        }
    }

    if (!sesion) {
        return (
            <section className="resena-contenedor">
                <h3>Deja tu opinión</h3>
                <p>
                    {error || "Inicia sesión como paciente para publicar una reseña de este centro médico."}
                </p>
                <div className="resena-login">
                    <Link
                        className="resena-enviar"
                        to="/paciente/login"
                        state={{ desde: location.pathname }}
                    >
                        Iniciar sesión
                    </Link>
                    <Link to="/paciente/registro">Crear una cuenta</Link>
                </div>
            </section>
        );
    }

    return (
        <section className="resena-contenedor">

            <h3>Deja tu opinión</h3>

            <p>
                ¿Cómo fue tu experiencia en este centro médico?
            </p>

            <form onSubmit={enviarResena} className="resena-formulario">

                <p className="resena-autor">
                    Publicarás como <strong>{sesion.paciente?.nombreCompleto}</strong>
                </p>

                <label>Tu calificación</label>

                <div className="resena-estrellas"
                     role="group"
                     aria-label="Calificación de 1 a 5 estrellas">

                    {[1, 2, 3, 4, 5].map(estrella => (
                        <button
                            key={estrella}
                            type="button"
                            className={
                                estrella <= puntuacion
                                    ? "estrella activa"
                                    : "estrella"
                            }
                            onClick={() => setPuntuacion(estrella)}
                            aria-label={`${estrella} estrellas`}
                            aria-pressed={puntuacion === estrella}
                            disabled={enviando}
                            style={{
                                color: estrella <= puntuacion
                                    ? "#f59e0b"
                                    : "#9ca3af",
                                fontSize: "28px"
                            }}
                        >
                            ★
                        </button>
                    ))}
                </div>

                <label htmlFor="resena-comentario">
                    Tu comentario
                </label>

                <textarea
                    id="resena-comentario"
                    placeholder="Cuéntanos cómo fue tu atención..."
                    value={comentario}
                    minLength={10}
                    maxLength={1000}
                    rows={4}
                    onChange={e => setComentario(e.target.value)}
                    disabled={enviando}
                    required
                />

                <small>
                    {comentario.length}/1000 caracteres
                </small>

                {error && (
                    <p className="resena-error" role="alert">
                        {error}
                    </p>
                )}

                {exito && (
                    <p className="resena-exito" role="status">
                        {exito}
                    </p>
                )}

                <button
                    type="submit"
                    className="resena-enviar"
                    disabled={enviando}
                >
                    {enviando
                        ? "Enviando..."
                        : "Enviar reseña"}
                </button>

            </form>
        </section>
    );
}
