
package medicontigo.backend.repositorio;

import medicontigo.backend.modelo.Resena;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    // HU-18: reseñas del paciente autenticado (con su centro, para mostrar el nombre)
    @Query("select r from Resena r join fetch r.centro "
         + "where r.paciente.id = :pacienteId order by r.fechaCreacion desc")
    List<Resena> buscarDelPaciente(@Param("pacienteId") Long pacienteId);
}
