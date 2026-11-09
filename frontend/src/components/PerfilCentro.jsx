import { useEffect, useRef, useState } from 'react';
import { Link } from 'react-router-dom';
import { obtenerPerfilCentro } from '../api/centrosApi';
import { atiendeHoy, formatoCosto, ocupacionDe, resumirHorarios, urlComoLlegar } from '../utils/formato';
import '../perfil.css';

// Perfil completo de un centro. Se usa en dos lugares:
//  - dentro del panel del mapa (compacto, con boton "Volver")
//  - en la pagina publica /centro/:id
export default function PerfilCentro({ id, compacto = false, alVolver }) {
  // resultado = { id, perfil, error } del ultimo pedido terminado
  const [resultado, setResultado] = useState(null);
  const [intento, setIntento] = useState(0);
  const contenedor = useRef(null);

  useEffect(() => {
    let vigente = true; // evita mostrar una respuesta vieja si el usuario cambia de centro

    obtenerPerfilCentro(id)
      .then((datos) => {
        if (vigente) setResultado({ id: String(id), perfil: datos, error: '' });
      })
      .catch((e) => {
        if (!vigente) return;
        const mensaje = e.status === 404
          ? 'No encontramos este centro médico.'
          : 'No se pudo cargar la información del centro.';
        setResultado({ id: String(id), perfil: null, error: mensaje });
      });

    return () => { vigente = false; };
  }, [id, intento]);

  // Mientras no llegue la respuesta de este centro se muestra "cargando"
  const cargando = resultado === null || resultado.id !== String(id);
  const perfil = cargando ? null : resultado.perfil;
  const error = cargando ? '' : resultado.error;

  const reintentar = () => {
    setResultado(null);
    setIntento(intento + 1);
  };

  // Al abrir el perfil se lleva el foco al contenido (lectores de pantalla y teclado)
  useEffect(() => {
    if (perfil && compacto) contenedor.current?.focus();
  }, [perfil, compacto]);

  const botonVolver = alVolver && (
    <button type="button" className="perfil-volver" onClick={alVolver}>← Volver a la lista</button>
  );

  if (cargando) {
    return (
      <div className="perfil" aria-busy="true">
        {botonVolver}
        <div className="perfil-esqueleto" role="status">Cargando información del centro...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="perfil">
        {botonVolver}
        <p className="error" role="alert">{error}</p>
        <button type="button" className="perfil-reintentar" onClick={reintentar}>Reintentar</button>
      </div>
    );
  }

  const ocupacion = ocupacionDe(perfil.nivelOcupacion);

  return (
    <article className={`perfil ${compacto ? 'perfil-compacto' : ''}`} ref={contenedor} tabIndex={-1}>
      {botonVolver}

      <header className="perfil-cabecera">
        <span className="perfil-tipo">{perfil.tipo}</span>
        <h2 className="perfil-nombre">{perfil.nombre}</h2>
        <p className="perfil-direccion">{perfil.direccion}</p>
      </header>

      <div className="perfil-acciones">
        <a className="perfil-boton principal" href={urlComoLlegar(perfil.latitud, perfil.longitud)}
           target="_blank" rel="noreferrer">Cómo llegar</a>
        {perfil.telefono && (
          <a className="perfil-boton" href={`tel:${perfil.telefono.replace(/[^\d+]/g, '')}`}>Llamar</a>
        )}
        {compacto && (
          <Link className="perfil-boton" to={`/centro/${perfil.id}`}>Página completa</Link>
        )}
      </div>

      <div className="perfil-datos">
        <div className="perfil-dato">
          <span>Costo de consulta</span>
          <strong>{formatoCosto(perfil.costoConsulta)}</strong>
        </div>
        <div className="perfil-dato">
          <span>Tiempo de espera</span>
          <strong>{perfil.tiempoEspera || 'No especificado'}</strong>
        </div>
        <div className="perfil-dato">
          <span>Ocupación actual</span>
          <strong className={`ocupacion ocupacion-${ocupacion.clase}`}>{ocupacion.etiqueta}</strong>
        </div>
        <div className="perfil-dato">
          <span>Teléfono</span>
          <strong>{perfil.telefono || 'No especificado'}</strong>
        </div>
      </div>

      <h3 className="perfil-titulo">Especialidades, horarios y personal</h3>

      {perfil.especialidades.length === 0 && (
        <p className="perfil-vacio">Este centro todavía no tiene especialidades registradas.</p>
      )}

      {perfil.especialidades.map((esp) => (
        <details className="perfil-especialidad" key={esp.id} open={!compacto}>
          <summary>
            <span className="perfil-especialidad-nombre">{esp.nombre}</span>
            {atiendeHoy(esp.horarios) && <span className="perfil-hoy">Atiende hoy</span>}
          </summary>

          <div className="perfil-especialidad-cuerpo">
            {esp.descripcion && <p className="perfil-descripcion">{esp.descripcion}</p>}

            <h4>Horarios de atención</h4>
            {esp.horarios.length === 0 ? (
              <p className="perfil-vacio">Horarios no registrados.</p>
            ) : (
              resumirHorarios(esp.horarios).map((h) => (
                <div className={`perfil-horario ${h.incluyeHoy ? 'es-hoy' : ''}`} key={h.dias + h.horas}>
                  <span>{h.dias}</span>
                  <strong>{h.horas}</strong>
                </div>
              ))
            )}

            <h4>Personal médico</h4>
            {esp.medicos.length === 0 ? (
              <p className="perfil-vacio">Personal no registrado.</p>
            ) : (
              esp.medicos.map((m) => (
                <div className="perfil-medico" key={m.cmp}>
                  <span>{m.nombreCompleto}</span>
                  <small>{m.cmp}</small>
                </div>
              ))
            )}
          </div>
        </details>
      ))}

      <p className="perfil-aviso">
        Información referencial. Confirma horarios y disponibilidad con el establecimiento antes de acudir.
      </p>
    </article>
  );
}
