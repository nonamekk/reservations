package box.reservations.payload.requests.room;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RoomIdRequest {
    @NotNull(message = "room id must not be null")
    @Min(0)
    private Long room_id;
}
