package box.reservations.payload.requests.room;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoomIdSubCategoryIdRequest {
    @NotNull(message = "room id must not be null")
    @Min(0)
    private Long room_id;

    @NotNull(message = "subcategory id must not be null")
    @Min(0)
    private Long subcategory_id;

}
