package box.reservations.payload.responses.room;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;


@Getter
@AllArgsConstructor
public class AllRoomsResponse {
    private RoomSimplified user_assigned_in_room;
    private List<RoomSimplified> rooms;
}
