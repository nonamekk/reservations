package box.reservations;

import box.reservations.entities.*;
import box.reservations.payload.requests.auth.LoginRequest;
import box.reservations.payload.requests.auth.RegistrationRequest;
import box.reservations.payload.requests.auth.ticket.NewTicketCreationRequest;
import box.reservations.payload.requests.reservations.ReservationSubCategoryId;
import box.reservations.payload.requests.room.RoomIdRequest;
import box.reservations.services.UserControlService;
import box.reservations.services.admin.AdminControlsService;
import box.reservations.services.auth.RegistrationService;
import box.reservations.services.client.returns.AllReservationsClientViewAll;
import box.reservations.services.employee.EmployeeControlsCategoryService;
import box.reservations.services.employee.EmployeeControlsSubcategoryService;
import box.reservations.services.employee.EmployeeRoomService;
import box.reservations.services.employee.returns.ReservationsEmployeeAll;
import box.reservations.services.employee.returns.ReservationsEmployeeSimplified;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
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
public class ReservationsTests {

    private static Room roomCreated;
    private static Category categoryCreated;
    private static SubCategory subCategoryCreated;
    private static User userCreated;
    private static String userPassword;

    @Autowired
    AdminControlsService adminControlsService;

    @Autowired
    RegistrationService registrationService;

    @Autowired
    EmployeeRoomService employeeRoomService;

    @Autowired
    EmployeeControlsCategoryService employeeControlsCategoryService;

    @Autowired
    EmployeeControlsSubcategoryService employeeControlsSubcategoryService;

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

        // assign user to room
        addSelfToRoom();
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

    private String getUserUuidAndMakeReservation() throws Exception {
        ReservationSubCategoryId reservationSubCategoryId = new ReservationSubCategoryId(
                subCategoryCreated.getId()
        );
        String requestBody = objectMapper.writeValueAsString(reservationSubCategoryId);

        return Objects.requireNonNull(mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/reservations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                        ).andExpect(MockMvcResultMatchers.status().isOk())
                        .andReturn()
                        .getResponse()
                        .getCookie("__Host-session-token"))
                .getValue();
    }

    private AllReservationsClientViewAll getReservationsClientSide(String clientUuid) throws Exception {

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(new Cookie("__Host-session-token", clientUuid))
        );
        MvcResult mvcResult = resultActions.andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        return objectMapper.readValue(jsonResponse, AllReservationsClientViewAll.class);
    }

    private ReservationsEmployeeAll getReservationEmployeeSide() throws Exception {
        String jwtCookie = getJwtCookie(userCreated.getEmail(), userPassword);
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/employee/controls/reservation")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(new Cookie("__Host-auth-token", jwtCookie))
        );
        MvcResult mvcResult = resultActions.andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        return objectMapper.readValue(jsonResponse, ReservationsEmployeeAll.class);
    }

    private ReservationsEmployeeAll postReservationEmployeeSide() throws Exception {
        String jwtCookie = getJwtCookie(userCreated.getEmail(), userPassword);
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/employee/controls/reservation")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(new Cookie("__Host-auth-token", jwtCookie))
        );
        MvcResult mvcResult = resultActions.andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();
        return objectMapper.readValue(jsonResponse, ReservationsEmployeeAll.class);
    }

    @Test
    public void testReservationBasicManipulation() throws Exception {
        // login as employee, ensure list is empty
        // create reservation
        // ensure list is not empty
        // accept reservation from employee side
        // ensure reservation now is in the active list
        // accept reservation from employee side
        // ensure reservation now is in the completed list

        ReservationsEmployeeAll reservationsEmployeeAll = getReservationEmployeeSide();
        List<ReservationsEmployeeSimplified> active_reservations = reservationsEmployeeAll
                .getActive_reservations();
        List<ReservationsEmployeeSimplified> inactive_reservations = reservationsEmployeeAll
                .getInactive_reservations();

        assert (active_reservations.isEmpty());
        assert (inactive_reservations.isEmpty());

        String clientUuid = getUserUuidAndMakeReservation();

        reservationsEmployeeAll = getReservationEmployeeSide();
        active_reservations = reservationsEmployeeAll
                .getActive_reservations();
        inactive_reservations = reservationsEmployeeAll
                .getInactive_reservations();

        assert (active_reservations.isEmpty());
        assert (!inactive_reservations.isEmpty());
        assert (inactive_reservations.size() == 1);
        assert (Objects.equals(inactive_reservations.getFirst().getSubcategory().getId(), subCategoryCreated.getId()));

        reservationsEmployeeAll = postReservationEmployeeSide();
        active_reservations = reservationsEmployeeAll
                .getActive_reservations();
        inactive_reservations = reservationsEmployeeAll
                .getInactive_reservations();

        assert (!active_reservations.isEmpty());
        assert (active_reservations.size() == 1);
        assert (Objects.equals(active_reservations.getFirst().getSubcategory().getId(), subCategoryCreated.getId()));
        assert (inactive_reservations.isEmpty());

        reservationsEmployeeAll = postReservationEmployeeSide();
        active_reservations = reservationsEmployeeAll
                .getActive_reservations();
        inactive_reservations = reservationsEmployeeAll
                .getInactive_reservations();

        assert (active_reservations.isEmpty());
        assert (inactive_reservations.isEmpty());

        AllReservationsClientViewAll allReservationsClientViewAll = getReservationsClientSide(clientUuid);

        assert (allReservationsClientViewAll.getCompleted_reservations().size() == 1);
        assert (allReservationsClientViewAll.getActive_reservations().isEmpty());
        assert (allReservationsClientViewAll.getInactive_reservations().isEmpty());
    }

}
