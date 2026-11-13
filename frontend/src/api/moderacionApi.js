// HU-14 (14.3): llamadas del panel de auditoría y moderación de reseñas (rol ADMIN)
const API = import.meta.env.VITE_API_URL ?? "http://localhost:8081";

async function pedirComoAdmin(ruta, opciones = {}) {
    const token = localStorage.getItem("token");
    const respuesta = await fetch(`${API}${ruta}`, {
        ...opciones,
        headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
            ...(opciones.headers ?? {}),
        },
    });

    if (!respuesta.ok) {
        let mensaje = `Error ${respuesta.status}`;
        try {
            const cuerpo = await respuesta.json();
            mensaje = cuerpo.message || cuerpo.mensaje || mensaje;
        } catch {
            // sin cuerpo JSON
        }
        const error = new Error(mensaje);
        error.status = respuesta.status;
        throw error;
    }
    return respuesta.json();
}

// Listado de reseñas. Filtros opcionales: { estado, centroId, conAlertas }
export function listarResenasAdmin(filtros = {}) {
    const params = new URLSearchParams();
    if (filtros.estado) params.set("estado", filtros.estado);
    if (filtros.centroId) params.set("centroId", String(filtros.centroId));
    if (filtros.conAlertas) params.set("conAlertas", "true");
    const consulta = params.toString();
    return pedirComoAdmin(`/api/admin/resenas${consulta ? `?${consulta}` : ""}`);
}

export function obtenerResumenModeracion() {
    return pedirComoAdmin("/api/admin/resenas/resumen");
}

// accion: APROBAR | RECHAZAR | OCULTAR
export function moderarResena(id, accion, { motivo, confirmarAlertas } = {}) {
    return pedirComoAdmin(`/api/admin/resenas/${id}/moderacion`, {
        method: "PATCH",
        body: JSON.stringify({ accion, motivo, confirmarAlertas: Boolean(confirmarAlertas) }),
    });
}
