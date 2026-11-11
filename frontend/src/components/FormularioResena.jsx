import "../resenas.css";
import { useState } from "react";
import { registrarResena } from "../api/resenasApi";

export default function FormularioResena({ centroId }) {

    const [nombre, setNombre] = useState("");
    const [puntuacion, setPuntuacion] = useState(0);
    const [comentario, setComentario] = useState("");

    const [enviando, setEnviando] = useState(false);
    const [error, setError] = useState("");
    const [exito, setExito] = useState("");

    async function enviarResena(e) {
        e.preventDefault();

        setError("");
        setExito("");

        if (nombre.trim().length < 2) {
            setError("Ingresa un nombre válido.");
            return;
        }

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
                nombrePaciente: nombre.trim(),
                puntuacion,
                comentario: comentario.trim()
            });

            setExito(
                "¡Reseña registrada correctamente! " +
                "Tu opinión está pendiente de revisión."
            );

            setNombre("");
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

    return (
        <section className="resena-contenedor">

            <h3>Deja tu opinión</h3>

            <p>
                ¿Cómo fue tu experiencia en este centro médico?
            </p>

            <form onSubmit={enviarResena} className="resena-formulario">

                <label htmlFor="resena-nombre">
                    Tu nombre
                </label>

                <input
                    id="resena-nombre"
                    type="text"
                    placeholder="Ingresa tu nombre"
                    value={nombre}
                    maxLength={80}
                    onChange={e => setNombre(e.target.value)}
                    disabled={enviando}
                    required
                />

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
