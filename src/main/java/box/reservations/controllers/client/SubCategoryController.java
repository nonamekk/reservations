package box.reservations.controllers.client;

import box.reservations.entities.SubCategory;
import box.reservations.services.client.ClientSubCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/subcategories")
public class SubCategoryController {

    @Autowired
    ClientSubCategoryService clientSubCategoryService;

    @GetMapping
    public ResponseEntity<List<SubCategory>> getAllSubCategories() {
        return ResponseEntity.ok().body(clientSubCategoryService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<SubCategory>> getAllSubCategoriesByCategoryId(
            @PathVariable(value = "id") Long id ) {
        return ResponseEntity.ok().body(clientSubCategoryService.getAllByCategoryId(id));
    }
}
