import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom'; // Para leer el ID de la URL

const PerfilCentroPublico = () => {
  const { id } = useParams(); // Saca el "1" de /centro/1
  
  // Variables de estado
  const [perfil, setPerfil] = useState(null);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState(null);

  // Efecto que llama al backend cuando carga la página
  useEffect(() => {
    // Llamamos al endpoint de Spring Boot que construiste en la Tarea 6.2
    fetch(`http://localhost:8081/api/publico/centros/${id}`)
      .then((respuesta) => {
        if (!respuesta.ok) {
          throw new Error("No se pudo cargar la información del centro médico");
        }
        return respuesta.json(); // Convertimos la respuesta a JSON
      })
      .then((datos) => {
        setPerfil(datos); // Guardamos los datos reales del DTO
        setCargando(false);
      })
      .catch((err) => {
        setError(err.message);
        setCargando(false);
      });
  }, [id]);

  // Pantalla de carga mientras espera al backend
  if (cargando) {
    return <div style={{ textAlign: 'center', marginTop: '50px' }}>Cargando información del centro...</div>;
  }

  // Pantalla de error si el servidor Java está apagado o falla
  if (error) {
    return <div style={{ textAlign: 'center', marginTop: '50px', color: 'red' }}>Error: {error}</div>;
  }

  // Si todo salió bien, dibujamos la interfaz con los datos reales (perfil)
  return (
    <div style={{ minHeight: '100vh', backgroundColor: '#f9fafb', padding: '40px 20px', fontFamily: 'system-ui, -apple-system, sans-serif' }}>
      <div style={{ maxWidth: '800px', margin: '0 auto', backgroundColor: 'white', borderRadius: '12px', boxShadow: '0 4px 6px -1px rgba(0, 0, 0, 0.1)', overflow: 'hidden' }}>
        
        {/* Encabezado */}
        <div style={{ backgroundColor: '#2563eb', padding: '30px', color: 'white' }}>
          <h1 style={{ margin: '0 0 10px 0', fontSize: '28px', fontWeight: 'bold' }}>{perfil.nombreCentro}</h1>
          <p style={{ margin: 0, color: '#bfdbfe', display: 'flex', alignItems: 'center', gap: '8px', fontSize: '16px' }}>
            📍 {perfil.direccionCentro}
          </p>
        </div>

        <div style={{ padding: '30px' }}>
          {/* Métricas */}
          <div style={{ display: 'flex', flexWrap: 'wrap', gap: '20px', marginBottom: '30px' }}>
            <div style={{ flex: '1 1 250px', backgroundColor: '#f9fafb', padding: '20px', borderRadius: '8px', border: '1px solid #f3f4f6' }}>
              <h3 style={{ fontSize: '14px', color: '#6b7280', margin: '0 0 5px 0', fontWeight: '500' }}>Costo de Consulta Promedio</h3>
              <p style={{ fontSize: '24px', fontWeight: 'bold', color: '#111827', margin: 0 }}>S/ {perfil.costoConsulta.toFixed(2)}</p>
            </div>
            <div style={{ flex: '1 1 250px', backgroundColor: '#f9fafb', padding: '20px', borderRadius: '8px', border: '1px solid #f3f4f6' }}>
              <h3 style={{ fontSize: '14px', color: '#6b7280', margin: '0 0 5px 0', fontWeight: '500' }}>Tiempo de Espera Estimado</h3>
              <p style={{ fontSize: '24px', fontWeight: 'bold', color: '#111827', margin: 0 }}>⏱️ {perfil.tiempoEspera}</p>
            </div>
          </div>

          {/* Especialidades */}
          <div style={{ marginBottom: '30px' }}>
            <h2 style={{ fontSize: '20px', color: '#111827', borderBottom: '2px solid #f3f4f6', paddingBottom: '10px', marginBottom: '20px', marginTop: 0 }}>
              Especialidades Disponibles
            </h2>
            <div style={{ display: 'flex', flexWrap: 'wrap', gap: '10px' }}>
              {perfil.especialidades.length > 0 ? (
                perfil.especialidades.map((especialidad, index) => (
                  <span key={index} style={{ backgroundColor: '#eff6ff', color: '#1d4ed8', padding: '8px 16px', borderRadius: '999px', fontSize: '14px', fontWeight: '500' }}>
                    {especialidad}
                  </span>
                ))
              ) : (
                <p style={{ color: '#6b7280' }}>No hay especialidades registradas.</p>
              )}
            </div>
          </div>

          {/* Horarios */}
          <div>
            <h2 style={{ fontSize: '20px', color: '#111827', borderBottom: '2px solid #f3f4f6', paddingBottom: '10px', marginBottom: '20px', marginTop: 0 }}>
              Horarios de Atención
            </h2>
            <ul style={{ listStyle: 'none', padding: 0, margin: 0 }}>
              {perfil.resumenHorarios.length > 0 ? (
                perfil.resumenHorarios.map((horario, index) => (
                  <li key={index} style={{ display: 'flex', alignItems: 'center', gap: '10px', color: '#374151', marginBottom: '12px', fontSize: '16px' }}>
                    <span style={{ color: '#22c55e', fontWeight: 'bold' }}>✓</span>
                    {horario}
                  </li>
                ))
              ) : (
                <p style={{ color: '#6b7280' }}>No hay horarios registrados.</p>
              )}
            </ul>
          </div>
        </div>

      </div>
    </div>
  );
};

export default PerfilCentroPublico;