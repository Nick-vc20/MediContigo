// HU-12: historial de resultados de triaje del paciente (rutas privadas)
import { pedirComoPaciente } from "./pacientesApi";

// Listar el historial del paciente autenticado (del más reciente al más antiguo)
export const obtenerMiHistorialTriaje = () => pedirComoPaciente("/api/paciente/triajes");

// Detalle de un resultado del historial
export const obtenerResultadoTriaje = (id) => pedirComoPaciente(`/api/paciente/triajes/${id}`);

// Guardar un resultado en el historial (lo usará el asistente de triaje de HU-11)
// datos: { sintomas, nivelUrgencia, especialidadRecomendada, recomendacion }
export const guardarResultadoTriaje = (datos) =>
    pedirComoPaciente("/api/paciente/triajes", {
        method: "POST",
        body: JSON.stringify(datos),
    });