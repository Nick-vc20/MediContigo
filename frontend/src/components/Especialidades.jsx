import { useCallback, useEffect, useState } from 'react';
import * as api from '../api/especialidadesApi';
import Horarios from './Horarios';

const FORM_VACIO = { nombre: '', descripcion: '' };

export default function Especialidades({ centroId = 1 }) {
  const [lista, setLista] = useState([]);
  const [verInactivas, setVerInactivas] = useState(false);
  const [form, setForm] = useState(FORM_VACIO);
  const [editandoId, setEditandoId] = useState(null);
  const [seleccionada, setSeleccionada] = useState(null);
  const [error, setError] = useState('');

  const cargar = useCallback(async () => {
    try {
      setError('');
      setLista(await api.listarEspecialidades(centroId, verInactivas));
    } catch (e) {
      setError(e.message);
    }
  }, [centroId, verInactivas]);

  useEffect(() => { cargar(); }, [cargar]);

  const cancelar = () => { setForm(FORM_VACIO); setEditandoId(null); };

  const editar = (e) => {
    setForm({ nombre: e.nombre, descripcion: e.descripcion ?? '' });
    setEditandoId(e.id);
  };

  const guardar = async (ev) => {
    ev.preventDefault();
    try {
      setError('');
      if (editandoId) await api.actualizarEspecialidad(centroId, editandoId, form);
      else await api.crearEspecialidad(centroId, form);
      cancelar();
      await cargar();
    } catch (e) {
      setError(e.message);
    }
  };

  const alternarEstado = async (e) => {
    if (e.estado && !window.confirm(`¿Dar de baja la especialidad "${e.nombre}"?`)) return;
    try {
      setError('');
      await api.cambiarEstado(centroId, e.id, !e.estado);
      setSeleccionada(null);
      await cargar();
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <main className="contenedor">
      <h1>Especialidades del centro</h1>

      <label>
        <input type="checkbox" checked={verInactivas}
               onChange={(ev) => setVerInactivas(ev.target.checked)} /> Ver inactivas
      </label>

      {error && <p className="error">{error}</p>}

      <form onSubmit={guardar} className="fila">
        <input placeholder="Nombre" required maxLength={100} value={form.nombre}
               onChange={(ev) => setForm({ ...form, nombre: ev.target.value })} />
        <input placeholder="Descripción" maxLength={255} value={form.descripcion}
               onChange={(ev) => setForm({ ...form, descripcion: ev.target.value })} />
        <button type="submit">{editandoId ? 'Guardar cambios' : 'Agregar'}</button>
        {editandoId && <button type="button" onClick={cancelar}>Cancelar</button>}
      </form>

      <div className="scroll">
        <table>
          <thead>
            <tr><th>Nombre</th><th>Descripción</th><th>Estado</th><th>Acciones</th></tr>
          </thead>
          <tbody>
            {lista.map((e) => (
              <tr key={e.id} className={e.estado ? '' : 'inactiva'}>
                <td>{e.nombre}</td>
                <td>{e.descripcion}</td>
                <td><span className={e.estado ? 'badge' : 'badge off'}>
                  {e.estado ? 'Activa' : 'Inactiva'}</span></td>
                <td>
                  <button onClick={() => editar(e)}>Editar</button>
                  <button onClick={() => setSeleccionada(e)}>Horarios</button>
                  <button onClick={() => alternarEstado(e)}>
                    {e.estado ? 'Dar de baja' : 'Reactivar'}</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {seleccionada && (
        <Horarios key={seleccionada.id} centroId={centroId} especialidad={seleccionada} />
      )}
    </main>
  );
}