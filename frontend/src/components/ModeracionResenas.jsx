// HU-14 (14.1 / 14.3): panel de auditoría y moderación de reseñas
import { useCallback, useEffect, useMemo, useState } from "react";
import {
    listarResenasAdmin,
    moderarResena,
    obtenerResumenModeracion,
} from "../api/moderacionApi";
import "../moderacion.css";

const PESTANAS = [
    { id: "PENDIENTE", etiqueta: "Pendientes", campo: "pendientes" },
    { id: "ALERTAS", etiqueta: "Con alertas", campo: "pendientesConAlertas" },
    { id: "PUBLICADA", etiqueta: "Publicadas", campo: "publicadas" },
    { id: "RECHAZADA", etiqueta: "Rechazadas", campo: "rechazadas" },
    { id: "OCULTA", etiqueta: "Ocultas", campo: "ocultas" },
    { id: "TODAS", etiqueta: "Todas", campo: "total" },
];

const ESTADOS = {
    PENDIENTE: "Pendiente",
    PUBLICADA: "Publicada",
    RECHAZADA: "Rechazada",
    OCULTA: "Oculta",
};

const ALERTAS = {
    LENGUAJE_OFENSIVO: "Lenguaje ofensivo",
    DATOS_PERSONALES: "Datos personales",
    ENLACE_EXTERNO: "Enlace externo",
    SPAM: "Posible spam",
};

const MOTIVOS_RAPIDOS = {
    RECHAZAR: [
        "Contiene lenguaje ofensivo",
        "Incluye datos personales",
        "Publicidad o enlaces externos",
        "No describe una experiencia de atención",
    ],
    OCULTAR: [
        "Denuncia de otro usuario en revisión",
        "Información incorrecta sobre el centro",
        "Contiene datos personales",
    ],
};

function formatearFecha(valor) {
    if (!valor) return "";
    return new Date(valor).toLocaleString("es-PE", {
        day: "2-digit",
        month: "short",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit",
    });
}

