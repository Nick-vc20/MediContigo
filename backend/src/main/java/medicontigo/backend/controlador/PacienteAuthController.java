package medicontigo.backend.controlador;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import medicontigo.backend.dto.LoginPacienteRequest;
import medicontigo.backend.dto.PacienteResponse;
import medicontigo.backend.dto.RegistroPacienteRequest;
import medicontigo.backend.dto.SesionPacienteResponse;
import medicontigo.backend.modelo.Paciente;
import medicontigo.backend.seguridad.JwtService;
import medicontigo.backend.seguridad.Rol;
import medicontigo.backend.servicio.PacienteService;

// Endpoints públicos del paciente: registro (HU-17) e inicio de sesión (HU-18)
@RestController
@RequestMapping("/api/pacientes")
public class PacienteAuthController {

    private final PacienteService pacienteService;
    private final JwtService jwtService;

    public PacienteAuthController(PacienteService pacienteService, JwtService jwtService) {
        this.pacienteService = pacienteService;
        this.jwtService = jwtService;
    }

    // HU-17 (17.3): 201 creado · 400 datos inválidos · 409 correo duplicado
    @PostMapping("/registro")
    public ResponseEntity<PacienteResponse> registrar(@RequestBody RegistroPacienteRequest datos) {
        try {
            Paciente paciente = pacienteService.registrar(datos);
            return ResponseEntity.status(HttpStatus.CREATED).body(PacienteResponse.desde(paciente));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    // HU-18 (18.2): 200 con token JWT de rol PACIENTE · 401 credenciales incorrectas
    @PostMapping("/login")
    public SesionPacienteResponse login(@RequestBody(required = false) LoginPacienteRequest credenciales) {
        if (credenciales == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, PacienteService.MENSAJE_CREDENCIALES);
        }
        Paciente paciente = pacienteService.autenticar(credenciales.correo(), credenciales.contrasena());
        String token = jwtService.generarTokenPaciente(paciente.getId(), paciente.getCorreo());
        long expiraEn = jwtService.validar(token).getExpiration().getTime();

        return new SesionPacienteResponse(token, Rol.PACIENTE.name(), expiraEn, PacienteResponse.desde(paciente));
    }
}
