package medicontigo.backend;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import io.jsonwebtoken.Claims;
import medicontigo.backend.modelo.Administrador;
import medicontigo.backend.repositorio.AdministradorRepository;
import medicontigo.backend.seguridad.InterceptorAutenticacion;
import medicontigo.backend.seguridad.JwtService;
import medicontigo.backend.seguridad.Rol;
import medicontigo.backend.seguridad.TokenInvalidoException;
import medicontigo.backend.seguridad.TokenRevocadoService;
import medicontigo.backend.servicio.AdministradorService;

@ExtendWith(MockitoExtension.class)
class AdministradorCajaBlancaTest {

    private static final String SECRETO = "MiClaveSecretaParaElProyectoDeIngenieriaDeSoftware123";
    private static final String CORREO = "admin@medicontigo.com";
    private static final String CLAVE = "admin123";

    @Mock AdministradorRepository repositorio;
    @Mock Environment entorno;
    @InjectMocks AdministradorService servicio;

    JwtService jwt;
    InterceptorAutenticacion interceptor;

    @BeforeEach
    public void preparar() {
        jwt = new JwtService(SECRETO, 30, 60, new TokenRevocadoService());
        interceptor = new InterceptorAutenticacion(jwt, Rol.ADMIN);
    }

    private Administrador admin(String clave) {
        return new Administrador(1, CORREO, clave);
    }

    private MockHttpServletRequest peticion(String cabecera) {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/analytics/resumen");
        if (cabecera != null) {
            request.addHeader("Authorization", cabecera);
        }
        return request;
    }

    @Test
    void servicioCorreoNoRegistradoDevuelveFalse() {
        when(repositorio.findByCorreo("otro@correo.com")).thenReturn(Optional.empty());
        assertFalse(servicio.validarLogin("otro@correo.com", CLAVE));
    }

    @Test
    void servicioPerfilDesarrolloClaveIgualDevuelveTrue() {
        when(repositorio.findByCorreo(CORREO)).thenReturn(Optional.of(admin(CLAVE)));
        when(entorno.acceptsProfiles(any(Profiles.class))).thenReturn(false);
        assertTrue(servicio.validarLogin(CORREO, CLAVE));
    }

    @Test
    void servicioPerfilDesarrolloClaveDistintaDevuelveFalse() {
        when(repositorio.findByCorreo(CORREO)).thenReturn(Optional.of(admin(CLAVE)));
        when(entorno.acceptsProfiles(any(Profiles.class))).thenReturn(false);
        assertFalse(servicio.validarLogin(CORREO, "incorrecta"));
    }

    @Test
    void servicioPerfilProduccionBcryptCoincideDevuelveTrue() {
        String hash = new BCryptPasswordEncoder().encode(CLAVE);
        when(repositorio.findByCorreo(CORREO)).thenReturn(Optional.of(admin(hash)));
        when(entorno.acceptsProfiles(any(Profiles.class))).thenReturn(true);
        assertTrue(servicio.validarLogin(CORREO, CLAVE));
    }

    @Test
    void servicioPerfilProduccionBcryptNoCoincideDevuelveFalse() {
        String hash = new BCryptPasswordEncoder().encode(CLAVE);
        when(repositorio.findByCorreo(CORREO)).thenReturn(Optional.of(admin(hash)));
        when(entorno.acceptsProfiles(any(Profiles.class))).thenReturn(true);
        assertFalse(servicio.validarLogin(CORREO, "incorrecta"));
    }

    @Test
    void jwtSecretoCortoLanzaExcepcion() {
        IllegalStateException ex = assertThrows(IllegalStateException.class,
            () -> new JwtService("corta", 30, 60, new TokenRevocadoService()));
        assertNotNull(ex);
    }

