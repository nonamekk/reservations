package box.reservations.payload.requests.subcategory;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
public class SubCategoryIdNameRequest {
    @NotNull(message = "subcategory_id must not be null")
    @Min(0)
    private Long subcategory_id;
    @NotNull(message = "name must not be null")
    @Length(min = 6, max = 200, message
            = "name must be between 6 and 200 characters")
    private String name;
}
