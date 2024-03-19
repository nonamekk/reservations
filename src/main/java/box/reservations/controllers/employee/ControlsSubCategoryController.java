package box.reservations.controllers.employee;

import box.reservations.entities.SubCategory;
import box.reservations.payload.requests.ByIdRequest;
import box.reservations.payload.requests.subcategory.SubCategoryIdNameRequest;
import box.reservations.payload.requests.subcategory.SubCategoryNameCategoryIdRequest;
import box.reservations.services.employee.EmployeeControlsSubcategoryService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/employee/controls/subcategory")
public class ControlsSubCategoryController {

    @Autowired
    EmployeeControlsSubcategoryService employeeControlsSubcategoryService;

    @PostMapping
    public ResponseEntity<SubCategory> createSubCategory(
            @RequestBody @Validated @NotNull
            SubCategoryNameCategoryIdRequest subCategoryNameCategoryIdRequest) throws Exception {

        SubCategory subCategory = employeeControlsSubcategoryService.createSubCategory(
                subCategoryNameCategoryIdRequest.getCategory_id(),
                subCategoryNameCategoryIdRequest.getName()
        );

        return ResponseEntity.ok().body(subCategory);
    }

    @PatchMapping
    public ResponseEntity<SubCategory> updateSubCategory (
            @RequestBody @Validated @NotNull
            SubCategoryIdNameRequest requestBody
    ) throws Exception {

        SubCategory subCategory = employeeControlsSubcategoryService.updateSubCategory(
                requestBody.getSubcategory_id(),
                requestBody.getName()
        );

        return ResponseEntity.ok().body(subCategory);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteCategory(
            @RequestBody @Validated @NotNull ByIdRequest request) throws Exception {
        employeeControlsSubcategoryService.deleteSubCategory(request.getId());
        return ResponseEntity.ok().build();
    }
}
