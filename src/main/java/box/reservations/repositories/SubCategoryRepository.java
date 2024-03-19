package box.reservations.repositories;

import box.reservations.entities.SubCategory;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {

    List<SubCategory> findByCategoryTitle(String name);
    List<SubCategory> findByCategoryId(Long categoryId);

    List<SubCategory> findAllByTitle(String name);

    @Transactional
    void deleteByCategoryTitle(String title);

    @Transactional
    void deleteByCategoryId(Long id);

}
