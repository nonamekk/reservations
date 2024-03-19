package box.reservations.repositories;

import box.reservations.entities.Category;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @NotNull List<Category> findAll();

    @Transactional
    void deleteByTitle(String title);
}
