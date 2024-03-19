package box.reservations.services.client;

import box.reservations.entities.Room;
import box.reservations.repositories.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


public class ClientRoomService {

    @Autowired
    RoomRepository roomRepository;


    /**
     * Retrieves all rooms of a specific subcategory.
     *
     * @param  subCategoryId  the ID of the subcategory
     * @return                a list of rooms belonging to the specified subcategory
     */
    public List<Room> getAllRoomsOfSubCategory(Long subCategoryId) {
        return roomRepository.findBySubCategoryId(subCategoryId);
    }
}
