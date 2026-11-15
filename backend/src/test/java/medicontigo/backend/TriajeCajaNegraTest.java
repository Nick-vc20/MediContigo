package medicontigo.backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TriajeCajaNegraTest {

    @Value("${local.server.port}")
    private int puerto;

    private static final String SOLICITUD_VALIDA = """
            {
                "sintomaPrincipal": "DOLOR_CABEZA",
                "duracion": "UNO_TRES_DIAS",
                "intensidad": "LEVE",
                "sintomasAdicionales": "Cansancio",
                "senalesAlarma": "NO"
            }
            """;

    private HttpResponse<String> enviarSolicitud(String contenido)
            throws IOException, InterruptedException {

        HttpRequest solicitud = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + puerto + "/api/triaje"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(contenido))
                .build();

        try (HttpClient cliente = HttpClient.newHttpClient()) {
            return cliente.send(
                    solicitud,
                    HttpResponse.BodyHandlers.ofString()
            );
        }
    }

    @Test
    void cn01CuestionarioValidoSinSenalesAlarma()
            throws IOException, InterruptedException {

        HttpResponse<String> respuesta = enviarSolicitud(SOLICITUD_VALIDA);

        assertEquals(200, respuesta.statusCode());
        assertTrue(respuesta.body().contains(
                "\"nivelOrientacion\":\"SIN_CLASIFICACION_CLINICA\""
        ));
        assertTrue(respuesta.body().contains(
                "\"requiereAtencionInmediata\":false"
        ));
    }

    @Test
    void cn02CuestionarioConSenalesAlarma()
            throws IOException, InterruptedException {

        String contenido = SOLICITUD_VALIDA.replace(
                "\"senalesAlarma\": \"NO\"",
                "\"senalesAlarma\": \"SI\""
        );

        HttpResponse<String> respuesta = enviarSolicitud(contenido);

        assertEquals(200, respuesta.statusCode());
        assertTrue(respuesta.body().contains(
                "\"nivelOrientacion\":\"ALERTA_REPORTADA\""
        ));
        assertTrue(respuesta.body().contains(
                "\"requiereAtencionInmediata\":true"
        ));
    }

    @Test
    void cn03CuestionarioConSenalesNoConfirmadas()
            throws IOException, InterruptedException {

        String contenido = SOLICITUD_VALIDA.replace(
                "\"senalesAlarma\": \"NO\"",
                "\"senalesAlarma\": \"NO_SE\""
        );

        HttpResponse<String> respuesta = enviarSolicitud(contenido);

        assertEquals(200, respuesta.statusCode());
        assertTrue(respuesta.body().contains(
                "\"nivelOrientacion\":\"ALERTA_NO_CONFIRMADA\""
        ));
    }

    @Test
    void cn04SintomaPrincipalInvalido()
            throws IOException, InterruptedException {

        String contenido = SOLICITUD_VALIDA.replace(
                "\"DOLOR_CABEZA\"",
                "\"SINTOMA_INVALIDO\""
        );

        HttpResponse<String> respuesta = enviarSolicitud(contenido);

        assertEquals(400, respuesta.statusCode());
    }

    @Test
    void cn05DuracionObligatoriaAusente()
            throws IOException, InterruptedException {

        String contenido = """
                {
                    "sintomaPrincipal": "DOLOR_CABEZA",
                    "intensidad": "LEVE",
                    "sintomasAdicionales": "",
                    "senalesAlarma": "NO"
                }
                """;

        HttpResponse<String> respuesta = enviarSolicitud(contenido);

        assertEquals(400, respuesta.statusCode());
    }

    @Test
    void cn06DescripcionSuperiorAlLimite()
            throws IOException, InterruptedException {

        String contenido = SOLICITUD_VALIDA.replace(
                "Cansancio",
                "a".repeat(501)
        );

        HttpResponse<String> respuesta = enviarSolicitud(contenido);

        assertEquals(400, respuesta.statusCode());
    }

    @Test
    void cn07CuestionarioConDescripcionEnElLimite()
            throws IOException, InterruptedException {

        String contenido = SOLICITUD_VALIDA.replace(
                "Cansancio",
                "a".repeat(500)
        );

        HttpResponse<String> respuesta = enviarSolicitud(contenido);

        assertEquals(200, respuesta.statusCode());
        assertTrue(respuesta.body().contains(
                "\"nivelOrientacion\":\"SIN_CLASIFICACION_CLINICA\""
        ));
    }
}
