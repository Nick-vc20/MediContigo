// HU-18: sesión del paciente en el navegador (token JWT + vencimiento).
// Se guarda aparte del token del administrador para que los roles no se mezclen.
import { useEffect, useSyncExternalStore } from "react";

const CLAVE = "medicontigo.sesionPaciente";
const EVENTO = "medicontigo:sesion-paciente";

let ultimoTexto;
let ultimaSesion = null;
let motivoCierre = null;

function leer() {
    let texto = null;
    try {
        texto = localStorage.getItem(CLAVE);
    } catch {
        // Almacenamiento bloqueado: se trata como sesión cerrada
    }
    if (texto !== ultimoTexto) {
        ultimoTexto = texto;
        try {
            ultimaSesion = texto ? JSON.parse(texto) : null;
        } catch {
            ultimaSesion = null;
        }
    }
    return ultimaSesion;
}

function avisar() {
    window.dispatchEvent(new Event(EVENTO));
}

// Devuelve la sesión vigente o null (si venció, se elimina)
export function obtenerSesionPaciente() {
    const sesion = leer();
    if (sesion && (!sesion.token || Date.now() >= sesion.expiraEn)) {
        cerrarSesionLocal("expirada");
        return null;
    }
    return sesion;
}

export function guardarSesionPaciente({ token, expiraEn, paciente }) {
    motivoCierre = null;
    try {
        localStorage.setItem(CLAVE, JSON.stringify({ token, expiraEn, paciente }));
    } catch {
        // Sin almacenamiento la sesión no persiste
    }
    avisar();
}

// motivo: "logout" | "expirada"
export function cerrarSesionLocal(motivo = "logout") {
    motivoCierre = motivo;
    try {
        localStorage.removeItem(CLAVE);
    } catch {
        // nada que limpiar
    }
    avisar();
}

// Motivo del último cierre ("logout" | "expirada"), para mostrar "tu sesión expiró"
export function ultimoMotivoCierre() {
    return motivoCierre;
}

function suscribir(callback) {
    window.addEventListener(EVENTO, callback);
    window.addEventListener("storage", callback); // otras pestañas
    return () => {
        window.removeEventListener(EVENTO, callback);
        window.removeEventListener("storage", callback);
    };
}

// Snapshot estable: el mismo objeto mientras no cambie el almacenamiento; null si ya venció
function leerVigente() {
    const sesion = leer();
    return sesion && Date.now() < sesion.expiraEn ? sesion : null;
}

// Hook: sesión actual del paciente; se cierra sola cuando vence el token
export function useSesionPaciente() {
    const vigente = useSyncExternalStore(suscribir, leerVigente, () => null);
    const sesion = leer();

    useEffect(() => {
        if (!sesion) return undefined;
        const restante = sesion.expiraEn - Date.now();
        if (restante <= 0) {
            cerrarSesionLocal("expirada");
            return undefined;
        }
        // setTimeout admite como máximo ~24 días
        const id = setTimeout(() => cerrarSesionLocal("expirada"), Math.min(restante, 2 ** 31 - 1));
        return () => clearTimeout(id);
    }, [sesion]);

    return vigente;
}
