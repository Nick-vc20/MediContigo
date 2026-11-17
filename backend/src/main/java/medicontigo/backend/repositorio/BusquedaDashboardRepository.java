
package medicontigo.backend.repositorio;

import medicontigo.backend.modelo.BusquedaDashboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusquedaDashboardRepository
        extends JpaRepository<BusquedaDashboard, Long> {
}
