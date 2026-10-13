import { useState } from "react";
import "../login.css";
function Login({ alIniciarSesion }) {
  const [correo, setCorreo] = useState("");
  const [password, setPassword] = useState("");
  const [verPassword, setVerPassword] = useState(false);
  const [error, setError] = useState("");
  const [cargando, setCargando] = useState(false);

  async function enviar(e) {
    e.preventDefault();
    setError("");
    setCargando(true);

    try {
      const respuesta = await fetch("http://localhost:8081/api/admin/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ correo: correo, password: password }),
      });

      if (!respuesta.ok) {
        setError("Correo o contraseña incorrectos");
        setCargando(false);
        return;
      }

      const datos = await respuesta.json();
      // guardamos el token para usarlo despues
      localStorage.setItem("token", datos.token);
      alIniciarSesion();
    } catch (err) {
      setError("No se pudo conectar con el servidor");
    }

    setCargando(false);
  }

  return (
    <div className="login-pagina">
      <div className="login-lado">
        <h1>Panel de administración</h1>
        <p>Entra con tu cuenta para gestionar el sistema.</p>
      </div>

      <div className="login-formulario">
        <div className="login-caja">
          <h2>Iniciar sesión</h2>
          <p className="subtitulo">Escribe tus datos de administrador</p>

          <form onSubmit={enviar}>
            <div className="campo">
              <label>Correo</label>
              <input
                type="email"
                placeholder="admin@correo.com"
                value={correo}
                onChange={(e) => setCorreo(e.target.value)}
                required
              />
            </div>

            <div className="campo">
              <label>Contraseña</label>
              <input
                type={verPassword ? "text" : "password"}
                placeholder="Tu contraseña"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
              <button
                type="button"
                className="ver-password"
                onClick={() => setVerPassword(!verPassword)}
              >
                {verPassword ? "Ocultar contraseña" : "Mostrar contraseña"}
              </button>
            </div>

            <button type="submit" className="boton" disabled={cargando}>
              {cargando ? "Entrando..." : "Entrar"}
            </button>
          </form>

          {error && <p className="mensaje-error">{error}</p>}
        </div>
      </div>
    </div>
  );
}

export default Login;
