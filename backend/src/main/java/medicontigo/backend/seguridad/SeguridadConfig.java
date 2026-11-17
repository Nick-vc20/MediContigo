package medicontigo.backend.seguridad;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// HU-18 (18.3): rutas privadas por rol
@Configuration
public class SeguridadConfig implements WebMvcConfigurer {

    private final JwtService jwtService;

    public SeguridadConfig(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(new InterceptorAutenticacion(jwtService, Rol.PACIENTE))
            .addPathPatterns("/api/paciente/**");



        registry.addInterceptor(new InterceptorAutenticacion(jwtService, Rol.ADMIN))
                .addPathPatterns("/api/admin/**", "/api/analytics/**")
                .excludePathPatterns("/api/admin/login");

    }
}
