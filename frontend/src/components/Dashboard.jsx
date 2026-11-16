
import { useEffect, useState } from "react";
import {
    BarChart, Bar, XAxis, YAxis, CartesianGrid,
    Tooltip, ResponsiveContainer, PieChart, Pie,
    Cell, Legend
} from "recharts";

const colores = ["#2563eb", "#16a34a", "#f59e0b", "#dc2626"];

function Dashboard() {

    const [datos, setDatos] = useState(null);
    const [cargando, setCargando] = useState(true);
    const [error, setError] = useState("");
    const [fechaDesde, setFechaDesde] = useState("");
    const [fechaHasta, setFechaHasta] = useState("");


    useEffect(() => {
        if (fechaDesde && fechaHasta && fechaDesde > fechaHasta) {
            setError("La fecha inicial no puede ser mayor que la final");
            setCargando(false);
            return;
        }

        setCargando(true);
        setError("");

        const parametros = new URLSearchParams();

        if (fechaDesde) {
            parametros.append("desde", fechaDesde);
        }

        if (fechaHasta) {
            parametros.append("hasta", fechaHasta);
        }

        const url = `http://localhost:8081/api/analytics/resumen?${parametros}`;


        fetch(url, {
            headers: {
                Authorization: `Bearer ${localStorage.getItem("token")}`
            }
        })
            .then(respuesta => {
                if (!respuesta.ok) {
                    throw new Error("Error al obtener las estadisticas");
                }
                return respuesta.json();
            })
            .then(resultado => {
                setDatos(resultado);
            })
            .catch(() => {
                setError("No se pudieron cargar las estadisticas");
            })
            .finally(() => {
                setCargando(false);
            });

    }, [fechaDesde, fechaHasta]);


    if (cargando) {
        return <p>Cargando dashboard...</p>;
    }

    if (error) {
        return <p>{error}</p>;
    }

    if (!datos) {
        return <p>No hay información disponible.</p>;
    }

    const triajesPorDia = Object.entries(datos.triajesPorDia || {})
        .filter(([fecha]) => {
            const cumpleDesde = !fechaDesde || fecha >= fechaDesde;
            const cumpleHasta = !fechaHasta || fecha <= fechaHasta;

            return cumpleDesde && cumpleHasta;
        })
        .map(([fecha, cantidad]) => ({
            fecha,
            cantidad
        }));


    const especialidades = Object.entries(
        datos.especialidadesRecomendadas || {}
    ).map(([nombre, cantidad]) => ({
        nombre,
        cantidad
    }));


    const busquedasPorEspecialidad = Object.entries(
        datos.busquedasPorEspecialidad || {}
    )
        .map(([nombre, cantidad]) => ({
            nombre,
            cantidad
        }))
        .sort((a, b) => b.cantidad - a.cantidad);


    const busquedasPorDia = Object.entries(
        datos.busquedasPorDia || {}
    )
        .map(([fecha, cantidad]) => ({
            fecha,
            cantidad
        }))
        .sort((a, b) => a.fecha.localeCompare(b.fecha));


    const busquedasPorZona = Object.entries(
        datos.busquedasPorZona || {}
    )
        .map(([zona, cantidad]) => ({
            zona,
            cantidad
        }))
        .sort((a, b) => b.cantidad - a.cantidad);


    const exportarExcel = async () => {
        try {
            const ExcelJS = (await import("exceljs")).default;

            const libro = new ExcelJS.Workbook();
            libro.creator = "MediContigo";


            const resumen = libro.addWorksheet("Resumen");

            resumen.columns = [
                { header: "Indicador", key: "indicador", width: 35 },
                { header: "Valor", key: "valor", width: 25 }
            ];

            resumen.addRows([
                {
                    indicador: "Fecha desde",
                    valor: fechaDesde || "Sin limite"
                },
                {
                    indicador: "Fecha hasta",
                    valor: fechaHasta || "Sin limite"
                },
                {
                    indicador: "Pacientes registrados",
                    valor: datos.totalPacientes
                },

                {
                    indicador: "Triajes realizados",
                    valor: datos.totalTriajes
                },
                {
                    indicador: "Búsquedas realizadas",
                    valor: datos.totalBusquedas ?? 0
                }

            ]);


            const hojaTriajes = libro.addWorksheet("Triajes por dia");

            hojaTriajes.columns = [
                { header: "Fecha", key: "fecha", width: 25 },
                { header: "Cantidad", key: "cantidad", width: 20 }
            ];

            hojaTriajes.addRows(triajesPorDia);


            const hojaEspecialidades = libro.addWorksheet("Especialidades");

            hojaEspecialidades.columns = [
                { header: "Especialidad", key: "nombre", width: 35 },
                { header: "Cantidad", key: "cantidad", width: 20 }
            ];

            hojaEspecialidades.addRows(especialidades);

            const hojaBusquedas = libro.addWorksheet("Busquedas por especialidad");

            hojaBusquedas.columns = [
                { header: "Especialidad", key: "nombre", width: 35 },
                { header: "Cantidad", key: "cantidad", width: 20 }
            ];

            hojaBusquedas.addRows(busquedasPorEspecialidad);


            const hojaBusquedasDia = libro.addWorksheet("Busquedas por dia");

            hojaBusquedasDia.columns = [
                { header: "Fecha", key: "fecha", width: 25 },
                { header: "Cantidad", key: "cantidad", width: 20 }
            ];

            hojaBusquedasDia.addRows(busquedasPorDia);


            const hojaZonas = libro.addWorksheet("Busquedas por zona");

            hojaZonas.columns = [
                { header: "Zona de interes", key: "zona", width: 35 },
                { header: "Cantidad", key: "cantidad", width: 20 }
            ];

            hojaZonas.addRows(busquedasPorZona);


            libro.worksheets.forEach(hoja => {
                const encabezado = hoja.getRow(1);

                encabezado.font = {
                    bold: true,
                    color: { argb: "FFFFFFFF" }
                };

                encabezado.fill = {
                    type: "pattern",
                    pattern: "solid",
                    fgColor: { argb: "FF2563EB" }
                };
            });


            const buffer = await libro.xlsx.writeBuffer();

            const archivo = new Blob([buffer], {
                type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            });

            const url = URL.createObjectURL(archivo);
            const enlace = document.createElement("a");

            enlace.href = url;
            enlace.download = "MediContigo_Reporte_Metricas.xlsx";

            document.body.appendChild(enlace);
            enlace.click();
            enlace.remove();

            setTimeout(() => URL.revokeObjectURL(url), 1000);

        } catch (error) {
            console.error("Error al exportar Excel:", error);
            alert("No se pudo generar el reporte Excel.");
        }
    };


    const exportarPDF = async () => {
        try {
            const { jsPDF } = await import("jspdf");
            const { autoTable } = await import("jspdf-autotable");

            const pdf = new jsPDF();

            pdf.setFillColor(37, 99, 235);
            pdf.rect(0, 0, 210, 30, "F");

            pdf.setTextColor(255, 255, 255);
            pdf.setFontSize(18);
            pdf.text("MediContigo", 14, 13);

            pdf.setFontSize(12);
            pdf.text("Reporte de Metricas Administrativas", 14, 22);

            pdf.setTextColor(40, 40, 40);
            pdf.setFontSize(10);

            pdf.text(
                `Generado: ${new Date().toLocaleString("es-PE")}`,
                14,
                39
            );


            autoTable(pdf, {
                startY: 46,
                head: [["Indicador", "Valor"]],
                body: [
                    ["Fecha desde", fechaDesde || "Sin limite"],
                    ["Fecha hasta", fechaHasta || "Sin limite"],
                    ["Pacientes registrados", String(datos.totalPacientes)],
                    ["Triajes realizados", String(datos.totalTriajes)],
                    ["Búsquedas realizadas", String(datos.totalBusquedas ?? 0)]

                ],
                theme: "grid",
                headStyles: { fillColor: [37, 99, 235] }
            });


            autoTable(pdf, {
                startY: pdf.lastAutoTable.finalY + 12,
                head: [["Fecha", "Cantidad de triajes"]],
                body: triajesPorDia.length > 0
                    ? triajesPorDia.map(item => [
                        item.fecha,
                        String(item.cantidad)
                    ])
                    : [["Sin registros", "0"]],
                theme: "striped",
                headStyles: { fillColor: [37, 99, 235] }
            });


            autoTable(pdf, {
                startY: pdf.lastAutoTable.finalY + 12,
                head: [["Especialidad recomendada", "Cantidad"]],
                body: especialidades.length > 0
                    ? especialidades.map(item => [
                        item.nombre,
                        String(item.cantidad)
                    ])
                    : [["Sin registros", "0"]],
                theme: "striped",
                headStyles: { fillColor: [37, 99, 235] }
            });


            autoTable(pdf, {
                startY: pdf.lastAutoTable.finalY + 12,
                head: [["Especialidad buscada", "Cantidad"]],
                body: busquedasPorEspecialidad.length > 0
                    ? busquedasPorEspecialidad.map(item => [
                        item.nombre,
                        String(item.cantidad)
                    ])
                    : [["Sin registros", "0"]],
                theme: "striped",
                headStyles: { fillColor: [37, 99, 235] }
            });


            autoTable(pdf, {
                startY: pdf.lastAutoTable.finalY + 12,
                head: [["Fecha", "Cantidad de busquedas"]],
                body: busquedasPorDia.length > 0
                    ? busquedasPorDia.map(item => [
                        item.fecha,
                        String(item.cantidad)
                    ])
                    : [["Sin registros", "0"]],
                theme: "striped",
                headStyles: { fillColor: [37, 99, 235] }
            });


            autoTable(pdf, {
                startY: pdf.lastAutoTable.finalY + 12,
                head: [["Zona de interes", "Cantidad de busquedas"]],
                body: busquedasPorZona.length > 0
                    ? busquedasPorZona.map(item => [
                        item.zona,
                        String(item.cantidad)
                    ])
                    : [["Sin registros", "0"]],
                theme: "striped",
                headStyles: { fillColor: [37, 99, 235] }
            });


            pdf.save("MediContigo_Reporte_Metricas.pdf");

        } catch (error) {
            console.error("Error al exportar PDF:", error);
            alert("No se pudo generar el reporte PDF.");
        }
    };

    const tarjeta = {
        background: "#eff6ff",
        padding: "20px",
        borderRadius: "10px",
        flex: 1,
        minWidth: "180px",
        color: "#1e293b"
    };

    return (
        <div style={{ padding: "24px", color: "#1e293b" }}>

            <h2>Dashboard de Métricas</h2>
            <p>Resumen de actividades de MediContigo</p>


            {/* Filtros por fecha */}
            <div style={{
                display: "flex",
                gap: "15px",
                marginTop: "20px",
                flexWrap: "wrap",
                alignItems: "center"
            }}>
                <label>
                    Desde:
                    <input
                        type="date"
                        value={fechaDesde}
                        onChange={(e) => setFechaDesde(e.target.value)}
                    />
                </label>

                <label>
                    Hasta:
                    <input
                        type="date"
                        min={fechaDesde || undefined}
                        value={fechaHasta}
                        onChange={(e) => setFechaHasta(e.target.value)}
                    />
                </label>

                <button onClick={() => {
                    setFechaDesde("");
                    setFechaHasta("");
                }}>
                    Limpiar filtros
                </button>


                <button
                    onClick={exportarExcel}
                    style={{
                        backgroundColor: "#16a34a",
                        color: "white",
                        padding: "10px 18px",
                        border: "none",
                        borderRadius: "6px",
                        cursor: "pointer",
                        fontWeight: "bold"
                    }}
                >
                    Exportar Excel
                </button>


                <button
                    onClick={exportarPDF}
                    style={{
                        backgroundColor: "#dc2626",
                        color: "white",
                        padding: "10px 18px",
                        border: "none",
                        borderRadius: "6px",
                        cursor: "pointer",
                        fontWeight: "bold"
                    }}
                >
                    Exportar PDF
                </button>

            </div>


            {/* Indicadores generales */}
            <div style={{
                display: "flex",
                gap: "20px",
                flexWrap: "wrap",
                marginTop: "20px"
            }}>

                <div style={tarjeta}>
                    <h3>Pacientes registrados</h3>
                    <h1>{datos.totalPacientes}</h1>
                </div>

                <div style={tarjeta}>
                    <h3>Triajes realizados</h3>
                    <h1>{datos.totalTriajes}</h1>
                </div>

                <div style={tarjeta}>
                    <h3>Búsquedas por especialidad</h3>
                    <h1>{datos.totalBusquedas ?? 0}</h1>
                </div>

            </div>

            {/* Grafico de barras */}
            <div style={{ marginTop: "35px" }}>
                <h3>Triajes realizados por día</h3>

                {triajesPorDia.length > 0 ? (
                    <div style={{ width: "100%", height: 300 }}>
                        <ResponsiveContainer>
                            <BarChart data={triajesPorDia}>
                                <CartesianGrid strokeDasharray="3 3" />
                                <XAxis dataKey="fecha" />
                                <YAxis allowDecimals={false} />
                                <Tooltip />
                                <Bar dataKey="cantidad" fill="#2563eb" />
                            </BarChart>
                        </ResponsiveContainer>
                    </div>
                ) : (
                    <p>Aún no hay triajes registrados para mostrar.</p>
                )}
            </div>

            {/* Grafico circular */}
            <div style={{ marginTop: "35px" }}>
                <h3>Especialidades recomendadas</h3>

                {especialidades.length > 0 ? (
                    <div style={{ width: "100%", height: 350 }}>
                        <ResponsiveContainer>
                            <PieChart>
                                <Pie
                                    data={especialidades}
                                    dataKey="cantidad"
                                    nameKey="nombre"
                                    cx="50%"
                                    cy="50%"
                                    outerRadius={100}
                                    label
                                >
                                    {especialidades.map((item, index) => (
                                        <Cell
                                            key={item.nombre}
                                            fill={colores[index % colores.length]}
                                        />
                                    ))}
                                </Pie>
                                <Tooltip />
                                <Legend />
                            </PieChart>
                        </ResponsiveContainer>
                    </div>
                ) : (
                    <p>Aún no hay especialidades recomendadas.</p>
                )}
            </div>


            {/* HU-15: Especialidades mas buscadas */}
            <div style={{ marginTop: "35px" }}>
                <h3>Especialidades más buscadas</h3>
                <p>
                    Cantidad de búsquedas realizadas por especialidad médica.
                </p>

                {busquedasPorEspecialidad.length > 0 ? (
                    <div
                        style={{
                            width: "100%",
                            height: Math.max(
                                300,
                                busquedasPorEspecialidad.length * 45
                            )
                        }}
                    >
                        <ResponsiveContainer width="100%" height="100%">
                            <BarChart
                                data={busquedasPorEspecialidad}
                                layout="vertical"
                                margin={{
                                    top: 10,
                                    right: 30,
                                    left: 20,
                                    bottom: 10
                                }}
                            >
                                <CartesianGrid strokeDasharray="3 3" />

                                <XAxis
                                    type="number"
                                    allowDecimals={false}
                                />

                                <YAxis
                                    dataKey="nombre"
                                    type="category"
                                    width={150}
                                />

                                <Tooltip />

                                <Bar
                                    dataKey="cantidad"
                                    name="Búsquedas"
                                    fill="#16a34a"
                                />
                            </BarChart>
                        </ResponsiveContainer>
                    </div>
                ) : (
                    <p>
                        Aún no hay búsquedas por especialidad registradas.
                    </p>
                )}
            </div>


            {/* HU-15: Busquedas realizadas por dia */}
            <div style={{ marginTop: "35px" }}>
                <h3>Búsquedas realizadas por día</h3>
                <p>
                    Cantidad de búsquedas de especialidades médicas por fecha.
                </p>

                {busquedasPorDia.length > 0 ? (
                    <div style={{ width: "100%", height: 300 }}>
                        <ResponsiveContainer width="100%" height="100%">
                            <BarChart data={busquedasPorDia}>
                                <CartesianGrid strokeDasharray="3 3" />

                                <XAxis dataKey="fecha" />

                                <YAxis allowDecimals={false} />

                                <Tooltip />

                                <Bar
                                    dataKey="cantidad"
                                    name="Búsquedas"
                                    fill="#2563eb"
                                />
                            </BarChart>
                        </ResponsiveContainer>
                    </div>
                ) : (
                    <p>
                        Aún no hay búsquedas registradas para mostrar.
                    </p>
                )}
            </div>


            {/* HU-15: Busquedas por zona geografica */}
            <div style={{ marginTop: "35px" }}>
                <h3>Búsquedas por zona geográfica</h3>

                <p>
                    Distribución de búsquedas según la zona de interés
                    seleccionada por los usuarios.
                </p>

                {busquedasPorZona.length > 0 ? (
                    <div style={{ width: "100%", height: 320 }}>
                        <ResponsiveContainer width="100%" height="100%">
                            <BarChart
                                data={busquedasPorZona}
                                margin={{
                                    top: 10,
                                    right: 30,
                                    left: 10,
                                    bottom: 10
                                }}
                            >
                                <CartesianGrid strokeDasharray="3 3" />

                                <XAxis dataKey="zona" />

                                <YAxis allowDecimals={false} />

                                <Tooltip />

                                <Bar
                                    dataKey="cantidad"
                                    name="Búsquedas"
                                    fill="#7c3aed"
                                />
                            </BarChart>
                        </ResponsiveContainer>
                    </div>
                ) : (
                    <p>
                        Aún no hay búsquedas con zona geográfica registrada.
                    </p>
                )}
            </div>


        </div>
    );
}

export default Dashboard;
