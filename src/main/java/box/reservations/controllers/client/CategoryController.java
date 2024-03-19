package box.reservations.controllers.client;

import box.reservations.entities.Category;
import box.reservations.services.client.ClientCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/categories")
public class CategoryController {
    @Autowired
    ClientCategoryService clientCategoryService;

    @GetMapping()
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok().body(clientCategoryService.getAll());
    }
}
