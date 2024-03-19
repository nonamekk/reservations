package box.reservations;

import box.reservations.entities.*;
import box.reservations.payload.requests.auth.LoginRequest;
import box.reservations.payload.requests.auth.RegistrationRequest;
import box.reservations.payload.requests.auth.ticket.NewTicketCreationRequest;
import box.reservations.payload.requests.room.RoomIdRequest;
import box.reservations.payload.requests.room.RoomIdSubCategoryIdRequest;
import box.reservations.payload.responses.room.AllRoomsResponse;
import box.reservations.payload.responses.room.RoomSimplified;
import box.reservations.services.UserControlService;
import box.reservations.services.admin.AdminControlsService;
import box.reservations.services.auth.RegistrationService;
import box.reservations.services.employee.EmployeeControlsCategoryService;
import box.reservations.services.employee.EmployeeControlsSubcategoryService;
import box.reservations.services.employee.EmployeeRoomService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ReservationsRoomTests {

    private static Room roomCreated;
    private static SubCategory subCategoryCreated;
    private static Category categoryCreated;
    private static User userCreated;
    private static String userPassword;

    @Autowired
    AdminControlsService adminControlsService;

    @Autowired
    RegistrationService registrationService;

    @Autowired
    EmployeeControlsCategoryService employeeControlsCategoryService;

    @Autowired
    EmployeeControlsSubcategoryService employeeControlsSubcategoryService;
    @Autowired
    EmployeeRoomService employeeRoomService;
    @Autowired
    UserControlService userControlService;

    @BeforeAll
    public void setup() throws Exception {
        // create ticket
        Ticket ticket = adminControlsService.createTicket(new NewTicketCreationRequest(Role.EMPLOYEE, 1));
        // create user employee
        String userEmail = UUID.randomUUID().toString() + "@email.com";
        userPassword = "password";
        userCreated = registrationService.addUser(new RegistrationRequest(userEmail, userPassword, ticket.getSecret()));

        // create category
        String categoryName = UUID.randomUUID().toString();
        categoryCreated = employeeControlsCategoryService.createCategory(categoryName);

        // create subcategory
        String subCategoryName = UUID.randomUUID().toString();
        subCategoryCreated = employeeControlsSubcategoryService.createSubCategory(
                categoryCreated.getId(),
                subCategoryName
        );

        // create room
        String roomName = UUID.randomUUID().toString();
        roomCreated = employeeRoomService.createRoom(roomName, subCategoryCreated.getId());
    }

    @AfterAll
    public void teardown() throws Exception {
        employeeControlsCategoryService.deleteCategory(categoryCreated.getId());
        employeeRoomService.deleteRoom(roomCreated.getId());
        userControlService.selfDeleteUser(userCreated.getEmail());
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String getJwtCookie(String email, String password) throws Exception {
        LoginRequest loginRequest = new LoginRequest(email, password);
        String requestBody = objectMapper.writeValueAsString(loginRequest);

        return Objects.requireNonNull(mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                        ).andExpect(MockMvcResultMatchers.status().isOk())
                        .andReturn()
                        .getResponse()
                        .getCookie("__Host-auth-token"))
                .getValue();
    }

    private AllRoomsResponse obtainRoomInfo() throws Exception {
        String jwtCookie = getJwtCookie(userCreated.getEmail(), userPassword);
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/employee/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(new Cookie("__Host-auth-token", jwtCookie))
        );

        MvcResult mvcResult = resultActions.andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        AllRoomsResponse response = objectMapper.readValue(jsonResponse, AllRoomsResponse.class);
        return response;
    }

    private SubCategory addSubCategoryToRoom() throws Exception {
        String jwtCookie = getJwtCookie(userCreated.getEmail(), userPassword);
        String subCategoryName = UUID.randomUUID().toString();
        SubCategory newSubcategory = employeeControlsSubcategoryService.createSubCategory(
                categoryCreated.getId(), subCategoryName);


        RoomIdSubCategoryIdRequest roomIdSubCategoryIdRequest = new RoomIdSubCategoryIdRequest(
                roomCreated.getId(), newSubcategory.getId()
        );
        String requestBody = objectMapper.writeValueAsString(roomIdSubCategoryIdRequest);


        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/employee/controls/room/subcategory")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(new Cookie("__Host-auth-token", jwtCookie))
                .content(requestBody)
        )
                .andExpect(MockMvcResultMatchers.status().isOk());
        return newSubcategory;
    }

    private void addSelfToRoom() throws Exception {
        String jwtCookie = getJwtCookie(userCreated.getEmail(), userPassword);
        RoomIdRequest request = new RoomIdRequest((roomCreated.getId()));
        String requestBody = objectMapper.writeValueAsString(request);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/employee/controls/room/user")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(new Cookie("__Host-auth-token", jwtCookie))
                .content(requestBody)
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }

    private void removeSelfFromRoom() throws Exception {
        String jwtCookie = getJwtCookie(userCreated.getEmail(), userPassword);

        mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/v1/employee/controls/room/user")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(new Cookie("__Host-auth-token", jwtCookie))
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }

    private @Nullable List<SubCategory> getTestedRoomSubCategories(AllRoomsResponse allRooms) {
        for (RoomSimplified roomDescription: allRooms.getRooms()) {
            if (Objects.equals(roomDescription.getName(), roomCreated.getName())) {
                return roomDescription.getSubcategories();
            }
        }
        return null;
    }

    private RoomSimplified getTestedRoom(AllRoomsResponse allRooms) {
        for (RoomSimplified room : allRooms.getRooms()) {
            if (room.getName().equals(roomCreated.getName())) {
                return room;
            }
        }
        return null;
    }


    @Test
    public void testRoomDefault() throws Exception {
        AllRoomsResponse allRoomsResponse = obtainRoomInfo();
        assert(allRoomsResponse.getUser_assigned_in_room() == null);
    }

    @Test
    public void testRoomSubCategoriesManipulations() throws Exception {
        AllRoomsResponse allRoomsResponse = obtainRoomInfo();
        List<SubCategory> subCategoryList = getTestedRoomSubCategories(allRoomsResponse);
        assert(subCategoryList.size() == 1);
        SubCategory newSubCategory = addSubCategoryToRoom();

        allRoomsResponse = obtainRoomInfo();
        subCategoryList = getTestedRoomSubCategories(allRoomsResponse);
        assert(subCategoryList.size() == 2);

        employeeControlsSubcategoryService.deleteSubCategory(newSubCategory.getId());

        allRoomsResponse = obtainRoomInfo();
        subCategoryList = getTestedRoomSubCategories(allRoomsResponse);
        assert(subCategoryList.size() == 1);
    }

    @Test
    public void testRoomSelfAssignToRoomManipulations() throws Exception {
        AllRoomsResponse allRoomsResponse = obtainRoomInfo();
        assert(allRoomsResponse.getUser_assigned_in_room() == null);
        addSelfToRoom();

        allRoomsResponse = obtainRoomInfo();
        assert(allRoomsResponse.getUser_assigned_in_room().getName().equals(roomCreated.getName()));
        assert(allRoomsResponse.getUser_assigned_in_room().getOccupied_by() == 1);

        removeSelfFromRoom();
        allRoomsResponse = obtainRoomInfo();
        assert(allRoomsResponse.getUser_assigned_in_room() == null);
    }
}
