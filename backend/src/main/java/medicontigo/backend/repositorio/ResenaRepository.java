
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

    // HU-14: listado del panel de moderación con filtros opcionales
    @Query("select r from Resena r join fetch r.centro "
         + "where (:estado is null or r.estado = :estado) "
         + "and (:centroId is null or r.centro.id = :centroId) "
         + "and (:soloConAlertas = false or r.alertasContenido is not null) "
         + "order by r.fechaCreacion desc, r.id desc")
    List<Resena> buscarParaModeracion(
        @Param("estado") String estado,
        @Param("centroId") Integer centroId,
        @Param("soloConAlertas") boolean soloConAlertas);

    // HU-14: cantidad de reseñas por estado (resumen del panel)
    @Query("select r.estado, count(r) from Resena r group by r.estado")
    List<Object[]> contarPorEstado();

    long countByEstadoAndAlertasContenidoIsNotNull(String estado);
}
