import { useState } from "react";
import { Link } from "react-router-dom";
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
        <div className="login-marca">
          <span className="login-logo" aria-hidden="true"></span>
          <span className="login-marca-nombre">MediContigo</span>
        </div>

        <h1>Panel de administración</h1>
        <p>Entra con tu cuenta para gestionar el sistema.</p>

        <ul className="login-beneficios">
          <li>Actualiza la ocupación de tu centro al instante</li>
          <li>Gestiona especialidades, horarios y médicos</li>
          <li>Mantén información confiable para los pacientes</li>
        </ul>
      </div>

      <div className="login-formulario">
        <div className="login-caja">
          <h2>Iniciar sesión</h2>
          <p className="subtitulo">Escribe tus datos de administrador</p>

          <form onSubmit={enviar}>
            <div className="campo">
              <label htmlFor="login-correo">Correo</label>
              <input
                id="login-correo"
                type="email"
                placeholder="admin@correo.com"
                autoComplete="username"
                value={correo}
                onChange={(e) => setCorreo(e.target.value)}
                required
              />
            </div>

            <div className="campo">
              <label htmlFor="login-password">Contraseña</label>
              <div className="campo-password">
                <input
                  id="login-password"
                  type={verPassword ? "text" : "password"}
                  placeholder="Tu contraseña"
                  autoComplete="current-password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                />
                <button
                  type="button"
                  className="ver-password"
                  aria-pressed={verPassword}
                  onClick={() => setVerPassword(!verPassword)}
                >
                  {verPassword ? "Ocultar" : "Mostrar"}
                </button>
              </div>
            </div>

            <button type="submit" className="boton" disabled={cargando}>
              {cargando ? (
                <>
                  <span className="boton-spinner" aria-hidden="true"></span>
                  Entrando...
                </>
              ) : (
                "Entrar"
              )}
            </button>
          </form>

          {error && (
            <p className="mensaje-error" role="alert">
              {error}
            </p>
          )}

          <Link to="/" className="login-volver">
            ← Volver al mapa de centros
          </Link>
        </div>
      </div>
    </div>
  );
}

export default Login;
