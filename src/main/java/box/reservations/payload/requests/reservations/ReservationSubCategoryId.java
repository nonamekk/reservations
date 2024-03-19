package box.reservations.payload.requests.reservations;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReservationSubCategoryId {
    @NotNull(message = "id must not be null")
    @Min(0)
    private Long subcategory_id;
}
