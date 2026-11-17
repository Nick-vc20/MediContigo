import { useState, useEffect } from "react";
import "../login.css";
import React from 'react';
import { ControlOcupacion } from './ControlOcupacion';

function Panel({ alCerrarSesion }) {
  const [mensaje, setMensaje] = useState("Cargando...");
  
  // Se extrae fuera del useEffect para poder pasárselo también al control de ocupación
  const token = localStorage.getItem("token"); 

  useEffect(() => {
    async function cargarPanel() {
      try {
        const respuesta = await fetch(`${import.meta.env.VITE_API_URL ?? "http://localhost:8081"}/api/panel`, {
          headers: { Authorization: "Bearer " + token },
        });

        // si el token no sirve, el backend responde 401
        if (respuesta.status === 401) {
          localStorage.removeItem("token");
          alCerrarSesion();
          return;
        }

        const texto = await respuesta.text();
        setMensaje(texto);
      } catch (err) {
        setMensaje("No se pudo conectar con el servidor");
      }
    }

    cargarPanel();
  }, [token, alCerrarSesion]);

  function cerrarSesion() {
    localStorage.removeItem("token");
    alCerrarSesion();
  }

  return (
    <div>
      <div className="panel-barra">
        <h2>Panel de administración</h2>
        <button className="boton-salir" onClick={cerrarSesion}>
          Cerrar sesión
        </button>
      </div>

      <div className="panel-contenido">
        <h1>Bienvenido, administrador</h1>
        
        <div className="panel-tarjeta">
          <h3>Respuesta del servidor</h3>
          <p>{mensaje}</p>
        </div>

        {/* HU-10 (10.1 y 10.3): control de ocupación conectado al registro de nivel de atención */}
        <div className="panel-tarjeta panel-ocupacion">
          <ControlOcupacion tokenAdmin={token} alExpirarSesion={alCerrarSesion} />
        </div>

      </div>
    </div>
  );
}

export default Panel;