package box.reservations.services.employee;

import box.reservations.entities.Category;
import box.reservations.entities.Reservation;
import box.reservations.entities.Room;
import box.reservations.entities.SubCategory;
import box.reservations.repositories.CategoryRepository;
import box.reservations.repositories.ReservationRepository;
import box.reservations.repositories.RoomRepository;
import box.reservations.repositories.SubCategoryRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class EmployeeControlsCategoryService {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    SubCategoryRepository subCategoryRepository;

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    EmployeeRoomService employeeRoomService;

    @Autowired
    ReservationRepository reservationRepository;

    /**
     * Create a new category.
     *
     * @param  name   the name of the category
     * @return       the newly created category
     */
    public Category createCategory(@NotNull String name) {
        Category category = categoryRepository.save(new Category(name));
        log.info("New category was created");
        return category;
    }

    /**
     * Update a category by its ID with a new name.
     *
     * @param  id    the ID of the category to be updated
     * @param  name  the new name to set for the category
     * @return       the updated category
     * @throws       Exception if the category with the given ID is not found
     */
    public Category updateCategory(Long id, String name) throws Exception {
        Optional<Category> category = categoryRepository.findById(id);
        Category foundCategory = category.orElseThrow(() -> new Exception("Cannot find category by id"));
        foundCategory.setTitle(name);
        Category categoryToReturn = categoryRepository.save(foundCategory);
        log.info("Updated category with new name");
        return categoryToReturn;
    }


    /**
     * Delete a category by its ID.
     *
     * @param  categoryId   the ID of the category to be deleted
     * @throws Exception   if the category cannot be found by ID
     */
    @Transactional
    public void deleteCategory(Long categoryId) throws Exception {
        Category category = (categoryRepository.findById(categoryId)).orElseThrow(() ->
                new Exception("Cannot find category by id"));;
        deleteCategory(category);
    }

    /**
     * Deletes the given category along with its subcategories, rooms, and reservations.
     *
     * @param  category  the category to be deleted
     * @throws Exception  if room or subcategory cannot be found by the id
     */
    @Transactional
    private void deleteCategory (Category category) throws Exception {
        List<SubCategory> subcategoryList = subCategoryRepository.findByCategoryId(category.getId());

        for (SubCategory subCategory : subcategoryList) {
            List<Room> roomList = roomRepository.findBySubCategoryId(subCategory.getId());
            for (Room room : roomList) {
                employeeRoomService.removeSubCategoryFromRoom(room.getId(), subCategory.getId());
            }
            List<Reservation> reservationList = reservationRepository.findByRegisteredUnderSubCategoryId(
                    subCategory.getId()
            );
            for (Reservation reservation : reservationList) {
                reservationRepository.delete(reservation);
            }
        }

        categoryRepository.deleteById(category.getId());
        log.info("Category was deleted");
    }
}
