package box.reservations.services.employee;

import box.reservations.entities.Reservation;
import box.reservations.entities.Room;
import box.reservations.entities.SubCategory;
import box.reservations.entities.User;
import box.reservations.payload.responses.room.RoomSimplified;
import box.reservations.repositories.ReservationRepository;
import box.reservations.repositories.RoomRepository;
import box.reservations.repositories.SubCategoryRepository;
import box.reservations.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class EmployeeRoomService {

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    SubCategoryRepository subCategoryRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ReservationRepository reservationRepository;

    /**
     * Creates a room with the given name and subcategory ID.
     *
     * @param  name          the name of the room
     * @param  subCategoryId the ID of the subcategory
     * @return               the created room
     */
    public Room createRoom(String name, Long subCategoryId) throws Exception {
        SubCategory subCategory = subCategoryRepository.findById(subCategoryId).orElseThrow(() ->
                new Exception("Subcategory with specified id is not found"));
        Room room = createRoom(name, subCategory);
        log.info("Room was created");
        return room;
    }

    /**
     * Deletes a room by its ID.
     *
     * @param  roomId   the ID of the room to be deleted
     * @throws Exception  if the room with the specified ID is not found
     */
    public void deleteRoom(Long roomId) throws Exception {
        Room foundRoom = roomRepository.findById(roomId).orElseThrow(() ->
                new Exception("Room with specified id not found"));
        deleteRoom(foundRoom);
        log.info("Room was removed");
    }

    /**
     * Updates a room with the specified ID and name.
     *
     * @param  roomId   the ID of the room to update
     * @param  name     the new name for the room
     * @return          the updated room
     */
    public Room updateRoom(Long roomId, String name) throws Exception {
        Room foundRoom = roomRepository.findById(roomId).orElseThrow(() ->
                new Exception("Room with specified id not found"));
        Room updatedRoom = updateRoom(foundRoom, name);
        log.info("Room was updated");
        return updatedRoom;
    }

    /**
     * Assigns another subcategory to a room.
     *
     * @param  roomId         the ID of the room
     * @param  subCategoryId  the ID of the subcategory
     * @throws Exception      if subcategory or room is not found
     */
    public void assignAnotherSubCategoryToRoom(Long roomId, Long subCategoryId) throws Exception {
        Pair<Room, SubCategory> pair = findRoomAndSubCategory(roomId, subCategoryId);
        assignAnotherSubCategoryToRoom(pair.getFirst(), pair.getSecond());
        log.info("Another subcategory was assigned to room");
    }

    /**
     * Removes a subcategory from a room.
     *
     * @param  roomId         the ID of the room
     * @param  subCategoryId  the ID of the subcategory to be removed
     * @throws Exception      if room or subcategory not found by the id
     */
    public void removeSubCategoryFromRoom(Long roomId, Long subCategoryId) throws Exception {
        Pair<Room, SubCategory> pair = findRoomAndSubCategory(roomId, subCategoryId);
        removeSubCategoryFromRoom(pair.getFirst(), pair.getSecond());
        log.info("Subcategory was removed from room");
    }

    /**
     * Assigns another user to a room.
     *
     * @param  roomId  the ID of the room
     * @param  email   the email of the user to be assigned
     * @throws Exception  if an error occurs during the assignment process
     */
    public void assignAnotherUserToRoom(Long roomId, String email) throws Exception {
        Pair<Room, User> pair = findRoomAndUser(roomId, email);
        assignUserToRoom(pair.getFirst(), pair.getSecond());
        log.info("Another user was assigned to room");
    }

    /**
     * Removes a user from the room using the specified email and logs result.
     *
     * @param  email  the email of the user to be removed from the room
     * @throws Exception if no user is found with the specified email
     */
    public void removeUserFromRoom(String email) throws Exception {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new Exception("No user is found with specified email"));
        removeUserFromRoom(user);
        log.info("User removed from room");
    }

    /**
     * Gets all rooms including the one assigned to the specified user.
     *
     * @param  email    the email of the user
     * @return         an array containing the room assigned to the user and a list of all rooms
     * @throws Exception      if user not found by the email
     */
    public Object[] getAllRoomsIncludeUserAssigned(String email) throws Exception {
        User currentUser = (userRepository.findByEmail(email).orElseThrow(()
                -> new Exception("User with specified email not found")));

        Room userIsAssignedInRoom = roomRepository.findByUserId(currentUser.getId()).orElse(null);
        List<Room> roomList = roomRepository.findAll();
        List<RoomSimplified> roomSimplifiedList = new ArrayList<>();
        for (Room room : roomList) {
            roomSimplifiedList.add(new RoomSimplified(room));
        }
        Object[] result = new Object[2];
        if (userIsAssignedInRoom == null) {
            result[0] = null;
        } else {
            result[0] = new RoomSimplified(userIsAssignedInRoom);
        }
        result[1] = roomSimplifiedList;
        log.info("Prepared all rooms and user included room");
        return result;
    }

    /**
     * Saves new room, based on the specified name and subcategory
     *
     * @param  name       name of the room
     * @param  subCategory  subcategory of the room
     * @return          saved room with id
     */
    private @NotNull Room createRoom(String name, SubCategory subCategory) {
        Room newRoom = new Room(name, subCategory);
        return roomRepository.save(newRoom);
    }

    /**
     * Deletes a room and its associated reservations from the database.
     *
     * @param  room  the room to be deleted
     */
    private void deleteRoom(Room room) {

        List<Reservation> reservations = reservationRepository.findByAssignedToRoom(room);
        for (Reservation reservation : reservations) {
            reservationRepository.delete(reservation);
        }

        roomRepository.delete(room);
    }

    /**
     * Finds a room and subcategory based on the specified room ID and subcategory id.
     *
     * @param  roomId          the ID of the room to search for
     * @param  subCategoryId   the ID of the subcategory to search for
     * @return                a Pair containing the found room and subcategory
     * @throws Exception      if room or subcategory not found by the id
     */
    private @NotNull Pair<Room, SubCategory> findRoomAndSubCategory(Long roomId, Long subCategoryId) throws Exception {
        Room foundRoom = roomRepository.findById(roomId).orElseThrow(()->
                new Exception("No room with specified id found"));
        SubCategory foundSubCategory = subCategoryRepository.findById(subCategoryId).orElseThrow(() ->
                new Exception("No subcategory with specified id found"));

        return Pair.of(foundRoom, foundSubCategory);
    }


    /**
     * Finds a room and user based on the specified room ID and user email.
     *
     * @param  roomId    the ID of the room to find
     * @param  email     the email of the user to find
     * @return          a Pair containing the found room and user
     * @throws Exception      if room or user not found by the id and email
     */
    private @NotNull Pair<Room, User> findRoomAndUser(Long roomId, String email) throws Exception {
        Room foundRoom = roomRepository.findById(roomId).orElseThrow(()->
                new Exception("No room with specified id found"));
        User foundUser = userRepository.findByEmail(email).orElseThrow(() ->
                new Exception("No user with specified email found"));
        return Pair.of(foundRoom, foundUser);
    }

    /**
     * Removes a subcategory from the given room.
     *
     * @param  room               the room from which the subcategory is to be removed
     * @param  subCategoryToRemove the subcategory to be removed from the room
     */
    private void removeSubCategoryFromRoom(@NotNull Room room, SubCategory subCategoryToRemove) {
        List<SubCategory> subCategoryList = room.getSubcategoriesAssigned();
        subCategoryList.remove(subCategoryToRemove);
        room.setSubcategoriesAssigned(subCategoryList);
        roomRepository.save(room);
    }

    /**
     * Assigns another subcategory to a room.
     *
     * @param  room         the room to assign the subcategory to
     * @param  subCategory  the subcategory to assign to the room
     */
    private void assignAnotherSubCategoryToRoom(@NotNull Room room, SubCategory subCategory) {
        List<SubCategory> subCategoryList = room.getSubcategoriesAssigned();
        subCategoryList.add(subCategory);
        room.setSubcategoriesAssigned(subCategoryList);
        roomRepository.save(room);
    }

    /**
     * Assigns a user to a room.
     *
     * @param  room  the room to which the user is assigned
     * @param  user  the user to be assigned to the room
     */
    private void assignUserToRoom(@NotNull Room room, User user) {
        List<User> userList = room.getUsersAssigned();
        userList.add(user);
        room.setUsersAssigned(userList);
        roomRepository.save(room);
    }

    /**
     * Removes the specified user from the room.
     *
     * @param  user  the user to be removed from the room
     * @throws Exception  if the user is not present in any room
     */
    private void removeUserFromRoom(User user) throws Exception {
        Room room = roomRepository.findByUserId(user.getId()).orElseThrow(() ->
                new Exception("User is not present in any room"));
        List<User> userList = room.getUsersAssigned();
        userList.remove(user);
        room.setUsersAssigned(userList);
        roomRepository.save(room);
    }

    /**
     * Updates the room with the new name.
     *
     * @param  room  the room to be updated
     * @param  name  the new name for the room
     * @return       the updated room
     */
    private Room updateRoom(Room room, String name) {
        room.setName(name);
        return roomRepository.save(room);
    }
}
