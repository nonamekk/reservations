package box.reservations.payload.requests.room;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

@Getter
public class RoomIdNameRequest {
    @NotNull(message = "room id must not be null")
    @Min(0)
    private Long room_id;
    @NotNull(message = "name must not be null")
    @Length(min = 3, max = 200, message
            = "name must be between 6 and 200 characters")
    private String name;
}
