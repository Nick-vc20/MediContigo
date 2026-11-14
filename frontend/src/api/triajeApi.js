const API = import.meta.env.VITE_API_URL ?? "http://localhost:8081";

export async function validarCuestionario(datos) {
    let respuesta;

    try {
        respuesta = await fetch(`${API}/api/triaje`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(datos),
        });
    } catch {
        throw new Error(
            "No se pudo conectar con el servidor. Verifica que MediContigo esté funcionando."
        );
    }

    if (!respuesta.ok) {
        let mensaje = "No se pudo validar el cuestionario";

        try {
            const cuerpo = await respuesta.json();
            mensaje = cuerpo.detail || cuerpo.message || cuerpo.mensaje || mensaje;
        } catch {
            // El servidor no devolvió un error en formato JSON
        }

        throw new Error(mensaje);
    }

    return respuesta.json();
}
