import { useEffect, useMemo, useRef, useState } from "react";
import { MapContainer, TileLayer, Marker, Tooltip, CircleMarker, useMap } from "react-leaflet";
import L from "leaflet";
import "leaflet/dist/leaflet.css";

import { buscarCentrosCercanos } from "../api/centrosApi";
import { TIPOS_LEYENDA, estiloTipo, formatoCosto, ocupacionDe } from "../utils/formato";
import PerfilCentro from "./PerfilCentro";
import "../mapa.css";

const ORIGEN_POR_DEFECTO = [-12.046374, -77.042793];

const iconos = {};

function crearIcono(tipo, seleccionado) {
    const { color, letra } = estiloTipo(tipo);
    const clave = `${letra}-${seleccionado}`;

    if (!iconos[clave]) {
        const tam = seleccionado ? 42 : 32;
        iconos[clave] = L.divIcon({
            className: "",
            html: `<div class="marcador ${seleccionado ? "marcador-activo" : ""}"
                style="background:${color};width:${tam}px;height:${tam}px">${letra}</div>`,
            iconSize: [tam, tam],
            iconAnchor: [tam / 2, tam / 2]
        });
    }
    return iconos[clave];
}

function AjustarVista({ origen, centros }) {
    const mapa = useMap();

    useEffect(() => {
        const puntos = [origen, ...centros.map(c => [Number(c.latitud), Number(c.longitud)])];
        if (puntos.length === 1) {
            mapa.setView(origen, 14);
            return;
        }
        mapa.fitBounds(L.latLngBounds(puntos), { padding: [40, 40], maxZoom: 15 });
    }, [origen, centros, mapa]);

    return null;
}

function Enfocar({ centro }) {
    const mapa = useMap();

    useEffect(() => {
        if (centro) {
            mapa.flyTo([Number(centro.latitud), Number(centro.longitud)],
                Math.max(mapa.getZoom(), 15), { duration: 0.6 });
        }
    }, [centro, mapa]);

    return null;
}

