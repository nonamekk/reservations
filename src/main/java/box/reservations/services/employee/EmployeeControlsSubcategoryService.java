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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class EmployeeControlsSubcategoryService {

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
     * Creates a new subcategory for the specified category.
     *
     * @param  categoryId       the ID of the category for which the subcategory is created
     * @param  subCategoryName  the name of the new subcategory
     * @return                 the newly created subcategory with id
     */
    public SubCategory createSubCategory(Long categoryId, String subCategoryName) throws Exception {
        Category category = (categoryRepository.findById(categoryId))
                .orElseThrow(() -> new Exception("Category with specified id not found"));

        List<SubCategory> curentSuCategoriesList = subCategoryRepository.findAllByTitle(subCategoryName);
        for (SubCategory subCategory : curentSuCategoriesList) {
            if (Objects.equals(subCategory.getCategory().getId(), categoryId)) {
                throw new Exception("SubCategory already exists under specified category id");
            }
        }

        SubCategory subCategory = new SubCategory(subCategoryName, category);
        SubCategory subCategoryToReturn = subCategoryRepository.save(subCategory);
        log.info("New subcategory is created");
        return subCategoryToReturn;
    }

    /**
     * Deletes a subcategory along with associated rooms and reservations.
     *
     * @param subCategoryId the ID of the subcategory to be deleted
     * @throws Exception if the subcategory with the specified ID is not found
     */
    @Transactional
    public void deleteSubCategory(Long subCategoryId) throws Exception {
        SubCategory subCategory = (subCategoryRepository.findById(subCategoryId))
                .orElseThrow(() -> new Exception("Subcategory with specified id not found"));

        List<Room> roomList = roomRepository.findBySubCategoryId(subCategoryId);
        for (Room room : roomList) {
            employeeRoomService.removeSubCategoryFromRoom(room.getId(), subCategory.getId());
        }

        List<Reservation> reservationList = reservationRepository.findByRegisteredUnderSubCategoryId(
                subCategory.getId()
        );
        for (Reservation reservation : reservationList) {
            reservationRepository.delete(reservation);
        }

        subCategoryRepository.delete(subCategory);
        log.info("Subcategory is deleted");
    }

    /**
     * Updates the subcategory with the specified ID to the new name.
     *
     * @param subCategoryId the ID of the subcategory to be updated
     * @param name the new name for the subcategory
     * @return the updated subcategory
     * @throws Exception if the subcategory with the specified ID is not found
     */
    public SubCategory updateSubCategory(Long subCategoryId, String name) throws Exception {
        SubCategory subCategory = (subCategoryRepository.findById(subCategoryId))
                .orElseThrow(() -> new Exception("Subcategory with specified id not found"));
        subCategory.setTitle(name);
        subCategoryRepository.save(subCategory);
        log.info("Subcategory is updated");
        return subCategory;
    }
}
