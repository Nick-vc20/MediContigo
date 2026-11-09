const API = import.meta.env.VITE_API_URL ?? 'http://localhost:8081';

async function pedir(ruta) {
  const r = await fetch(`${API}${ruta}`);
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
  return r.json();
}

// Centros dentro del radio (km), ordenados por distancia
export const buscarCentrosCercanos = (lat, lng, radio) =>
  pedir(`/api/centros?lat=${lat}&lng=${lng}&radio=${radio}`);

// Perfil publico consolidado: datos + especialidades + horarios + personal
export const obtenerPerfilCentro = (id) => pedir(`/api/publico/centros/${id}`);
