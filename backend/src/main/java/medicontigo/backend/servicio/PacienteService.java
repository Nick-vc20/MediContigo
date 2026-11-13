package medicontigo.backend.servicio;

import java.util.Locale;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import medicontigo.backend.dto.RegistroPacienteRequest;
import medicontigo.backend.modelo.Paciente;
import medicontigo.backend.repositorio.PacienteRepository;

// HU-17 (registro) y HU-18 (validación de credenciales) del paciente
@Service
public class PacienteService {

    private static final Pattern FORMATO_CORREO =
        Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    // Mínimo 8 caracteres con al menos una letra y un número
    private static final Pattern FORMATO_CONTRASENA =
        Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d).{8,72}$");

    public static final String MENSAJE_CREDENCIALES = "Correo o contraseña incorrectos";

    private final PacienteRepository pacienteRepository;
    private final BCryptPasswordEncoder cifrador = new BCryptPasswordEncoder();

    public PacienteService(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    @Transactional
    public Paciente registrar(RegistroPacienteRequest datos) {
        if (datos == null) {
            throw new IllegalArgumentException("Debes ingresar tus datos de registro");
        }

        String nombre = datos.nombreCompleto() == null ? "" : datos.nombreCompleto().trim();
        if (nombre.length() < 2 || nombre.length() > 80) {
            throw new IllegalArgumentException("El nombre debe tener entre 2 y 80 caracteres");
        }

        String correo = normalizarCorreo(datos.correo());
        if (correo.length() > 120 || !FORMATO_CORREO.matcher(correo).matches()) {
            throw new IllegalArgumentException("Ingresa un correo electrónico válido");
        }

        String contrasena = datos.contrasena();
        if (contrasena == null || !FORMATO_CONTRASENA.matcher(contrasena).matches()) {
            throw new IllegalArgumentException(
                "La contraseña debe tener entre 8 y 72 caracteres e incluir letras y números"
            );
        }

        if (!Boolean.TRUE.equals(datos.aceptaTratamientoDatos())) {
            throw new IllegalArgumentException(
                "Debes aceptar el tratamiento de tus datos personales para registrarte"
            );
        }

        if (pacienteRepository.existsByCorreo(correo)) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Ya existe una cuenta registrada con ese correo"
            );
        }

        Paciente paciente = new Paciente();
        paciente.setNombreCompleto(nombre);
        paciente.setCorreo(correo);
        paciente.setContrasenaHash(cifrador.encode(contrasena));
        paciente.setAceptaTratamientoDatos(true);

        return pacienteRepository.save(paciente);
    }

    // Devuelve el paciente si las credenciales son correctas; si no, 401 con mensaje genérico
    @Transactional(readOnly = true)
    public Paciente autenticar(String correo, String contrasena) {
        if (correo == null || contrasena == null || contrasena.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, MENSAJE_CREDENCIALES);
        }

        return pacienteRepository.findByCorreo(normalizarCorreo(correo))
            .filter(p -> cifrador.matches(contrasena, p.getContrasenaHash()))
            .orElseThrow(() ->
                new ResponseStatusException(HttpStatus.UNAUTHORIZED, MENSAJE_CREDENCIALES));
    }

    @Transactional(readOnly = true)
    public Paciente obtener(Long id) {
        return pacienteRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.UNAUTHORIZED, "La sesión ya no es válida"));
    }

    static String normalizarCorreo(String correo) {
        return correo == null ? "" : correo.trim().toLowerCase(Locale.ROOT);
    }
}
