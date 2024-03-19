package box.reservations.repositories;

import box.reservations.entities.Ticket;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Optional<Ticket> findBySecret(String secret);
    @NotNull List<Ticket> findAll();
}
