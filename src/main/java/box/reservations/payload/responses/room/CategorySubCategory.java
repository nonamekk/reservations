package box.reservations.payload.responses.room;

import box.reservations.entities.Category;
import box.reservations.entities.SubCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategorySubCategory {
    private Category category;
    private SubCategory subCategory;
}
