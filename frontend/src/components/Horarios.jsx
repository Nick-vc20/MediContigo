import { useCallback, useEffect, useState } from 'react';
import * as api from '../api/especialidadesApi';

const DIAS = ['', 'Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado', 'Domingo'];
const FORM_VACIO = { diaSemana: 1, horaInicio: '', horaFin: '' };

export default function Horarios({ centroId, especialidad }) {
  const [lista, setLista] = useState([]);
  const [form, setForm] = useState(FORM_VACIO);
  const [error, setError] = useState('');

  const cargar = useCallback(async () => {
    try {
      setError('');
      setLista(await api.listarHorarios(centroId, especialidad.id));
    } catch (e) {
      setError(e.message);
    }
  }, [centroId, especialidad.id]);

  useEffect(() => { cargar(); }, [cargar]);

  const agregar = async (ev) => {
    ev.preventDefault();
    try {
      setError('');
      await api.agregarHorario(centroId, especialidad.id, {
        diaSemana: Number(form.diaSemana),
        horaInicio: form.horaInicio,
        horaFin: form.horaFin,
      });
      setForm(FORM_VACIO);
      await cargar();
    } catch (e) {
      setError(e.message);
    }
  };

  const eliminar = async (h) => {
    try {
      setError('');
      await api.eliminarHorario(centroId, especialidad.id, h.id);
      await cargar();
    } catch (e) {
      setError(e.message);
    }
  };

  return (
    <section>
      <h2>Horarios de {especialidad.nombre}</h2>
      {error && <p className="error">{error}</p>}

      <form onSubmit={agregar} className="fila">
        <select value={form.diaSemana}
                onChange={(ev) => setForm({ ...form, diaSemana: ev.target.value })}>
          {DIAS.slice(1).map((d, i) => <option key={d} value={i + 1}>{d}</option>)}
        </select>
        <input type="time" required value={form.horaInicio}
               onChange={(ev) => setForm({ ...form, horaInicio: ev.target.value })} />
        <span>a</span>
        <input type="time" required value={form.horaFin}
               onChange={(ev) => setForm({ ...form, horaFin: ev.target.value })} />
        <button type="submit">Agregar bloque</button>
      </form>

      <ul>
        {lista.map((h) => (
          <li key={h.id}>
            {DIAS[h.diaSemana]}: {h.horaInicio.slice(0, 5)} - {h.horaFin.slice(0, 5)}{' '}
            <button onClick={() => eliminar(h)}>Eliminar</button>
          </li>
        ))}
        {lista.length === 0 && <li>Sin horarios registrados.</li>}
      </ul>
    </section>
  );
}