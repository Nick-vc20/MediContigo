import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { useState } from "react";
import Login from "./components/Login";
import Administrador from "./components/Administrador";
import Especialidades from "./components/Especialidades";
import PerfilCentroPublico from "./components/PerfilCentroPublico";
import "./App.css";

export default function App() {
  const [logueado, setLogueado] = useState(localStorage.getItem("token") !== null);

  return (
    <BrowserRouter>
      <Routes>
        
        {/* ==========================================
            1. ZONA PÚBLICA (Pacientes) 
            Usa :id dinámico para leer el centro desde la URL
        ========================================== */}
        <Route path="/centro/:id" element={<PerfilCentroPublico />} />
        
        {/* ==========================================
            2. ZONA PRIVADA (Administradores) 
        ========================================== */}
        <Route 
          path="/admin" 
          element={
            logueado ? (
              <>
                <Administrador alCerrarSesion={() => setLogueado(false)} />
                <Especialidades centroId={1} />
              </>
            ) : (
              <Login alIniciarSesion={() => setLogueado(true)} />
            )
          } 
        />

        {/* ==========================================
            3. REDIRECCIÓN POR DEFECTO
        ========================================== */}
        <Route path="*" element={<Navigate to="/admin" />} />

      </Routes>
    </BrowserRouter>
  );
}