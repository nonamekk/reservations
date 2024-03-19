package box.reservations.payload.requests.room;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class RoomIdUserIdRequest {
    @NotNull(message = "room id must not be null")
    @Min(0)
    private Long room_id;

    @NotNull(message = "user id must not be null")
    @Min(0)
    private Long user_id;
}
