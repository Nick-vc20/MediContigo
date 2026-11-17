import { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { CircleMarker, MapContainer, TileLayer, useMap, useMapEvents } from 'react-leaflet';
import 'leaflet/dist/leaflet.css';

import { listarTodosLosCentros, registrarCentro } from '../api/centrosAdminApi';
import { formatoCosto } from '../utils/formato';
import Especialidades from './Especialidades';

import '../gestionCentros.css';

// Mismos tipos que reconoce el mapa para pintar los marcadores (ver estiloTipo)
const TIPOS = ['Hospital', 'Clínica', 'Posta', 'Centro Médico'];

const LIMA = [-12.046374, -77.042793];

const FORM_VACIO = {
  nombre: '',
  tipo: 'Clínica',
  direccion: '',
  telefono: '',
  costoConsulta: '',
  tiempoEspera: '',
};

function validar(form, posicion) {
  if (!form.nombre.trim()) return 'Escribe el nombre del centro.';
  if (!form.direccion.trim()) return 'Escribe la dirección del centro.';
  if (!posicion) return 'Marca en el mapa dónde queda el centro.';

  if (form.telefono.trim() && !/^[\d\s()+-]{6,20}$/.test(form.telefono.trim())) {
    return 'El teléfono solo puede llevar números, espacios, paréntesis, + y guiones.';
  }

  if (form.costoConsulta !== '' && !(Number(form.costoConsulta) >= 0)) {
    return 'El costo de consulta no puede ser negativo.';
  }

  return '';
}

// Un clic en el mapa fija la ubicación; si se busca una dirección, el mapa se mueve hasta ella
function SelectorUbicacion({ posicion, alElegir, vista }) {
  const mapa = useMap();

  useMapEvents({
    click(e) {
      alElegir([e.latlng.lat, e.latlng.lng]);
    },
  });

  useEffect(() => {
    if (vista) mapa.flyTo(vista, 16, { duration: 0.6 });
  }, [vista, mapa]);

  if (!posicion) return null;

  return (
    <CircleMarker
      center={posicion}
      radius={10}
      pathOptions={{ color: 'white', fillColor: '#dc2626', fillOpacity: 1, weight: 3 }}
    />
  );
}

export default function GestionCentros() {
  const [centros, setCentros] = useState([]);
  const [activoId, setActivoId] = useState(null);
  const [errorLista, setErrorLista] = useState('');

  const [form, setForm] = useState(FORM_VACIO);
  const [posicion, setPosicion] = useState(null);
  const [vista, setVista] = useState(null);
  const [guardando, setGuardando] = useState(false);
  const [buscando, setBuscando] = useState(false);
  const [error, setError] = useState('');
  const [avisoMapa, setAvisoMapa] = useState('');
  const [registrado, setRegistrado] = useState(null);

  const cargarCentros = useCallback(async (preferido) => {
    try {
      setErrorLista('');
      const lista = await listarTodosLosCentros();
      lista.sort((a, b) => a.id - b.id);
      setCentros(lista);
      setActivoId((anterior) => {
        if (preferido) return preferido;
        if (lista.some((c) => c.id === anterior)) return anterior;
        return lista.length > 0 ? lista[0].id : null;
      });
    } catch (e) {
      setErrorLista(e.message);
    }
  }, []);

  useEffect(() => { cargarCentros(); }, [cargarCentros]);

  const cambiar = (campo) => (ev) => setForm({ ...form, [campo]: ev.target.value });

  // Busca la dirección escrita en el formulario y mueve el mapa hasta ahí
  async function ubicarDireccion() {
    const consulta = form.direccion.trim();
    if (!consulta || buscando) return;

    setBuscando(true);
    setAvisoMapa('');

    try {
      const r = await fetch(
        `https://nominatim.openstreetmap.org/search?q=${encodeURIComponent(consulta)}&format=jsonv2&limit=1&countrycodes=pe`
      );
      if (!r.ok) throw new Error('fallo la busqueda');

      const datos = await r.json();
      if (datos.length === 0) {
        setAvisoMapa('No encontramos esa dirección. Marca el punto directamente en el mapa.');
        return;
      }

      const punto = [Number(datos[0].lat), Number(datos[0].lon)];
      setPosicion(punto);
      setVista(punto);
    } catch {
      setAvisoMapa('No se pudo buscar la dirección. Marca el punto directamente en el mapa.');
    } finally {
      setBuscando(false);
    }
  }

  async function guardar(ev) {
    ev.preventDefault();
    setRegistrado(null);

    const problema = validar(form, posicion);
    if (problema) {
      setError(problema);
      return;
    }

    setError('');
    setGuardando(true);

    try {
      const nuevo = await registrarCentro(
        {
          nombre: form.nombre.trim(),
          tipo: form.tipo,
          direccion: form.direccion.trim(),
          telefono: form.telefono.trim() || null,
          latitud: Number(posicion[0].toFixed(6)),
          longitud: Number(posicion[1].toFixed(6)),
          costoConsulta: form.costoConsulta === '' ? 0 : Number(form.costoConsulta),
          // el mapa muestra este texto tal cual, no puede ir vacío
          tiempoEspera: form.tiempoEspera.trim() || 'No especificado',
        },
        localStorage.getItem('token')
      );

      setRegistrado(nuevo);
      setForm(FORM_VACIO);
      setPosicion(null);
      setAvisoMapa('');
      await cargarCentros(nuevo.id);
    } catch (e) {
      setError(e.message);
    } finally {
      setGuardando(false);
    }
  }

  const activo = centros.find((c) => c.id === activoId) || null;

  return (
    <section className="contenedor gc">
      <h2>Registrar un centro médico</h2>

      <p className="gc-ayuda">
        Lo que guardes aquí aparece en el mapa de pacientes con sus datos. Después de registrarlo,
        agrega sus especialidades y horarios en la sección de abajo para completar el perfil.
      </p>

      {registrado && (
        <div className="gc-exito" role="status">
          <strong>{registrado.nombre}</strong> quedó registrado y ya se puede ver en el mapa.{' '}
          <Link to={`/?centro=${registrado.id}`}>Verlo en el mapa</Link>
        </div>
      )}

      {error && <p className="error" role="alert">{error}</p>}

      <form className="gc-formulario" onSubmit={guardar}>
        <div className="gc-campos">
          <label>
            Nombre
            <input
              required
              maxLength={150}
              placeholder="Ej: Posta Médica Barranco"
              value={form.nombre}
              onChange={cambiar('nombre')}
            />
          </label>

          <label>
            Tipo de establecimiento
            <select value={form.tipo} onChange={cambiar('tipo')}>
              {TIPOS.map((t) => <option key={t} value={t}>{t}</option>)}
            </select>
          </label>

          <label className="gc-ancho">
            Dirección
            <input
              required
              maxLength={255}
              placeholder="Ej: Av. Grau 340, Barranco"
              value={form.direccion}
              onChange={cambiar('direccion')}
            />
          </label>

          <label>
            Teléfono
            <input
              maxLength={30}
              placeholder="(01) 555-0199"
              value={form.telefono}
              onChange={cambiar('telefono')}
            />
          </label>

          <label>
            Costo de consulta (S/)
            <input
              type="number"
              min="0"
              step="0.01"
              placeholder="Sin especificar"
              value={form.costoConsulta}
              onChange={cambiar('costoConsulta')}
            />
          </label>

          <label>
            Tiempo de espera
            <input
              maxLength={50}
              placeholder="Ej: 30 minutos"
              value={form.tiempoEspera}
              onChange={cambiar('tiempoEspera')}
            />
          </label>
        </div>

        <div className="gc-ubicacion">
          <div className="gc-ubicacion-barra">
            <span>
              {posicion
                ? `Ubicación marcada: ${posicion[0].toFixed(5)}, ${posicion[1].toFixed(5)}`
                : 'Haz clic en el mapa para marcar la ubicación'}
            </span>
            <button
              type="button"
              onClick={ubicarDireccion}
              disabled={buscando || !form.direccion.trim()}
            >
              {buscando ? 'Buscando...' : 'Ubicar la dirección escrita'}
            </button>
          </div>

          {avisoMapa && <p className="gc-aviso">{avisoMapa}</p>}

          <div className="gc-mapa">
            <MapContainer center={LIMA} zoom={12} style={{ height: '100%', width: '100%' }}>
              <TileLayer
                attribution="&copy; OpenStreetMap contributors"
                url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
              />
              <SelectorUbicacion posicion={posicion} alElegir={setPosicion} vista={vista} />
            </MapContainer>
          </div>
        </div>

        <button type="submit" className="gc-guardar" disabled={guardando}>
          {guardando ? 'Guardando...' : 'Registrar centro'}
        </button>
      </form>

      <h2 className="gc-separador">Centro que estás administrando</h2>

      {errorLista && <p className="error" role="alert">{errorLista}</p>}

      {centros.length === 0 && !errorLista && (
        <p className="gc-ayuda">Todavía no hay centros registrados.</p>
      )}

      {activo && (
        <>
          <div className="gc-selector">
            <select
              aria-label="Centro a administrar"
              value={activoId ?? ''}
              onChange={(ev) => setActivoId(Number(ev.target.value))}
            >
              {centros.map((c) => (
                <option key={c.id} value={c.id}>{c.nombre} ({c.tipo})</option>
              ))}
            </select>

            <Link className="gc-enlace" to={`/?centro=${activo.id}`}>Ver en el mapa</Link>
            <Link className="gc-enlace" to={`/centro/${activo.id}`}>Ver perfil público</Link>
          </div>

          <p className="gc-resumen">
            {activo.direccion} · {activo.telefono || 'Sin teléfono'} ·{' '}
            {formatoCosto(activo.costoConsulta)} · espera {activo.tiempoEspera}
          </p>

          {/* Se reutiliza tal cual: especialidades y horarios del centro elegido (HU-03) */}
          <Especialidades key={activo.id} centroId={activo.id} />
        </>
      )}
    </section>
  );
}
