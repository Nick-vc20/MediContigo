package medicontigo.backend.servicio;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

/**
 * HU-14: filtro automático de contenido para reseñas.
 * No publica ni rechaza por sí solo: marca alertas que el administrador
 * revisa en el panel de moderación antes de decidir.
 */
@Service
public class FiltroContenidoService {

    public static final String LENGUAJE_OFENSIVO = "LENGUAJE_OFENSIVO";
    public static final String DATOS_PERSONALES = "DATOS_PERSONALES";
    public static final String ENLACE_EXTERNO = "ENLACE_EXTERNO";
    public static final String SPAM = "SPAM";

    // Palabras sin tildes y en minúsculas (el texto se normaliza antes de comparar)
    private static final Set<String> PALABRAS_OFENSIVAS = Set.of(
        "idiota", "idiotas", "estupido", "estupida", "estupidos", "imbecil", "imbeciles",
        "mierda", "carajo", "puta", "puto", "putos", "cojudo", "cojuda", "cojudos",
        "huevon", "huevona", "huevones", "pendejo", "pendeja", "pendejos",
        "maldito", "maldita", "malditos", "inutil", "inutiles", "tarado", "tarada",
        "conchatumadre", "ctm", "csm", "mrd", "basura"
    );

    private static final Pattern CORREO =
        Pattern.compile("[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+");
    // Celulares (9 dígitos que empiezan con 9) o cualquier número de 7+ dígitos, con espacios o guiones
    private static final Pattern TELEFONO =
        Pattern.compile("(?<!\\d)(\\+?51[\\s-]?)?9(?:[\\s-]?\\d){8}(?!\\d)|(?<!\\d)\\d(?:[\\s-]?\\d){6,}(?!\\d)");
    private static final Pattern ENLACE =
        Pattern.compile("(https?://|www\\.)\\S+|\\b[a-z0-9-]+\\.(com|net|org|pe|info|xyz|ly)(/\\S*)?\\b",
            Pattern.CASE_INSENSITIVE);
    // Un mismo carácter repetido 6 o más veces seguidas ("buenooooooo", "!!!!!!")
    private static final Pattern REPETICION = Pattern.compile("(.)\\1{5,}");

    /**
     * Analiza el texto y devuelve los códigos de alerta encontrados (lista vacía si está limpio).
     */
    public List<String> analizar(String texto) {
        List<String> alertas = new ArrayList<>();
        if (texto == null || texto.isBlank()) {
            return alertas;
        }

        if (contieneLenguajeOfensivo(texto)) {
            alertas.add(LENGUAJE_OFENSIVO);
        }
        if (CORREO.matcher(texto).find() || TELEFONO.matcher(texto).find()) {
            alertas.add(DATOS_PERSONALES);
        }
        // Un correo no cuenta como enlace (ya se marcó como dato personal)
        String sinCorreos = CORREO.matcher(texto).replaceAll(" ");
        if (ENLACE.matcher(sinCorreos).find()) {
            alertas.add(ENLACE_EXTERNO);
        }
        if (esSpam(texto)) {
            alertas.add(SPAM);
        }
        return alertas;
    }

    private boolean contieneLenguajeOfensivo(String texto) {
        for (String palabra : normalizar(texto).split("[^a-z0-9]+")) {
            if (PALABRAS_OFENSIVAS.contains(palabra)) {
                return true;
            }
        }
        return false;
    }

    private boolean esSpam(String texto) {
        if (REPETICION.matcher(texto).find()) {
            return true;
        }
        // Texto escrito casi todo en MAYÚSCULAS
        long letras = texto.chars().filter(Character::isLetter).count();
        long mayusculas = texto.chars().filter(Character::isUpperCase).count();
        return letras >= 20 && mayusculas * 100 / letras >= 70;
    }

    // minúsculas y sin tildes: "ESTÚPIDO" -> "estupido"
    static String normalizar(String texto) {
        return Normalizer.normalize(texto.toLowerCase(), Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "");
    }
}
