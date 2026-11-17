// HU-10 (10.3): llamada del administrador para actualizar el nivel de ocupación de un centro.
// El centro se identifica en la URL y el nivel viaja en el cuerpo: { nivel: 'BAJO' | 'MEDIO' | 'ALTO' }.
const API = import.meta.env.VITE_API_URL ?? 'http://localhost:8081';

export async function actualizarOcupacion(centroId, nivel, token) {
  const sesion = token ?? localStorage.getItem('token');

  const r = await fetch(`${API}/api/admin/centros/${centroId}/ocupacion`, {
    method: 'PATCH',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${sesion}`,
    },
    body: JSON.stringify({ nivel }),
  });

  if (!r.ok) {
    let mensaje = `Error ${r.status}`;
    try {
      const cuerpo = await r.json();
      mensaje = cuerpo.message || cuerpo.mensaje || mensaje;
    } catch { /* sin cuerpo */ }
    const error = new Error(mensaje);
    error.status = r.status;
    throw error;
  }

  // { centroId, nombre, nivelOcupacion, etiqueta, descripcion }
  return r.json();
}
