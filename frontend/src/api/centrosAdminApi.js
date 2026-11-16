// HU-06 (mejora): llamadas que usa el administrador para dar de alta centros
// y ver cuáles hay registrados. Reutiliza los endpoints que ya existen en el backend.
const API = import.meta.env.VITE_API_URL ?? 'http://localhost:8081';

async function pedir(ruta, opciones = {}) {
  const r = await fetch(`${API}${ruta}`, opciones);

  if (!r.ok) {
    let mensaje = `Error ${r.status}`;
    try {
      const cuerpo = await r.json();
      // el registro responde { mensaje }, el resto de rutas responde { message }
      mensaje = cuerpo.mensaje || cuerpo.message || mensaje;
    } catch { /* sin cuerpo */ }
    throw new Error(mensaje);
  }

  return r.json();
}

// POST /api/centros-medicos (HU-01). Devuelve el centro con su id ya asignado
export const registrarCentro = (datos, token) =>
  pedir('/api/centros-medicos', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify(datos),
  });

// No hay un listado general en el backend. /api/centros (HU-05) devuelve los centros
// dentro de un radio, así que con un radio mayor que el planeta salen todos.
export const listarTodosLosCentros = () =>
  pedir('/api/centros?lat=0&lng=0&radio=40000');
