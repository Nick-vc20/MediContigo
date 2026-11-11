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


 // HU-07: 
export const buscarCentrosFiltrados = (lat, lng, radio, filtros = {}) => {
    const params = new URLSearchParams({
        lat: String(lat),
        lng: String(lng),
        radio: String(radio)
    });

    if (filtros.especialidad) {
        params.set("especialidad", filtros.especialidad);
    }

    if (filtros.tipo) {
        params.set("tipo", filtros.tipo);
    }

    if (filtros.maxCosto !== "" &&
        filtros.maxCosto !== undefined &&
        filtros.maxCosto !== null) {
        params.set("maxCosto", String(filtros.maxCosto));
    }

    if (filtros.hoy) {
        params.set("hoy", "true");
    }

    return pedir(`/api/centros/filtrados?${params.toString()}`);
};

// Obtener especialidades y tipos disponibles
export const obtenerOpcionesFiltros = (lat, lng, radio) => {
    const params = new URLSearchParams({
        lat: String(lat),
        lng: String(lng),
        radio: String(radio)
    });

    return pedir(`/api/centros/filtrados/opciones?${params.toString()}`);
};
