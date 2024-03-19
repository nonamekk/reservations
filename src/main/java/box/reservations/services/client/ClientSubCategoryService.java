package box.reservations.services.client;

import box.reservations.entities.SubCategory;
import box.reservations.repositories.SubCategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ClientSubCategoryService {
    @Autowired
    SubCategoryRepository subCategoryRepository;

    /**
     * Retrieves all subcategories.
     *
     * @return         	the list of all subcategories
     */
    public List<SubCategory> getAll() {
        log.info("Finding all subcategories");
        return subCategoryRepository.findAll();
    }

    /**
     * Gets all subcategories by category id.
     *
     * @param  id   the category id
     * @return      the list of subcategories
     */
    public List<SubCategory> getAllByCategoryId(Long id) {
        log.info("Finding all subcategories of category id");
        return subCategoryRepository.findByCategoryId(id);
    }

}
