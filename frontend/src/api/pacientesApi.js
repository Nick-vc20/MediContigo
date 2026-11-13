// HU-17 / HU-18: llamadas al backend del paciente
import { cerrarSesionLocal, obtenerSesionPaciente } from "../auth/sesionPaciente";

const API = import.meta.env.VITE_API_URL ?? "http://localhost:8081";

async function leerError(respuesta, porDefecto) {
    let mensaje = porDefecto;
    try {
        const cuerpo = await respuesta.json();
        mensaje = cuerpo.message || cuerpo.mensaje || mensaje;
    } catch {
        // sin cuerpo JSON
    }
    const error = new Error(mensaje);
    error.status = respuesta.status;
    return error;
}

async function enviarJson(ruta, datos, porDefecto) {
    const respuesta = await fetch(`${API}${ruta}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(datos),
    });
    if (!respuesta.ok) {
        throw await leerError(respuesta, porDefecto);
    }
    return respuesta.json();
}

// Petición a una ruta privada del paciente: adjunta el token y cierra la sesión si el backend responde 401
export async function pedirComoPaciente(ruta, opciones = {}) {
    const sesion = obtenerSesionPaciente();
    if (!sesion) {
        const error = new Error("Tu sesión ha expirado. Inicia sesión nuevamente");
        error.status = 401;
        throw error;
    }

    const respuesta = await fetch(`${API}${ruta}`, {
        ...opciones,
        headers: {
            ...(opciones.body ? { "Content-Type": "application/json" } : {}),
            ...opciones.headers,
            Authorization: `Bearer ${sesion.token}`,
        },
    });

    if (respuesta.status === 401) {
        const error = await leerError(respuesta, "Tu sesión ha expirado. Inicia sesión nuevamente");
        cerrarSesionLocal("expirada");
        throw error;
    }
    if (!respuesta.ok) {
        throw await leerError(respuesta, `Error ${respuesta.status}`);
    }
    return respuesta.status === 204 ? null : respuesta.json();
}

// HU-17: registro
export const registrarPaciente = (datos) =>
    enviarJson("/api/pacientes/registro", datos, "No se pudo completar el registro");

// HU-18: inicio de sesión → { token, rol, expiraEn, paciente }
export const iniciarSesionPaciente = (correo, contrasena) =>
    enviarJson("/api/pacientes/login", { correo, contrasena }, "Correo o contraseña incorrectos");

// HU-18: cierre de sesión (revoca el token en el servidor)
export const cerrarSesionPaciente = () =>
    pedirComoPaciente("/api/paciente/logout", { method: "POST" });

export const obtenerMiPerfil = () => pedirComoPaciente("/api/paciente/perfil");

export const obtenerMisResenas = () => pedirComoPaciente("/api/paciente/resenas");
