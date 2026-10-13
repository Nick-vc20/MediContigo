import { useState, useEffect } from "react";
import "../login.css";

function Panel({ alCerrarSesion }) {
  const [mensaje, setMensaje] = useState("Cargando...");

  useEffect(() => {
    async function cargarPanel() {
      const token = localStorage.getItem("token");

      try {
        const respuesta = await fetch("http://localhost:8081/api/panel", {
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
  }, []);

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
      </div>
    </div>
  );
}

export default Panel;
