import { Link, useParams } from 'react-router-dom';
import PerfilCentro from './PerfilCentro';

// HU-06: pagina publica /centro/:id (se puede compartir el enlace)
export default function PerfilCentroPublico() {
  const { id } = useParams();

  return (
    <div className="contenedor">
      <h1>Perfil del centro médico</h1>
      <Link to="/">← Volver al mapa de centros</Link>
      <div className="perfil-pagina">
        <PerfilCentro id={id} />
      </div>
    </div>
  );
}
