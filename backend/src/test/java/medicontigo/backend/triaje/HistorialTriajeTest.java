package medicontigo.backend.triaje;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.jayway.jsonpath.JsonPath;

import medicontigo.backend.modelo.Paciente;
import medicontigo.backend.modelo.ResultadoTriaje;
import medicontigo.backend.repositorio.PacienteRepository;
import medicontigo.backend.repositorio.ResultadoTriajeRepository;

// HU-12 (12.4): persistencia del historial de triaje y consulta por usuario
@SpringBootTest
@Transactional
class HistorialTriajeTest {

    private static final String CORREO = "triaje@correo.com";
    private static final String CLAVE = "Segura123";
    private static final String RUTA = "/api/paciente/triajes";

    @Autowired WebApplicationContext contexto;
    @Autowired ResultadoTriajeRepository triajeRepository;
    @Autowired PacienteRepository pacienteRepository;
    MockMvc mvc;
    String tokenPaciente;

    @BeforeEach
    void preparar() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(contexto).build();
        tokenPaciente = registrarYLoguear("Lucía Triaje", CORREO, CLAVE);
    }

    private String registrarYLoguear(String nombre, String correo, String clave) throws Exception {
        mvc.perform(post("/api/pacientes/registro").contentType(MediaType.APPLICATION_JSON).content("""
                {"nombreCompleto": "%s", "correo": "%s", "contrasena": "%s",
                 "aceptaTratamientoDatos": true}
                """.formatted(nombre, correo, clave)))
            .andExpect(status().isCreated());
        String cuerpo = mvc.perform(post("/api/pacientes/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"correo\": \"%s\", \"contrasena\": \"%s\"}".formatted(correo, clave)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        return JsonPath.read(cuerpo, "$.token");
    }

    private ResultActions guardar(String token, String json) throws Exception {
        return mvc.perform(post(RUTA).header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON).content(json));
    }

    private static String triaje(String sintomas, String nivel) {
        return """
            {"sintomas": "%s", "nivelUrgencia": "%s", "especialidadRecomendada": "Medicina General",
             "recomendacion": "Acudir a consulta en las próximas 24 horas"}
            """.formatted(sintomas, nivel);
    }

    // ---------- Persistencia ----------

    @Test
    void guardarTriajeLoPersisteAsociadoAlPaciente() throws Exception {
        String cuerpo = guardar(tokenPaciente, triaje("Fiebre y dolor de cabeza", "alerta_no_confirmada"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.nivelUrgencia").value("ALERTA_NO_CONFIRMADA"))
            .andExpect(jsonPath("$.fechaTriaje").isNotEmpty())
            .andReturn().getResponse().getContentAsString();

        Long id = ((Number) JsonPath.read(cuerpo, "$.id")).longValue();
        ResultadoTriaje guardado = triajeRepository.findById(id).orElseThrow();
        Paciente paciente = pacienteRepository.findByCorreo(CORREO).orElseThrow();

        assertEquals(paciente.getId(), guardado.getPaciente().getId());
        assertEquals("Fiebre y dolor de cabeza", guardado.getSintomas());
        assertEquals("Medicina General", guardado.getEspecialidadRecomendada());
        assertNotNull(guardado.getFechaTriaje());
    }

    @Test
    void elHistorialSeListaDelMasRecienteAlMasAntiguo() throws Exception {
        guardar(tokenPaciente, triaje("Tos leve", "SIN_CLASIFICACION_CLINICA")).andExpect(status().isCreated());
        guardar(tokenPaciente, triaje("Dolor de pecho intenso", "ALERTA_REPORTADA")).andExpect(status().isCreated());

        mvc.perform(get(RUTA).header("Authorization", "Bearer " + tokenPaciente))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].sintomas").value("Dolor de pecho intenso"))
            .andExpect(jsonPath("$[1].sintomas").value("Tos leve"));
    }

    @Test
    void historialVacioDevuelveListaVacia() throws Exception {
        mvc.perform(get(RUTA).header("Authorization", "Bearer " + tokenPaciente))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }

    // ---------- Consulta por usuario ----------

    @Test
    void cadaPacienteSoloVeSuPropioHistorial() throws Exception {
        String cuerpo = guardar(tokenPaciente, triaje("Dolor de garganta", "SIN_CLASIFICACION_CLINICA"))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
        Long idLucia = ((Number) JsonPath.read(cuerpo, "$.id")).longValue();

        String tokenOtro = registrarYLoguear("Pedro Otro", "otro.triaje@correo.com", "Clave1234");

        mvc.perform(get(RUTA).header("Authorization", "Bearer " + tokenOtro))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));

        // No puede ver el detalle de un resultado ajeno
        mvc.perform(get(RUTA + "/" + idLucia).header("Authorization", "Bearer " + tokenOtro))
            .andExpect(status().isNotFound());

        // La dueña sí lo ve
        mvc.perform(get(RUTA + "/" + idLucia).header("Authorization", "Bearer " + tokenPaciente))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.sintomas").value("Dolor de garganta"));
    }

    @Test
    void elPacienteDelTokenEsElDuenoAunqueElCuerpoIntenteOtroId() throws Exception {
        guardar(tokenPaciente, """
            {"pacienteId": 999, "sintomas": "Mareos", "nivelUrgencia": "ALERTA_REPORTADA",
             "recomendacion": "Acudir a emergencias"}
            """).andExpect(status().isCreated());

        Paciente paciente = pacienteRepository.findByCorreo(CORREO).orElseThrow();
        List<ResultadoTriaje> historial =
            triajeRepository.findByPaciente_IdOrderByFechaTriajeDescIdDesc(paciente.getId());
        assertEquals(1, historial.size());
    }

    // ---------- Seguridad ----------

    @Test
    void sinTokenDevuelve401() throws Exception {
        mvc.perform(get(RUTA)).andExpect(status().isUnauthorized());
        mvc.perform(post(RUTA).contentType(MediaType.APPLICATION_JSON).content(triaje("Fiebre", "SIN_CLASIFICACION_CLINICA")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void tokenDeAdministradorDevuelve403() throws Exception {
        String cuerpo = mvc.perform(post("/api/admin/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"correo\": \"admin@medicontigo.com\", \"password\": \"admin123\"}"))
            .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        String tokenAdmin = JsonPath.read(cuerpo, "$.token");

        mvc.perform(get(RUTA).header("Authorization", "Bearer " + tokenAdmin))
            .andExpect(status().isForbidden());
    }

    // ---------- Validaciones ----------

    @Test
    void datosInvalidosDevuelven400YNoSeGuardanNada() throws Exception {
        guardar(tokenPaciente, triaje("", "SIN_CLASIFICACION_CLINICA"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Los síntomas deben tener entre 3 y 500 caracteres"));

        guardar(tokenPaciente, triaje("Fiebre alta", "GRAVE"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message")
                .value("El nivel de urgencia debe ser ALERTA_REPORTADA, ALERTA_NO_CONFIRMADA o SIN_CLASIFICACION_CLINICA"));

        guardar(tokenPaciente, "{\"sintomas\": \"Fiebre alta\", \"nivelUrgencia\": \"ALERTA_REPORTADA\"}")
            .andExpect(status().isBadRequest());

        mvc.perform(post(RUTA).header("Authorization", "Bearer " + tokenPaciente)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());

        Paciente paciente = pacienteRepository.findByCorreo(CORREO).orElseThrow();
        assertEquals(0, triajeRepository.countByPaciente_Id(paciente.getId()));
    }
}