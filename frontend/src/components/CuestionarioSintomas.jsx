
import { useState } from "react";
import "../cuestionario.css";

const ESTADO_INICIAL = {
    sintomaPrincipal: "",
    duracion: "",
    intensidad: "",
    sintomasAdicionales: "",
    senalesAlarma: "",
};

export default function CuestionarioSintomas() {
    const [respuestas, setRespuestas] = useState(ESTADO_INICIAL);
    const [enviado, setEnviado] = useState(false);

    function actualizar(campo, valor) {
        setRespuestas((anterior) => ({
            ...anterior,
            [campo]: valor,
        }));
        setEnviado(false);
    }

    function enviar(evento) {
        evento.preventDefault();
        setEnviado(true);
    }

    function reiniciar() {
        setRespuestas(ESTADO_INICIAL);
        setEnviado(false);
    }

    return (
        <main className="panel-contenido">
            <section className="panel-tarjeta cuenta-seccion cuestionario">

                <h1>Orientación según tus síntomas</h1>

                <div className="cuestionario-introduccion">
                    <p>
                        Responde las siguientes preguntas para registrar
                        información sobre las molestias que presentas.
                    </p>

                    <p>
                        Este cuestionario es orientativo y no reemplaza
                        una evaluación médica profesional.
                    </p>
                </div>

                <form
                    className="cuestionario-formulario"
                    onSubmit={enviar}
                >

                    {/* Pregunta 1 */}
                    <div className="cuestionario-campo">
                        <label htmlFor="sintomaPrincipal">
                            1. ¿Cuál es tu síntoma principal?
                        </label>

                        <select
                            id="sintomaPrincipal"
                            value={respuestas.sintomaPrincipal}
                            onChange={(e) =>
                                actualizar(
                                    "sintomaPrincipal",
                                    e.target.value
                                )
                            }
                            required
                        >
                            <option value="">
                                Selecciona una opción
                            </option>
                            <option value="DOLOR_CABEZA">
                                Dolor de cabeza
                            </option>
                            <option value="DOLOR_ABDOMINAL">
                                Dolor abdominal
                            </option>
                            <option value="FIEBRE">
                                Fiebre
                            </option>
                            <option value="TOS">
                                Tos
                            </option>
                            <option value="MALESTAR_GENERAL">
                                Malestar general
                            </option>
                            <option value="OTRO">
                                Otro síntoma
                            </option>
                        </select>
                    </div>

                    {/* Pregunta 2 */}
                    <div className="cuestionario-campo">
                        <label htmlFor="duracion">
                            2. ¿Desde cuándo presentas los síntomas?
                        </label>

                        <select
                            id="duracion"
                            value={respuestas.duracion}
                            onChange={(e) =>
                                actualizar("duracion", e.target.value)
                            }
                            required
                        >
                            <option value="">
                                Selecciona una opción
                            </option>
                            <option value="MENOS_24_HORAS">
                                Menos de 24 horas
                            </option>
                            <option value="UNO_TRES_DIAS">
                                Entre 1 y 3 días
                            </option>
                            <option value="CUATRO_SIETE_DIAS">
                                Entre 4 y 7 días
                            </option>
                            <option value="MAS_SEMANA">
                                Más de una semana
                            </option>
                        </select>
                    </div>

                    {/* Pregunta 3 */}
                    <div className="cuestionario-campo">
                        <label htmlFor="intensidad">
                            3. ¿Qué tan intensa es tu molestia?
                        </label>

                        <select
                            id="intensidad"
                            value={respuestas.intensidad}
                            onChange={(e) =>
                                actualizar("intensidad", e.target.value)
                            }
                            required
                        >
                            <option value="">
                                Selecciona una opción
                            </option>
                            <option value="LEVE">
                                Leve
                            </option>
                            <option value="MODERADA">
                                Moderada
                            </option>
                            <option value="INTENSA">
                                Intensa
                            </option>
                        </select>
                    </div>

                    {/* Pregunta 4 */}
                    <div className="cuestionario-campo">
                        <label htmlFor="sintomasAdicionales">
                            4. ¿Presentas otros síntomas?
                        </label>

                        <textarea
                            id="sintomasAdicionales"
                            value={respuestas.sintomasAdicionales}
                            onChange={(e) =>
                                actualizar(
                                    "sintomasAdicionales",
                                    e.target.value
                                )
                            }
                            placeholder="Describe otras molestias, si las tienes"
                            rows={4}
                            maxLength={500}
                        />
                    </div>

                    {/* Pregunta 5 */}
                    <div className="cuestionario-campo">
                        <label htmlFor="senalesAlarma">
                            5. ¿Presentas alguna señal de alarma?
                        </label>

                        <select
                            id="senalesAlarma"
                            value={respuestas.senalesAlarma}
                            onChange={(e) =>
                                actualizar("senalesAlarma", e.target.value)
                            }
                            required
                        >
                            <option value="">
                                Selecciona una opción
                            </option>
                            <option value="SI">
                                Sí
                            </option>
                            <option value="NO">
                                No
                            </option>
                            <option value="NO_SE">
                                No estoy seguro
                            </option>
                        </select>
                    </div>

                    {/* Aviso de seguridad */}
                    <div className="cuestionario-alerta" role="note">
                        <strong>Importante:</strong>
                        <p>
                            Si tienes dificultad grave para respirar,
                            dolor intenso en el pecho, pérdida de
                            conciencia u otra emergencia, busca atención
                            médica inmediata. No esperes el resultado
                            de este cuestionario.
                        </p>
                    </div>

                    {/* Botones */}
                    <div className="cuestionario-botones">
                        <button type="submit">
                            Revisar respuestas
                        </button>

                        <button
                            type="button"
                            onClick={reiniciar}
                        >
                            Limpiar
                        </button>
                    </div>

                </form>

                {/* Resultado provisional */}
                {enviado && (
                    <section
                        className="cuestionario-resultado"
                        aria-live="polite"
                    >
                        <h2>Cuestionario completado</h2>

                        <p>
                            Has respondido las preguntas del formulario.
                        </p>

                        <p>
                            La evaluación y las recomendaciones
                            todavía no están implementadas.
                        </p>

                        <p>
                            Tus respuestas no se han enviado
                            ni guardado en el servidor.
                        </p>
                    </section>
                )}

            </section>
        </main>
    );
}
