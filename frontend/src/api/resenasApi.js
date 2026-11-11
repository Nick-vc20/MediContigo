
const API = import.meta.env.VITE_API_URL ?? "http://localhost:8081";

// Registrar una reseña nueva
export async function registrarResena(centroId, datos) {
    const respuesta = await fetch(
        `${API}/api/publico/centros/${centroId}/resenas`,
        {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(datos)
        }
    );

    if (!respuesta.ok) {
        let mensaje = "No se pudo registrar la reseña";

        try {
            const error = await respuesta.json();
            mensaje = error.message || error.detail || mensaje;
        } catch {
            // Se conserva el mensaje predeterminado
        }

        throw new Error(mensaje);
    }

    return respuesta.json();
}

// Consultar reseñas publicadas
export async function obtenerResenas(centroId) {
    const respuesta = await fetch(
        `${API}/api/publico/centros/${centroId}/resenas`
    );

    if (!respuesta.ok) {
        throw new Error("No se pudieron cargar las reseñas");
    }

    return respuesta.json();
}
