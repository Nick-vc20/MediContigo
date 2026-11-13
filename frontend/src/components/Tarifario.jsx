import { useEffect, useState } from 'react';
import { listarTarifas } from '../api/tarifasApi';

export default function Tarifario({ centroId }) {
  const [tarifas, setTarifas] = useState([]);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    let vigente = true;

    setCargando(true);
    setError('');

    listarTarifas(centroId)
      .then((datos) => {
        if (vigente) {
          setTarifas(datos);
          setCargando(false);
        }
      })
      .catch(() => {
        if (vigente) {
          setError('No se pudieron cargar las tarifas.');
          setCargando(false);
        }
      });

    return () => {
      vigente = false;
    };
  }, [centroId]);

  if (cargando) {
    return <p className="perfil-vacio">Cargando tarifas...</p>;
  }

  if (error) {
    return <p className="perfil-vacio" role="alert">{error}</p>;
  }

  if (tarifas.length === 0) {
    return <p className="perfil-vacio">Este centro todavía no tiene tarifas registradas.</p>;
  }

  return (
    <section aria-label="Tarifario de especialidades">
      <h3 className="perfil-titulo">Tarifario de especialidades</h3>

      {tarifas.map((tarifa) => (
        <div className="perfil-dato" key={tarifa.id}>
          <span>{tarifa.especialidadNombre}</span>
          <strong>
            {tarifa.moneda === 'PEN' ? 'S/' : tarifa.moneda}{' '}
            {Number(tarifa.precioAproximado).toFixed(2)}
          </strong>
        </div>
      ))}

      <p className="perfil-aviso">
        Los precios son aproximados y pueden variar según el establecimiento.
      </p>
    </section>
  );
}