export default function ModeracionResenas({ alExpirarSesion }) {
    const [resenas, setResenas] = useState(null);
    const [resumen, setResumen] = useState(null);
    const [error, setError] = useState("");
    const [aviso, setAviso] = useState("");

    const [pestana, setPestana] = useState("PENDIENTE");
    const [centroId, setCentroId] = useState("");
    const [busqueda, setBusqueda] = useState("");

    // Reseña en edición: { id, accion: "RECHAZAR" | "OCULTAR" | "CONFIRMAR" }
    const [edicion, setEdicion] = useState(null);
    const [motivo, setMotivo] = useState("");
    const [procesando, setProcesando] = useState(null);

    const manejarError = useCallback((e) => {
        if (e.status === 401) {
            localStorage.removeItem("token");
            alExpirarSesion?.();
            return;
        }
        setError(e.message || "No se pudo conectar con el servidor");
    }, [alExpirarSesion]);

    const cargar = useCallback(async () => {
        setError("");
        try {
            const [lista, conteo] = await Promise.all([
                listarResenasAdmin(),
                obtenerResumenModeracion(),
            ]);
            setResenas(lista);
            setResumen(conteo);
        } catch (e) {
            manejarError(e);
        }
    }, [manejarError]);

    useEffect(() => {
        cargar();
    }, [cargar]);

    const centros = useMemo(() => {
        const mapa = new Map();
        (resenas ?? []).forEach((r) => mapa.set(r.centroId, r.centroNombre));
        return [...mapa.entries()].sort((a, b) => a[1].localeCompare(b[1]));
    }, [resenas]);

    const visibles = useMemo(() => {
        const texto = busqueda.trim().toLowerCase();
        return (resenas ?? []).filter((r) => {
            if (pestana === "ALERTAS") {
                if (r.estado !== "PENDIENTE" || r.alertas.length === 0) return false;
            } else if (pestana !== "TODAS" && r.estado !== pestana) {
                return false;
            }
            if (centroId && String(r.centroId) !== centroId) return false;
            if (texto) {
                const enTexto = `${r.comentario} ${r.nombrePaciente} ${r.centroNombre}`.toLowerCase();
                if (!enTexto.includes(texto)) return false;
            }
            return true;
        });
    }, [resenas, pestana, centroId, busqueda]);

    function abrir(id, accion) {
        setEdicion({ id, accion });
        setMotivo("");
        setAviso("");
        setError("");
    }

    function cancelar() {
        setEdicion(null);
        setMotivo("");
    }

    async function ejecutar(resena, accion, opciones = {}) {
        setProcesando(resena.id);
        setError("");
        setAviso("");
        try {
            const actualizada = await moderarResena(resena.id, accion, opciones);
            setResenas((lista) => lista.map((r) => (r.id === actualizada.id ? actualizada : r)));
            setResumen(await obtenerResumenModeracion());
            setAviso(`Reseña de ${actualizada.nombrePaciente}: ${ESTADOS[actualizada.estado].toLowerCase()}.`);
            cancelar();
        } catch (e) {
            manejarError(e);
        } finally {
            setProcesando(null);
        }
    }

    function aprobar(resena) {
        // El filtro de contenido marcó la reseña: se pide una confirmación explícita
        if (resena.alertas.length > 0) {
            abrir(resena.id, "CONFIRMAR");
            return;
        }
        ejecutar(resena, "APROBAR");
    }

    return (
        <section className="moderacion" aria-labelledby="moderacion-titulo">
            <header className="moderacion-cabecera">
                <div>
                    <h3 id="moderacion-titulo">Auditoría de reseñas</h3>
                    <p>Revisa las opiniones de los pacientes antes de que aparezcan en el perfil público.</p>
                </div>
                <button type="button" className="moderacion-recargar" onClick={cargar}>
                    Actualizar
                </button>
            </header>

            <div className="moderacion-pestanas" role="tablist" aria-label="Estado de las reseñas">
                {PESTANAS.map((p) => (
                    <button
                        key={p.id}
                        type="button"
                        role="tab"
                        aria-selected={pestana === p.id}
                        className={`moderacion-pestana pestana-${p.id.toLowerCase()}`}
                        onClick={() => setPestana(p.id)}
                    >
                        {p.etiqueta}
                        <span className="moderacion-contador">{resumen ? resumen[p.campo] : "–"}</span>
                    </button>
                ))}
            </div>

            <div className="moderacion-filtros">
                <label>
                    Centro médico
                    <select value={centroId} onChange={(e) => setCentroId(e.target.value)}>
                        <option value="">Todos los centros</option>
                        {centros.map(([id, nombre]) => (
                            <option key={id} value={String(id)}>{nombre}</option>
                        ))}
                    </select>
                </label>
                <label>
                    Buscar
                    <input
                        type="search"
                        placeholder="Texto, paciente o centro"
                        value={busqueda}
                        onChange={(e) => setBusqueda(e.target.value)}
                    />
                </label>
            </div>

            {error && <p className="moderacion-mensaje error" role="alert">{error}</p>}
            {aviso && <p className="moderacion-mensaje exito" role="status">{aviso}</p>}

            {resenas === null && !error && <p className="moderacion-vacio">Cargando reseñas...</p>}
            {resenas !== null && visibles.length === 0 && (
                <p className="moderacion-vacio">No hay reseñas en esta vista.</p>
            )}

            <ul className="moderacion-lista">
                {visibles.map((r) => {
                    const enEdicion = edicion?.id === r.id ? edicion.accion : null;
                    const ocupado = procesando === r.id;
                    return (
                        <li key={r.id} className={`moderacion-item ${r.alertas.length ? "con-alertas" : ""}`}>
                            <div className="moderacion-item-cabecera">
                                <div>
                                    <strong>{r.centroNombre}</strong>
                                    <span className="moderacion-autor">
                                        {r.nombrePaciente} · {formatearFecha(r.fechaCreacion)}
                                    </span>
                                </div>
                                <span className={`moderacion-estado estado-${r.estado.toLowerCase()}`}>
                                    {ESTADOS[r.estado] ?? r.estado}
                                </span>
                            </div>

                            <div className="moderacion-estrellas" aria-label={`${r.puntuacion} de 5 estrellas`}>
                                {"★".repeat(r.puntuacion)}
                                <span>{"★".repeat(5 - r.puntuacion)}</span>
                            </div>

                            <p className="moderacion-comentario">{r.comentario}</p>

                            {r.alertas.length > 0 && (
                                <ul className="moderacion-alertas" aria-label="Alertas del filtro de contenido">
                                    {r.alertas.map((a) => (
                                        <li key={a}>⚠ {ALERTAS[a] ?? a}</li>
                                    ))}
                                </ul>
                            )}

                            {r.moderadoPor && (
                                <p className="moderacion-auditoria">
                                    {ESTADOS[r.estado]} por {r.moderadoPor} el {formatearFecha(r.fechaModeracion)}
                                    {r.motivoModeracion && <> — <em>{r.motivoModeracion}</em></>}
                                </p>
                            )}

                            {(enEdicion === "RECHAZAR" || enEdicion === "OCULTAR") && (
                                <form
                                    className="moderacion-motivo"
                                    onSubmit={(e) => {
                                        e.preventDefault();
                                        ejecutar(r, enEdicion, { motivo });
                                    }}
                                >
                                    <label htmlFor={`motivo-${r.id}`}>
                                        Motivo para {enEdicion === "RECHAZAR" ? "rechazar" : "ocultar"} (lo verá el paciente)
                                    </label>
                                    <div className="moderacion-motivos-rapidos">
                                        {MOTIVOS_RAPIDOS[enEdicion].map((m) => (
                                            <button key={m} type="button" onClick={() => setMotivo(m)}>{m}</button>
                                        ))}
                                    </div>
                                    <textarea
                                        id={`motivo-${r.id}`}
                                        rows={2}
                                        maxLength={255}
                                        value={motivo}
                                        onChange={(e) => setMotivo(e.target.value)}
                                        required
                                        minLength={5}
                                    />
                                    <div className="moderacion-acciones">
                                        <button type="submit" className="accion peligro" disabled={ocupado}>
                                            {ocupado ? "Guardando..." : `Confirmar ${enEdicion === "RECHAZAR" ? "rechazo" : "ocultamiento"}`}
                                        </button>
                                        <button type="button" className="accion" onClick={cancelar}>Cancelar</button>
                                    </div>
                                </form>
                            )}

                            {enEdicion === "CONFIRMAR" && (
                                <div className="moderacion-confirmar" role="alertdialog" aria-label="Confirmar publicación">
                                    <p>
                                        El filtro automático marcó esta reseña. ¿Revisaste el contenido y deseas
                                        publicarla de todas formas?
                                    </p>
                                    <div className="moderacion-acciones">
                                        <button
                                            type="button"
                                            className="accion aprobar"
                                            disabled={ocupado}
                                            onClick={() => ejecutar(r, "APROBAR", { confirmarAlertas: true })}
                                        >
                                            {ocupado ? "Publicando..." : "Sí, publicar"}
                                        </button>
                                        <button type="button" className="accion" onClick={cancelar}>Cancelar</button>
                                    </div>
                                </div>
                            )}

                            {!enEdicion && (
                                <div className="moderacion-acciones">
                                    {r.estado === "PENDIENTE" && (
                                        <>
                                            <button type="button" className="accion aprobar" disabled={ocupado} onClick={() => aprobar(r)}>
                                                Aprobar
                                            </button>
                                            <button type="button" className="accion peligro" disabled={ocupado} onClick={() => abrir(r.id, "RECHAZAR")}>
                                                Rechazar
                                            </button>
                                        </>
                                    )}
                                    {r.estado === "PUBLICADA" && (
                                        <button type="button" className="accion peligro" disabled={ocupado} onClick={() => abrir(r.id, "OCULTAR")}>
                                            Ocultar
                                        </button>
                                    )}
                                    {r.estado === "OCULTA" && (
                                        <button type="button" className="accion aprobar" disabled={ocupado} onClick={() => aprobar(r)}>
                                            Restaurar
                                        </button>
                                    )}
                                    {r.estado === "RECHAZADA" && (
                                        <button type="button" className="accion" disabled={ocupado} onClick={() => aprobar(r)}>
                                            Reconsiderar y publicar
                                        </button>
                                    )}
                                </div>
                            )}
                        </li>
                    );
                })}
            </ul>
        </section>
    );
}
