package medicontigo.backend.seguridad;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import com.jayway.jsonpath.JsonPath;

import io.jsonwebtoken.Claims;
import medicontigo.backend.modelo.Paciente;
import medicontigo.backend.repositorio.PacienteRepository;

// HU-18 (18.4): acceso, expiración de sesión, cierre de sesión y acceso cruzado entre roles
@SpringBootTest
@Transactional
class AutenticacionPacienteTest {

    private static final String CORREO = "paciente@correo.com";
    private static final String CLAVE = "Segura123";

    @Autowired WebApplicationContext contexto;
    MockMvc mvc;
    @Autowired JwtService jwtService;
    @Autowired PacienteRepository pacienteRepository;

    @BeforeEach
    void registrarPaciente() throws Exception {
        // Se reutiliza el mismo contexto de Spring que las demás pruebas (misma BD H2 en memoria)
        mvc = MockMvcBuilders.webAppContextSetup(contexto).build();
        mvc.perform(post("/api/pacientes/registro").contentType(MediaType.APPLICATION_JSON).content("""
                {"nombreCompleto": "María Paciente", "correo": "%s", "contrasena": "%s",
                 "aceptaTratamientoDatos": true}
                """.formatted(CORREO, CLAVE)))
            .andExpect(status().isCreated());
    }

    private String loginPaciente(String correo, String clave) throws Exception {
        String cuerpo = mvc.perform(post("/api/pacientes/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"correo\": \"%s\", \"contrasena\": \"%s\"}".formatted(correo, clave)))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        return JsonPath.read(cuerpo, "$.token");
    }

    private String loginAdmin() throws Exception {
        String cuerpo = mvc.perform(post("/api/admin/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"correo\": \"admin@medicontigo.com\", \"password\": \"admin123\"}"))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        return JsonPath.read(cuerpo, "$.token");
    }

    private static String bearer(String token) {
        return "Bearer " + token;
    }

    // ---------- Inicio de sesión ----------