    @Test
    void jwtTokenAdminTieneCorreoRolYVencimientoDe30Minutos() {
        Claims claims = jwt.validar(jwt.generarTokenAdmin(CORREO));
        assertEquals(CORREO, claims.getSubject());
        assertEquals(Rol.ADMIN, jwt.rolDe(claims));
        assertEquals(30 * 60_000, claims.getExpiration().getTime() - claims.getIssuedAt().getTime());
    }

    @Test
    void jwtTokenVencidoLanzaExcepcion() {
        JwtService jwtVencido = new JwtService(SECRETO, -1, 60, new TokenRevocadoService());
        String vencido = jwtVencido.generarTokenAdmin(CORREO);
        TokenInvalidoException ex = assertThrows(TokenInvalidoException.class, () -> jwt.validar(vencido));
        assertEquals("Tu sesión ha expirado. Inicia sesión nuevamente", ex.getMessage());
    }

    @Test
    void jwtTokenAlteradoLanzaExcepcion() {
        String token = jwt.generarTokenAdmin(CORREO);
        String alterado = token.substring(0, token.length() - 3) + "abc";
        assertNotNull(assertThrows(TokenInvalidoException.class, () -> jwt.validar(alterado)));
    }

    @Test
    void jwtTokenFirmadoConOtraClaveLanzaExcepcion() {
        JwtService otro = new JwtService("OtraClaveSecretaDistintaParaProbarFirmaInvalida12345", 30, 60,
            new TokenRevocadoService());
        String token = otro.generarTokenAdmin(CORREO);
        assertNotNull(assertThrows(TokenInvalidoException.class, () -> jwt.validar(token)));
    }

    @Test
    void jwtTokenRevocadoLanzaExcepcion() {
        String token = jwt.generarTokenAdmin(CORREO);
        jwt.revocar(jwt.validar(token));
        TokenInvalidoException ex = assertThrows(TokenInvalidoException.class, () -> jwt.validar(token));
        assertEquals("La sesión fue cerrada. Inicia sesión nuevamente", ex.getMessage());
    }

    @Test
    void interceptorPreflightDejaPasarSinToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("OPTIONS", "/api/admin/centros/1/ocupacion");
        request.addHeader("Origin", "http://localhost:5173");
        request.addHeader("Access-Control-Request-Method", "PATCH");
        assertTrue(interceptor.preHandle(request, new MockHttpServletResponse(), new Object()));
    }

    @Test
    void interceptorSinCabeceraDevuelve401() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        assertFalse(interceptor.preHandle(peticion(null), response, new Object()));
        assertEquals(401, response.getStatus());
    }

    @Test
    void interceptorCabeceraSinBearerDevuelve401() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        assertFalse(interceptor.preHandle(peticion("Basic abc123"), response, new Object()));
        assertEquals(401, response.getStatus());
    }

    @Test
    void interceptorTokenAdminValidoDejaPasar() throws Exception {
        MockHttpServletRequest request = peticion("Bearer " + jwt.generarTokenAdmin(CORREO));
        MockHttpServletResponse response = new MockHttpServletResponse();
        assertTrue(interceptor.preHandle(request, response, new Object()));
        assertNotNull(request.getAttribute(InterceptorAutenticacion.ATRIBUTO_CLAIMS));
    }

    @Test
    void interceptorTokenVencidoDevuelve401() throws Exception {
        JwtService jwtVencido = new JwtService(SECRETO, -1, 60, new TokenRevocadoService());
        String vencido = jwtVencido.generarTokenAdmin(CORREO);
        MockHttpServletResponse response = new MockHttpServletResponse();
        assertFalse(interceptor.preHandle(peticion("Bearer " + vencido), response, new Object()));
        assertEquals(401, response.getStatus());
    }

    @Test
    void interceptorTokenDePacienteDevuelve403() throws Exception {
        String token = jwt.generarTokenPaciente(1L, "paciente@correo.com");
        MockHttpServletResponse response = new MockHttpServletResponse();
        assertFalse(interceptor.preHandle(peticion("Bearer " + token), response, new Object()));
        assertEquals(403, response.getStatus());
    }
}
