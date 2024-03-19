package box.reservations.repositories;

import box.reservations.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("SELECT r FROM Room r JOIN r.subcategoriesAssigned s WHERE s.id = :subCategoryId")
    List<Room> findBySubCategoryId(@Param("subCategoryId") Long subCategoryId);

//    List<Room> findBySubCategoryId(Long subCategoryId);
//    Optional<Room> findByUserId(Long userId);

    @Query("SELECT r FROM Room r JOIN r.usersAssigned u WHERE u.id = :userId")
    Optional<Room> findByUserId(@Param("userId") Long userId);
}
