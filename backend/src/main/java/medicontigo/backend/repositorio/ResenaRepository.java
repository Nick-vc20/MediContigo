
package medicontigo.backend.repositorio;

import medicontigo.backend.modelo.Resena;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, Long> {

    // Obtener todas las reseñas de un centro médico
    List<Resena> findByCentro_IdOrderByFechaCreacionDesc(
        Integer centroId
    );

    // Obtener reseñas según su estado
    List<Resena> findByCentro_IdAndEstadoOrderByFechaCreacionDesc(
        Integer centroId,
        String estado
    );
}
