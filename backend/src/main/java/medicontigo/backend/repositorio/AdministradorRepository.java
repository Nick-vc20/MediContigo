package medicontigo.backend.repositorio;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import medicontigo.backend.modelo.Administrador;

@Repository
public interface AdministradorRepository extends JpaRepository<Administrador, Integer> {

    // Spring Data JPA crea la consulta SQL automáticamente leyendo el nombre del método
    Optional<Administrador> findByCorreo(String correo);
}