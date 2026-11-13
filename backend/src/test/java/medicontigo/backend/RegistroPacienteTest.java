package medicontigo.backend;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import medicontigo.backend.modelo.Paciente;
import medicontigo.backend.repositorio.PacienteRepository;

// HU-17 (17.4): pruebas de validación del registro de paciente
@SpringBootTest
@Transactional
class RegistroPacienteTest {

    @Autowired WebApplicationContext contexto;
    MockMvc mvc;
    @Autowired PacienteRepository pacienteRepository;

    @BeforeEach
    void prepararMockMvc() {
        // Se reutiliza el mismo contexto de Spring que las demás pruebas (misma BD H2 en memoria)
        mvc = MockMvcBuilders.webAppContextSetup(contexto).build();
    }

    private ResultActions registrar(String nombre, String correo, String contrasena, Boolean acepta)
            throws Exception {
        String json = """
            {"nombreCompleto": %s, "correo": %s, "contrasena": %s, "aceptaTratamientoDatos": %s}
            """.formatted(texto(nombre), texto(correo), texto(contrasena), acepta);
        return mvc.perform(post("/api/pacientes/registro")
            .contentType(MediaType.APPLICATION_JSON).content(json));
    }

    private static String texto(String valor) {
        return valor == null ? "null" : "\"" + valor + "\"";
    }

    @Test
    void registroValidoDevuelve201SinExponerLaContrasena() throws Exception {
        registrar("Ana Torres", "Ana.Torres@Correo.com ", "Segura123", true)
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.nombreCompleto").value("Ana Torres"))
            .andExpect(jsonPath("$.correo").value("ana.torres@correo.com"))
            .andExpect(jsonPath("$.contrasena").doesNotExist())
            .andExpect(jsonPath("$.contrasenaHash").doesNotExist());
    }

    @Test
    void laContrasenaSeGuardaCifradaConBCrypt() throws Exception {
        registrar("Luis Rojas", "luis@correo.com", "Segura123", true).andExpect(status().isCreated());

        Paciente guardado = pacienteRepository.findByCorreo("luis@correo.com").orElseThrow();
        assertNotEquals("Segura123", guardado.getContrasenaHash());
        assertTrue(guardado.getContrasenaHash().startsWith("$2"), "Debe ser un hash BCrypt");
        assertTrue(guardado.isAceptaTratamientoDatos());
        assertNotNull(guardado.getFechaAceptacionDatos());
    }

    @Test
    void correoDuplicadoDevuelve409AunqueCambienMayusculas() throws Exception {
        registrar("Ana Torres", "ana@correo.com", "Segura123", true).andExpect(status().isCreated());

        registrar("Otra Ana", "ANA@correo.com", "OtraClave9", true)
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message").value("Ya existe una cuenta registrada con ese correo"));

        assertEquals(1, pacienteRepository.findAll().stream()
            .filter(p -> p.getCorreo().equals("ana@correo.com")).count());
    }

    @Test
    void correoConFormatoInvalidoDevuelve400() throws Exception {
        registrar("Ana Torres", "ana-sin-arroba.com", "Segura123", true)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Ingresa un correo electrónico válido"));
    }

    @Test
    void contrasenaDebilDevuelve400() throws Exception {
        registrar("Ana Torres", "ana1@correo.com", "corta1", true).andExpect(status().isBadRequest());
        registrar("Ana Torres", "ana2@correo.com", "soloLetrasLargas", true).andExpect(status().isBadRequest());
        registrar("Ana Torres", "ana3@correo.com", "12345678", true).andExpect(status().isBadRequest());
    }

    @Test
    void nombreInvalidoDevuelve400() throws Exception {
        registrar(" ", "ana@correo.com", "Segura123", true).andExpect(status().isBadRequest());
        registrar(null, "ana@correo.com", "Segura123", true).andExpect(status().isBadRequest());
    }

    @Test
    void sinAceptarTratamientoDeDatosDevuelve400() throws Exception {
        registrar("Ana Torres", "ana@correo.com", "Segura123", false)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message")
                .value("Debes aceptar el tratamiento de tus datos personales para registrarte"));
        registrar("Ana Torres", "ana@correo.com", "Segura123", null).andExpect(status().isBadRequest());

        assertTrue(pacienteRepository.findByCorreo("ana@correo.com").isEmpty());
    }
}
