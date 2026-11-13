// HU-17: validaciones del registro de paciente
const FORMATO_CORREO = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
const FORMATO_CONTRASENA = /^(?=.*[A-Za-z])(?=.*\d).{8,72}$/;

// Mismas reglas que valida el backend (PacienteService)
export function validarRegistro({ nombreCompleto, correo, contrasena, confirmacion, aceptaTratamientoDatos }) {
    const errores = {};
    const nombre = nombreCompleto.trim();
    if (nombre.length < 2 || nombre.length > 80) {
        errores.nombreCompleto = "El nombre debe tener entre 2 y 80 caracteres.";
    }
    if (!FORMATO_CORREO.test(correo.trim())) {
        errores.correo = "Ingresa un correo electrónico válido.";
    }
    if (!FORMATO_CONTRASENA.test(contrasena)) {
        errores.contrasena = "Usa entre 8 y 72 caracteres, con letras y números.";
    }
    if (confirmacion !== contrasena) {
        errores.confirmacion = "Las contraseñas no coinciden.";
    }
    if (!aceptaTratamientoDatos) {
        errores.aceptaTratamientoDatos = "Debes aceptar el tratamiento de tus datos personales.";
    }
    return errores;
}
