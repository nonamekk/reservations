package box.reservations.payload.responses.room;

import box.reservations.entities.Room;
import box.reservations.entities.SubCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RoomSimplified {
    private final Long id;
    private final String name;
    private final List<SubCategory> subcategories;
    private final Long occupied_by;

    public RoomSimplified(Room room) {
        this.id = room.getId();
        this.name = room.getName();

        this.occupied_by = room.getUsersAssigned().stream().count();

        this.subcategories = room.getSubcategoriesAssigned();
    }
}
