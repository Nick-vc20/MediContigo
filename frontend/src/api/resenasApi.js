import { pedirComoPaciente } from "./pacientesApi";

const API = import.meta.env.VITE_API_URL ?? "http://localhost:8081";

// HU-13 + HU-18: registrar una reseña (solo pacientes con sesión iniciada)
export function registrarResena(centroId, datos) {
    return pedirComoPaciente(`/api/paciente/centros/${centroId}/resenas`, {
        method: "POST",
        body: JSON.stringify(datos),
    });
}

// Consultar reseñas publicadas (público)
export async function obtenerResenas(centroId) {
    const respuesta = await fetch(
        `${API}/api/publico/centros/${centroId}/resenas`
    );

    if (!respuesta.ok) {
        throw new Error("No se pudieron cargar las reseñas");
    }

    return respuesta.json();
}
