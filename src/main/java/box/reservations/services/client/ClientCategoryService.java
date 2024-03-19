package box.reservations.services.client;

import box.reservations.entities.Category;
import box.reservations.repositories.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ClientCategoryService {
    @Autowired
    CategoryRepository categoryRepository;

    /**
     * Retrieves all categories.
     *
     * @return         	list of all categories
     */
    public List<Category> getAll() {
        log.info("Finding all categories");
        return categoryRepository.findAll();
    }
}
