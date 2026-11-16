// HU-12 (12.1): vista del historial de triajes realizados por el paciente
import { useEffect, useState } from "react";
import { obtenerMiHistorialTriaje } from "../api/triajesApi";

const NIVELES = {
    ALERTA_REPORTADA: "Señal de alarma reportada",
    ALERTA_NO_CONFIRMADA: "Señales de alarma no confirmadas",
    SIN_CLASIFICACION_CLINICA: "Orientación general",
};

function formatearFechaHora(valor) {
    if (!valor) return "";
    return new Date(valor).toLocaleString("es-PE", {
        day: "2-digit",
        month: "short",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit",
    });
}

export default function HistorialTriaje() {
    const [historial, setHistorial] = useState(null);
    const [error, setError] = useState("");
    const [abierto, setAbierto] = useState(null);

    useEffect(() => {
        let activo = true;
        obtenerMiHistorialTriaje()
            .then((datos) => {
                if (activo) setHistorial(datos);
            })
            .catch((err) => {
                // Un 401 cierra la sesión y RutaPaciente redirige al login
                if (activo && err.status !== 401) {
                    setError(err.status ? err.message : "No se pudo cargar tu historial de triaje");
                }
            });
        return () => {
            activo = false;
        };
    }, []);

    return (
        <section className="panel-tarjeta cuenta-seccion">
            <h3>Mi historial de triaje</h3>

            {error && (
                <p className="mensaje-error" role="alert">
                    {error}
                </p>
            )}

            {historial === null && !error && <p className="cuenta-vacio">Cargando...</p>}

            {historial?.length === 0 && (
                <p className="cuenta-vacio">
                    Aún no tienes resultados de triaje guardados. Aquí aparecerán cuando uses el
                    asistente de triaje por síntomas.
                </p>
            )}

            {historial?.length > 0 && (
                <ul className="triaje-lista">
                    {historial.map((t) => {
                        const expandido = abierto === t.id;
                        return (
                            <li key={t.id}>
                                <div className="cuenta-resena-cabecera">
                                    <small>{formatearFechaHora(t.fechaTriaje)}</small>
                                    <span className={`triaje-nivel nivel-${t.nivelUrgencia.toLowerCase()}`}>
                                        {NIVELES[t.nivelUrgencia] ?? t.nivelUrgencia}
                                    </span>
                                </div>

                                <p className="triaje-sintomas">
                                    <strong>Síntomas:</strong> {t.sintomas}
                                </p>

                                {t.especialidadRecomendada && (
                                    <p>
                                        <strong>Especialidad sugerida:</strong> {t.especialidadRecomendada}
                                    </p>
                                )}

                                <button
                                    type="button"
                                    className="triaje-ver-mas"
                                    aria-expanded={expandido}
                                    onClick={() => setAbierto(expandido ? null : t.id)}
                                >
                                    {expandido ? "Ocultar recomendación" : "Ver recomendación"}
                                </button>

                                {expandido && <p className="triaje-recomendacion">{t.recomendacion}</p>}
                            </li>
                        );
                    })}
                </ul>
            )}

            <p className="triaje-aviso">
                Los resultados del triaje son orientativos y no reemplazan una consulta médica.
            </p>
        </section>
    );
}