const API = import.meta.env.VITE_API_URL ?? 'http://localhost:8081';

async function pedir(ruta, opciones = {}) {
  const r = await fetch(`${API}${ruta}`, {
    headers: { 'Content-Type': 'application/json' },
    ...opciones,
  });
  if (!r.ok) {
    let mensaje = `Error ${r.status}`;
    try { mensaje = (await r.json()).message || mensaje; } catch { /* sin cuerpo */ }
    throw new Error(mensaje);
  }
  return r.status === 204 ? null : r.json();
}

const base = (centroId) => `/api/centros/${centroId}/especialidades`;

export const listarEspecialidades = (centroId, incluirInactivas) =>
  pedir(`${base(centroId)}?incluirInactivas=${incluirInactivas}`);

export const crearEspecialidad = (centroId, datos) =>
  pedir(base(centroId), { method: 'POST', body: JSON.stringify(datos) });

export const actualizarEspecialidad = (centroId, id, datos) =>
  pedir(`${base(centroId)}/${id}`, { method: 'PUT', body: JSON.stringify(datos) });

export const cambiarEstado = (centroId, id, activo) =>
  pedir(`${base(centroId)}/${id}/estado?activo=${activo}`, { method: 'PATCH' });

export const listarHorarios = (centroId, id) =>
  pedir(`${base(centroId)}/${id}/horarios`);

export const agregarHorario = (centroId, id, datos) =>
  pedir(`${base(centroId)}/${id}/horarios`, { method: 'POST', body: JSON.stringify(datos) });

export const eliminarHorario = (centroId, id, horarioId) =>
  pedir(`${base(centroId)}/${id}/horarios/${horarioId}`, { method: 'DELETE' });