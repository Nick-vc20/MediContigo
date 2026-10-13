import { useState } from "react";
import Login from "./components/Login";
import Administrador from "./components/Administrador";
import Especialidades from "./components/Especialidades";
import "./App.css";

export default function App() {
  const [logueado, setLogueado] = useState(localStorage.getItem("token") !== null);

  if (!logueado) {
    return <Login alIniciarSesion={() => setLogueado(true)} />;
  }

  return (
    <>
      <Administrador alCerrarSesion={() => setLogueado(false)} />
      <Especialidades centroId={1} />
    </>
  );
}