    @Test
    void loginCorrectoEntregaTokenConRolPacienteYVencimiento() throws Exception {
        long antes = System.currentTimeMillis();
        String cuerpo = mvc.perform(post("/api/pacientes/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"correo\": \"PACIENTE@correo.com \", \"contrasena\": \"%s\"}".formatted(CLAVE)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.rol").value("PACIENTE"))
            .andExpect(jsonPath("$.paciente.nombreCompleto").value("María Paciente"))
            .andExpect(jsonPath("$.paciente.contrasenaHash").doesNotExist())
            .andReturn().getResponse().getContentAsString();

        Claims claims = jwtService.validar(JsonPath.read(cuerpo, "$.token"));
        assertEquals(Rol.PACIENTE, jwtService.rolDe(claims));
        long expiraEn = ((Number) JsonPath.read(cuerpo, "$.expiraEn")).longValue();
        assertEquals(claims.getExpiration().getTime(), expiraEn);
        assertTrue(expiraEn > antes, "La sesión debe tener vencimiento futuro");
        assertTrue(expiraEn <= antes + jwtService.getMinutosPaciente() * 60_000 + 5_000);
    }

    @Test
    void contrasenaIncorrectaDevuelve401ConMensajeGenerico() throws Exception {
        mvc.perform(post("/api/pacientes/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"correo\": \"%s\", \"contrasena\": \"Incorrecta1\"}".formatted(CORREO)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value("Correo o contraseña incorrectos"));
    }

    @Test
    void correoNoRegistradoDevuelveElMismoMensajeQueContrasenaIncorrecta() throws Exception {
        mvc.perform(post("/api/pacientes/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"correo\": \"nadie@correo.com\", \"contrasena\": \"%s\"}".formatted(CLAVE)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value("Correo o contraseña incorrectos"));
    }

    // ---------- Rutas privadas ----------

    @Test
    void rutasPrivadasSinTokenDevuelven401() throws Exception {
        mvc.perform(get("/api/paciente/perfil")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/paciente/resenas")).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/paciente/centros/1/resenas").contentType(MediaType.APPLICATION_JSON)
                .content("{\"puntuacion\": 5, \"comentario\": \"Excelente atención en general\"}"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void tokenAlteradoDevuelve401() throws Exception {
        String token = loginPaciente(CORREO, CLAVE);
        String alterado = token.substring(0, token.length() - 3) + "abc";
        mvc.perform(get("/api/paciente/perfil").header("Authorization", bearer(alterado)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void pacienteAutenticadoAccedeASuPerfilYPublicaResenaConSuNombre() throws Exception {
        String token = loginPaciente(CORREO, CLAVE);

        mvc.perform(get("/api/paciente/perfil").header("Authorization", bearer(token)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.correo").value(CORREO));

        mvc.perform(post("/api/paciente/centros/1/resenas").header("Authorization", bearer(token))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"puntuacion\": 4, \"comentario\": \"Buena atención y rápida\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.nombrePaciente").value("María Paciente"))
            .andExpect(jsonPath("$.estado").value("PENDIENTE"));

        mvc.perform(get("/api/paciente/resenas").header("Authorization", bearer(token)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].centroNombre").value("Hospital Lima"));
    }

    @Test
    void cadaPacienteSoloVeSusPropiasResenas() throws Exception {
        String tokenMaria = loginPaciente(CORREO, CLAVE);
        mvc.perform(post("/api/paciente/centros/1/resenas").header("Authorization", bearer(tokenMaria))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"puntuacion\": 5, \"comentario\": \"Muy buena experiencia\"}"))
            .andExpect(status().isCreated());

        mvc.perform(post("/api/pacientes/registro").contentType(MediaType.APPLICATION_JSON).content("""
                {"nombreCompleto": "Pedro", "correo": "pedro@correo.com", "contrasena": "Clave1234",
                 "aceptaTratamientoDatos": true}
                """)).andExpect(status().isCreated());
        String tokenPedro = loginPaciente("pedro@correo.com", "Clave1234");

        mvc.perform(get("/api/paciente/resenas").header("Authorization", bearer(tokenPedro)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }

    // ---------- Expiración y cierre de sesión ----------

    @Test
    void tokenVencidoDevuelve401ConMensajeDeSesionExpirada() throws Exception {
        Paciente paciente = pacienteRepository.findByCorreo(CORREO).orElseThrow();
        String vencido = jwtService.generar(String.valueOf(paciente.getId()), Rol.PACIENTE,
            Map.of("correo", CORREO), -1_000);

        mvc.perform(get("/api/paciente/perfil").header("Authorization", bearer(vencido)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value("Tu sesión ha expirado. Inicia sesión nuevamente"));
    }

    @Test
    void despuesDeCerrarSesionElTokenDejaDeFuncionar() throws Exception {
        String token = loginPaciente(CORREO, CLAVE);

        mvc.perform(get("/api/paciente/perfil").header("Authorization", bearer(token)))
            .andExpect(status().isOk());

        mvc.perform(post("/api/paciente/logout").header("Authorization", bearer(token)))
            .andExpect(status().isNoContent());

        mvc.perform(get("/api/paciente/perfil").header("Authorization", bearer(token)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value("La sesión fue cerrada. Inicia sesión nuevamente"));

        // Un nuevo inicio de sesión vuelve a dar acceso
        String nuevo = loginPaciente(CORREO, CLAVE);
        mvc.perform(get("/api/paciente/perfil").header("Authorization", bearer(nuevo)))
            .andExpect(status().isOk());
    }

    // ---------- Acceso cruzado entre roles ----------

    @Test
    void tokenDeAdministradorNoAccedeARutasDelPaciente() throws Exception {
        String tokenAdmin = loginAdmin();
        mvc.perform(get("/api/paciente/perfil").header("Authorization", bearer(tokenAdmin)))
            .andExpect(status().isForbidden());
        mvc.perform(post("/api/paciente/centros/1/resenas").header("Authorization", bearer(tokenAdmin))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"puntuacion\": 1, \"comentario\": \"Reseña falsa del administrador\"}"))
            .andExpect(status().isForbidden());
    }

    @Test
    void tokenDePacienteNoAccedeARutasDelAdministrador() throws Exception {
        String tokenPaciente = loginPaciente(CORREO, CLAVE);
        mvc.perform(patch("/api/admin/1/ocupacion").param("nivel", "ALTO")
                .header("Authorization", bearer(tokenPaciente)))
            .andExpect(status().isForbidden());

        mvc.perform(patch("/api/admin/1/ocupacion").param("nivel", "ALTO"))
            .andExpect(status().isUnauthorized());

        mvc.perform(patch("/api/admin/1/ocupacion").param("nivel", "ALTO")
                .header("Authorization", bearer(loginAdmin())))
            .andExpect(status().isOk());
    }

    @Test
    void lasCredencialesDePacienteNoSirvenEnElLoginDeAdministrador() throws Exception {
        mvc.perform(post("/api/admin/login").contentType(MediaType.APPLICATION_JSON)
                .content("{\"correo\": \"%s\", \"password\": \"%s\"}".formatted(CORREO, CLAVE)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void lasRutasPublicasSiguenAbiertas() throws Exception {
        mvc.perform(get("/api/publico/centros/1/resenas")).andExpect(status().isOk());
        mvc.perform(get("/api/publico/centros/1")).andExpect(status().isOk());
    }
}