function MapaCentros() {

    const [origen, setOrigen] = useState(null);
    const [etiquetaOrigen, setEtiquetaOrigen] = useState("");
    const [mensajeGPS, setMensajeGPS] = useState("");
    const [radio, setRadio] = useState(5);
    const [centros, setCentros] = useState([]);
    const [cargando, setCargando] = useState(false);
    const [error, setError] = useState("");
    const [direccionManual, setDireccionManual] = useState("");
    const [buscandoDireccion, setBuscandoDireccion] = useState(false);
    const [seleccionadoId, setSeleccionadoId] = useState(null);
    const panel = useRef(null);

    useEffect(() => {
        if (!navigator.geolocation) {
            setMensajeGPS("Tu navegador no permite obtener la ubicacion. Puedes buscar una direccion.");
            setOrigen(ORIGEN_POR_DEFECTO);
            setEtiquetaOrigen("Lima Centro");
            return;
        }

        navigator.geolocation.getCurrentPosition(
            (posicion) => {
                setOrigen([posicion.coords.latitude, posicion.coords.longitude]);
                setEtiquetaOrigen("Tu ubicación actual");
            },
            () => {
                setMensajeGPS("No se pudo obtener tu ubicacion. Puedes buscar una direccion.");
                setOrigen(ORIGEN_POR_DEFECTO);
                setEtiquetaOrigen("Lima Centro");
            },
            { enableHighAccuracy: true, timeout: 10000 }
        );
    }, []);

    useEffect(() => {
        if (!origen) {
            return;
        }

        let vigente = true;
        setCargando(true);
        setError("");

        buscarCentrosCercanos(origen[0], origen[1], radio)
            .then(datos => {
                if (vigente) setCentros(datos);
            })
            .catch(() => {
                if (vigente) {
                    setCentros([]);
                    setError("No se pudieron cargar los centros medicos");
                }
            })
            .finally(() => {
                if (vigente) setCargando(false);
            });

        return () => { vigente = false; };
    }, [origen, radio]);

    async function buscarDireccion(e) {
        e.preventDefault();

        if (!direccionManual.trim() || buscandoDireccion) {
            return;
        }

        setBuscandoDireccion(true);
        setError("");

        try {
            const consulta = encodeURIComponent(direccionManual);
            const respuesta = await fetch(
                `https://nominatim.openstreetmap.org/search?q=${consulta}&format=jsonv2&limit=1&countrycodes=pe`
            );

            if (!respuesta.ok) {
                throw new Error("Error al buscar la direccion");
            }

            const datos = await respuesta.json();

            if (datos.length === 0) {
                setError("No se encontro la direccion ingresada");
                return;
            }

            setOrigen([Number(datos[0].lat), Number(datos[0].lon)]);
            setEtiquetaOrigen(direccionManual.trim());
            setMensajeGPS("");
            setSeleccionadoId(null);
            setRadio(5);

        } catch {
            setError("No se pudo buscar la direccion");
        } finally {
            setBuscandoDireccion(false);
        }
    }

    function seleccionar(id) {
        setSeleccionadoId(id);

        if (window.innerWidth < 900) {
            setTimeout(() => panel.current?.scrollIntoView({ behavior: "smooth", block: "start" }), 50);
        }
    }

    const seleccionado = useMemo(
        () => centros.find(c => c.id === seleccionadoId) || null,
        [centros, seleccionadoId]
    );

    return (
        <div className="mapa-pagina">

            <div className="mapa-barra">
                <div>
                    <h2>Centros médicos cercanos</h2>
                    {origen && <p className="mapa-origen">Buscando cerca de: <b>{etiquetaOrigen}</b></p>}

                    <ul className="mapa-leyenda" aria-label="Tipos de establecimiento">
                        {TIPOS_LEYENDA.map(tipo => (
                            <li key={tipo.etiqueta}>
                                <span className="leyenda-punto" style={{ background: tipo.color }}>{tipo.letra}</span>
                                {tipo.etiqueta}
                            </li>
                        ))}
                    </ul>
                </div>

                <div className="mapa-controles">
                    <div className="mapa-radio" role="group" aria-label="Radio de búsqueda">
                        {[5, 10, 15, 20].map(km => (
                            <button
                                key={km}
                                type="button"
                                className={radio === km ? "activo" : ""}
                                aria-pressed={radio === km}
                                onClick={() => setRadio(km)}
                            >
                                {km} km
                            </button>
                        ))}
                    </div>

                    <form className="mapa-buscador" onSubmit={buscarDireccion}>
                        <input
                            type="text"
                            aria-label="Buscar otra zona"
                            placeholder="Buscar otra zona: Cercado de Lima, Perú"
                            value={direccionManual}
                            onChange={(e) => setDireccionManual(e.target.value)}
                        />
                        <button type="submit" disabled={buscandoDireccion}>
                            {buscandoDireccion ? "Buscando..." : "Buscar"}
                        </button>
                    </form>
                </div>
            </div>

            {mensajeGPS && <p className="mapa-aviso">{mensajeGPS}</p>}
            {error && <p className="error" role="alert">{error}</p>}

            <div className="mapa-cuerpo">

                {/* Panel lateral: lista de centros o perfil del centro elegido */}
                <aside className="mapa-panel" ref={panel}>
                    {seleccionadoId ? (
                        <PerfilCentro
                            id={seleccionadoId}
                            compacto
                            alVolver={() => setSeleccionadoId(null)}
                        />
                    ) : (
                        <>
                            <h3 className="mapa-panel-titulo">
                                {cargando ? "Buscando..." : `${centros.length} centros en ${radio} km`}
                            </h3>

                            {!cargando && !error && centros.length === 0 && (
                                <p className="perfil-vacio">
                                    No se encontraron centros en tu área. Prueba ampliando el radio de búsqueda.
                                </p>
                            )}

                            {centros.map(centro => {
                                const { color, letra } = estiloTipo(centro.tipo);
                                const ocupacion = ocupacionDe(centro.nivelOcupacion);

                                return (
                                    <button
                                        key={centro.id}
                                        type="button"
                                        className="centro-item"
                                        onClick={() => seleccionar(centro.id)}
                                    >
                                        <span className="centro-item-icono" style={{ background: color }}>{letra}</span>
                                        <span className="centro-item-info">
                                            <strong>{centro.nombre}</strong>
                                            <small>{centro.direccion}</small>
                                            <small>
                                                {centro.distanciaKm.toFixed(1)} km · {formatoCosto(centro.costoConsulta)} · espera {centro.tiempoEspera}
                                            </small>
                                        </span>
                                        <span className={`ocupacion ocupacion-${ocupacion.clase}`}>{ocupacion.etiqueta}</span>
                                    </button>
                                );
                            })}
                        </>
                    )}
                </aside>

                <div className="mapa-mapa">
                    {origen && (
                        <MapContainer
                            key={origen.join(",")}
                            center={origen}
                            zoom={14}
                            style={{ height: "100%", width: "100%" }}
                        >
                            <TileLayer
                                attribution='&copy; OpenStreetMap contributors'
                                url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                            />

                            <AjustarVista origen={origen} centros={centros} />
                            <Enfocar centro={seleccionado} />

                            <CircleMarker
                                center={origen}
                                radius={9}
                                pathOptions={{ color: "white", fillColor: "#2563eb", fillOpacity: 1, weight: 3 }}
                            >
                                <Tooltip>{etiquetaOrigen}</Tooltip>
                            </CircleMarker>

                            {centros.map(centro => (
                                <Marker
                                    key={centro.id}
                                    position={[centro.latitud, centro.longitud]}
                                    icon={crearIcono(centro.tipo, centro.id === seleccionadoId)}
                                    title={centro.nombre}
                                    eventHandlers={{ click: () => seleccionar(centro.id) }}
                                >
                                    <Tooltip direction="top" offset={[0, -16]}>{centro.nombre}</Tooltip>
                                </Marker>
                            ))}
                        </MapContainer>
                    )}
                </div>

            </div>
        </div>
    );
}

export default MapaCentros;
