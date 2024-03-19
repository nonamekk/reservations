package box.reservations.controllers.employee;

import box.reservations.entities.Category;
import box.reservations.payload.requests.ByIdRequest;
import box.reservations.payload.requests.category.CategoryIdNameRequest;
import box.reservations.payload.requests.category.CategoryNameRequest;
import box.reservations.services.employee.EmployeeControlsCategoryService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/employee/controls/category")
public class ControlsCategoryController {

    @Autowired
    EmployeeControlsCategoryService employeeControlsCategoryService;

    @PostMapping
    public ResponseEntity<Category> createCategory(
            @RequestBody @Validated @NotNull CategoryNameRequest categoryNameRequest) throws Exception {
        return ResponseEntity.ok().body(employeeControlsCategoryService.
                createCategory(
                        categoryNameRequest.getName()
                ));
    }

    @PatchMapping
    public ResponseEntity<Category> updateCategory(
            @RequestBody @Validated @NotNull CategoryIdNameRequest categoryIdNameRequest) throws Exception {
                return ResponseEntity.ok().body(employeeControlsCategoryService
                        .updateCategory(
                                categoryIdNameRequest.getId(),
                                categoryIdNameRequest.getName()
                        ));
    }

    @DeleteMapping
    public ResponseEntity<?> deleteCategory(
            @RequestBody @Validated @NotNull ByIdRequest request) throws Exception {
        employeeControlsCategoryService.deleteCategory(request.getId());
        return ResponseEntity.ok().build();
    }

}
