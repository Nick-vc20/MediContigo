
import { useEffect, useState } from "react";
import { MapContainer, TileLayer, Marker, Popup, CircleMarker } from "react-leaflet";
import L from "leaflet";
import "leaflet/dist/leaflet.css";

import markerIcon from "leaflet/dist/images/marker-icon.png";
import markerShadow from "leaflet/dist/images/marker-shadow.png";


function crearIcono(tipo) {

    const nombre = (tipo || "").toLowerCase();

    let color = "#6b7280";
    let letra = "+";

    if (nombre.includes("hospital")) {
        color = "#dc2626";
        letra = "H";
    } else if (nombre.includes("clínica") || nombre.includes("clinica")) {
        color = "#2563eb";
        letra = "C";
    } else if (nombre.includes("posta")) {
        color = "#16a34a";
        letra = "P";
    }

    return L.divIcon({
        className: "",
        html: `<div style="
      background: ${color};
      color: white;
      width: 32px;
      height: 32px;
      border-radius: 50%;
      border: 3px solid white;
      display: flex;
      justify-content: center;
      align-items: center;
      font-weight: bold;
      box-shadow: 0 2px 6px #888;
    ">${letra}</div>`,
        iconSize: [38, 38],
        iconAnchor: [19, 19]
    });
}


// Ubicacion inicial de prueba
const ubicacion = [-12.05605, -77.01573];

function MapaCentros() {

    const [centros, setCentros] = useState([]);
    const [error, setError] = useState("");
    const [cargando, setCargando] = useState(false);
    const [ubicacionUsuario, setUbicacionUsuario] = useState(null);
    const [mensajeGPS, setMensajeGPS] = useState("");
    const [radio, setRadio] = useState(5);
    const [direccionManual, setDireccionManual] = useState("");
    const [buscandoDireccion, setBuscandoDireccion] = useState(false);

    useEffect(() => {
        if (!navigator.geolocation) {
            setMensajeGPS("Tu navegador no permite obtener la ubicacion");
            return;
        }

        navigator.geolocation.getCurrentPosition(
            (posicion) => {
                const lat = posicion.coords.latitude;
                const lng = posicion.coords.longitude;

                setUbicacionUsuario([lat, lng]);
            },
            () => {
                setMensajeGPS("No se pudo obtener tu ubicacion. Puedes buscar manualmente.");
            },
            {
                enableHighAccuracy: true,
                timeout: 10000
            }
        );
    }, []);


    useEffect(() => {

        if (!ubicacionUsuario) {
            return;
        }

        const lat = ubicacionUsuario[0];
        const lng = ubicacionUsuario[1];

        setCargando(true);
        setError("");

        fetch(`http://localhost:8081/api/centros?lat=${lat}&lng=${lng}&radio=${radio}`)
            .then(respuesta => {
                if (!respuesta.ok) {
                    throw new Error("Error al consultar los centros");
                }
                return respuesta.json();
            })
            .then(datos => {
                setCentros(datos);
            })
            .catch(() => {
                setError("No se pudieron cargar los centros medicos");
            })
            .finally(() => {
                setCargando(false);
            });

    }, [ubicacionUsuario, radio]);


    async function buscarDireccion(e) {
        e.preventDefault();

        if (!direccionManual.trim() || buscandoDireccion) {
            return;
        }

        setBuscandoDireccion(true);
        setError("");

        try {
            const direccion = encodeURIComponent(direccionManual);

            const respuesta = await fetch(
                `https://nominatim.openstreetmap.org/search?q=${direccion}&format=jsonv2&limit=1`
            );

            if (!respuesta.ok) {
                throw new Error("Error al buscar la direccion");
            }

            const datos = await respuesta.json();

            if (datos.length === 0) {
                setError("No se encontro la direccion ingresada");
                return;
            }

            const lat = Number(datos[0].lat);
            const lng = Number(datos[0].lon);

            setUbicacionUsuario([lat, lng]);
            setRadio(5);

        } catch (error) {
            setError("No se pudo buscar la direccion");
        } finally {
            setBuscandoDireccion(false);
        }
    }


    return (
        <div>
            <h2>Centros medicos cercanos</h2>
            <p>Radio de búsqueda: {radio} km</p>

            {ubicacionUsuario && (
                <p>
                    Tu ubicacion: {ubicacionUsuario[0].toFixed(6)},
                    {ubicacionUsuario[1].toFixed(6)}
                </p>
            )}

            {mensajeGPS && <p>{mensajeGPS}</p>}

            {mensajeGPS && (
                <form onSubmit={buscarDireccion}>
                    <input
                        type="text"
                        placeholder="Ejemplo: Cercado de Lima, Perú"
                        value={direccionManual}
                        onChange={(e) => setDireccionManual(e.target.value)}
                    />

                    <button type="submit" disabled={buscandoDireccion}>
                        {buscandoDireccion ? "Buscando..." : "Buscar direccion"}
                    </button>
                </form>
            )}


            {cargando && <p>Cargando centros medicos...</p>}

            {error && <p>{error}</p>}
            {!cargando && !error && ubicacionUsuario && centros.length === 0 && (
                <p>No se encontraron centros en tu área.</p>
            )}

            {radio === 5 && (
                <button onClick={() => setRadio(10)}>
                    Ampliar búsqueda a 10 km
                </button>
            )}

            {radio === 10 && (
                <button onClick={() => setRadio(5)}>
                    Volver a 5 km
                </button>
            )}

            <MapContainer
                key={ubicacionUsuario ? ubicacionUsuario.join(",") : "inicial"}
                center={ubicacionUsuario || ubicacion}
                zoom={14}
                style={{ height: "450px", width: "100%" }}
            >
                <TileLayer
                    attribution='&copy; OpenStreetMap contributors'
                    url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                />
                {ubicacionUsuario && (
                    <CircleMarker
                        center={ubicacionUsuario}
                        radius={9}
                        pathOptions={{
                            color: "white",
                            fillColor: "#2563eb",
                            fillOpacity: 1,
                            weight: 3
                        }}
                    >
                        <Popup>Tu ubicación actual</Popup>
                    </CircleMarker>
                )}

                {centros.map(centro => (
                    <Marker
                        key={centro.id}
                        position={[centro.latitud, centro.longitud]}
                        icon={crearIcono(centro.tipo)}
                    >
                        <Popup>
                            <b>{centro.nombre}</b>
                            <p>Tipo: {centro.tipo}</p>
                            <p>Direccion: {centro.direccion}</p>
                        </Popup>
                    </Marker>
                ))}
            </MapContainer>
        </div>
    );
}

export default MapaCentros;
