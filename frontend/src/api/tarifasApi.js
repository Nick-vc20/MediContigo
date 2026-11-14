const API = import.meta.env.VITE_API_URL ?? 'http://localhost:8081';

async function pedir(ruta, opciones = {}) {
  const respuesta = await fetch(`${API}${ruta}`, {
    headers: { 'Content-Type': 'application/json' },
    ...opciones,
  });

  if (!respuesta.ok) {
    let mensaje = `Error ${respuesta.status}`;

    try {
      const cuerpo = await respuesta.json();
      mensaje = cuerpo.message || cuerpo.mensaje || mensaje;
    } catch {
      // La respuesta no contiene un cuerpo JSON.
    }

    const error = new Error(mensaje);
    error.status = respuesta.status;
    throw error;
  }

  return respuesta.status === 204 ? null : respuesta.json();
}

const base = (centroId) => `/api/centros/${centroId}/tarifas`;

// HU-08: Obtener todas las tarifas de un centro medico.
export const listarTarifas = (centroId) =>
  pedir(base(centroId));

// HU-08: Consultar la tarifa de una especialidad.
export const obtenerTarifa = (centroId, especialidadId) =>
  pedir(`${base(centroId)}/${especialidadId}`);

// HU-08: Registrar o actualizar el precio de una especialidad.
export const guardarTarifa = (centroId, especialidadId, datos) =>
  pedir(`${base(centroId)}/${especialidadId}`, {
    method: 'PUT',
    body: JSON.stringify(datos),
  });
