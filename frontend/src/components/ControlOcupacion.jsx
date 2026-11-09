import React, { useState } from 'react';

export const ControlOcupacion = ({ centroId, ocupacionInicial, tokenAdmin }) => {
  const [nivel, setNivel] = useState(ocupacionInicial || "BAJO");
  const [guardando, setGuardando] = useState(false);

  const manejarCambio = async (nuevoNivel) => {
    setNivel(nuevoNivel);
    setGuardando(true);

    try {
      const res = await fetch(`http://localhost:8081/api/admin/centros/${centroId}/ocupacion?nivel=${nuevoNivel}`, {
        method: 'PATCH',
        headers: {
          'Authorization': `Bearer ${tokenAdmin}` // Quítalo si aún no implementas seguridad JWT
        }
      });

      if (!res.ok) throw new Error("Error al actualizar");
      console.log("Ocupación actualizada en BD a:", nuevoNivel);
    } catch (error) {
      console.error("Error de sincronización:", error);
      alert("Hubo un problema al actualizar el nivel.");
    } finally {
      setGuardando(false);
    }
  };

  return (
    <div style={{ padding: '15px', border: '1px solid #ccc', borderRadius: '8px', maxWidth: '300px' }}>
      <h3>Estado del Centro</h3>
      <select 
        value={nivel} 
        onChange={(e) => manejarCambio(e.target.value)}
        disabled={guardando}
        style={{ padding: '8px', width: '100%', fontSize: '16px', marginTop: '10px' }}
      >
        <option value="BAJO">🟢 Nivel Bajo (Sin espera)</option>
        <option value="MEDIO">🟡 Nivel Medio (Espera regular)</option>
        <option value="ALTO">🔴 Nivel Alto (Colapsado)</option>
      </select>
      {guardando && <p style={{ fontSize: '12px', color: 'gray' }}>Sincronizando...</p>}
    </div>
  );
};