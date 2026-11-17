import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';

import { listarTodosLosCentros } from '../api/centrosAdminApi';
import { actualizarOcupacion } from '../api/ocupacionApi';
import { ocupacionDe } from '../utils/formato';

import '../ocupacion.css';

// Mismos niveles y colores que ve el paciente en el mapa y en el perfil (ver ocupacionDe)
const NIVELES = [
  { valor: 'BAJO', titulo: 'Baja', detalle: 'Sin espera', clase: 'bajo' },
  { valor: 'MEDIO', titulo: 'Media', detalle: 'Espera regular', clase: 'medio' },
  { valor: 'ALTO', titulo: 'Alta', detalle: 'Colapsado', clase: 'alto' },
];

const horaDe = (fecha) =>
  fecha.toLocaleTimeString('es-PE', { hour: '2-digit', minute: '2-digit', hour12: false });

function mensajeDeError(e) {
  if (e.status === 403) return 'Tu cuenta no tiene permisos para cambiar la ocupación.';
  if (e.status === undefined) return 'No se pudo conectar con el servidor. Intenta de nuevo.';
  return e.message || 'No se pudo actualizar la ocupación.';
}

/**
 * HU-10 (10.1 y 10.3): control administrativo del nivel de ocupación.
 * - Lista los centros registrados y deja elegir el que se quiere actualizar.
 * - Un clic guarda el nivel (PATCH /api/admin/centros/{id}/ocupacion) y se refleja
 *   enseguida en el mapa y en el perfil público.
 * Props opcionales: centroId (centro preseleccionado), tokenAdmin y alExpirarSesion.
 */
export function ControlOcupacion({ centroId: centroInicial, tokenAdmin, alExpirarSesion }) {
  const [centros, setCentros] = useState([]);
  const [cargando, setCargando] = useState(true);
  const [errorLista, setErrorLista] = useState('');
  const [centroId, setCentroId] = useState(centroInicial ?? null);
  const [guardando, setGuardando] = useState(null); // nivel que se está guardando
  const [aviso, setAviso] = useState(null); // { tipo: 'ok' | 'error', texto, hora }

  useEffect(() => {
    let vigente = true;

    listarTodosLosCentros()
      .then((lista) => {
        if (!vigente) return;
        lista.sort((a, b) => a.nombre.localeCompare(b.nombre, 'es'));
        setCentros(lista);
        setCentroId((actual) =>
          lista.some((c) => c.id === actual) ? actual : (lista[0]?.id ?? null)
        );
      })
      .catch((e) => {
        if (vigente) setErrorLista(e.message || 'No se pudo cargar la lista de centros.');
      })
      .finally(() => {
        if (vigente) setCargando(false);
      });

    return () => {
      vigente = false;
    };
  }, []);

  const activo = centros.find((c) => c.id === centroId) ?? null;
  const actual = ocupacionDe(activo?.nivelOcupacion);

  const fijarNivel = (id, nivel) =>
    setCentros((lista) => lista.map((c) => (c.id === id ? { ...c, nivelOcupacion: nivel } : c)));

  async function elegir(nivel) {
    if (!activo || guardando || nivel === activo.nivelOcupacion) return;

    const { id, nivelOcupacion: anterior } = activo;

    setAviso(null);
    setGuardando(nivel);
    fijarNivel(id, nivel); // el cambio se ve al instante; si falla se devuelve al nivel anterior

    try {
      const r = await actualizarOcupacion(id, nivel, tokenAdmin);
      fijarNivel(id, r.nivelOcupacion);
      setAviso({
        tipo: 'ok',
        texto: `${r.nombre} ahora figura con ${r.etiqueta.toLowerCase()} (${r.descripcion.toLowerCase()}).`,
        hora: horaDe(new Date()),
      });
    } catch (e) {
      fijarNivel(id, anterior);

      if (e.status === 401) {
        localStorage.removeItem('token');
        alExpirarSesion?.();
        return;
      }
      setAviso({ tipo: 'error', texto: mensajeDeError(e) });
    } finally {
      setGuardando(null);
    }
  }

  function cambiarCentro(ev) {
    setCentroId(Number(ev.target.value));
    setAviso(null);
  }

  // Se usa <div> y no <section>: index.css aplica un estilo de tarjeta a todos los <section>
  return (
    <div className="oc" role="region" aria-labelledby="oc-titulo">
      <h3 id="oc-titulo">Ocupación del establecimiento</h3>
      <p className="oc-ayuda">
        Elige el nivel de atención actual del centro. Se guarda al instante y los pacientes lo ven
        en el mapa y en el perfil del centro.
      </p>

      {cargando && <p className="oc-estado" role="status">Cargando centros...</p>}
      {errorLista && <p className="oc-error" role="alert">{errorLista}</p>}

      {!cargando && !errorLista && centros.length === 0 && (
        <p className="oc-estado">Todavía no hay centros registrados.</p>
      )}

      {activo && (
        <>
          <div className="oc-selector">
            <label htmlFor="oc-centro">Centro</label>
            <select id="oc-centro" value={activo.id} onChange={cambiarCentro} disabled={Boolean(guardando)}>
              {centros.map((c) => (
                <option key={c.id} value={c.id}>{c.nombre} ({c.tipo})</option>
              ))}
            </select>
          </div>

          <p className="oc-actual">
            Nivel actual:{' '}
            <span className={`oc-badge oc-${actual.clase}`}>{actual.etiqueta}</span>
          </p>

          <fieldset className="oc-niveles" disabled={Boolean(guardando)}>
            <legend className="oc-oculto">Nivel de ocupación</legend>

            {NIVELES.map((n) => {
              const elegido = activo.nivelOcupacion === n.valor;
              return (
                <label key={n.valor} className={`oc-nivel oc-${n.clase}${elegido ? ' activo' : ''}`}>
                  <input
                    type="radio"
                    name="oc-nivel"
                    value={n.valor}
                    checked={elegido}
                    onChange={() => elegir(n.valor)}
                  />
                  <span className="oc-punto" aria-hidden="true" />
                  <strong>{n.titulo}</strong>
                  <span className="oc-detalle">{n.detalle}</span>
                  {guardando === n.valor && <span className="oc-spinner" aria-hidden="true" />}
                </label>
              );
            })}
          </fieldset>

          <div className="oc-pie" aria-live="polite">
            {guardando && <p className="oc-estado">Guardando y sincronizando con la vista pública...</p>}

            {aviso?.tipo === 'ok' && (
              <p className="oc-ok" role="status">
                ✓ {aviso.texto} Actualizado a las {aviso.hora}.
              </p>
            )}

            {aviso?.tipo === 'error' && <p className="oc-error" role="alert">{aviso.texto}</p>}
          </div>

          <div className="oc-enlaces">
            <Link to={`/centro/${activo.id}`}>Ver perfil público</Link>
            <Link to={`/?centro=${activo.id}`}>Ver en el mapa</Link>
          </div>
        </>
      )}
    </div>
  );
}

export default ControlOcupacion;
