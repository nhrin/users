package com.example.users;

import com.example.users.controller.UserController;
import com.example.users.model.User;
import com.example.users.model.UserRequest;
import com.example.users.service.UserService;
import com.example.users.util.Util;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@ExtendWith({SpringExtension.class, MockitoExtension.class})
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    public void testCreateUserValidUserReturnsCreated() throws Exception {
        UserRequest userRequest = new UserRequest();
        userRequest.setFirstName("John");
        userRequest.setLastName("Doe");
        userRequest.setBirthDate("1990-01-01");
        userRequest.setEmail("john.doe@example.com");

        when(userService.getMinBirthDate())
                .thenReturn(LocalDate.of(2002, 1, 1));
        mockMvc.perform(post("/users/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    public void testCreateUserBadRequest() throws Exception {
        // Birth date after current date
        UserRequest userRequest = new UserRequest();
        userRequest.setFirstName("John");
        userRequest.setLastName("Doe");
        String birthDateAfterCurrentDay = LocalDate.now().plusDays(1).toString();
        userRequest.setBirthDate(birthDateAfterCurrentDay);
        userRequest.setEmail("john.doe@example.com");

        when(userService.getMinAge()).thenReturn(18);
        when(userService.getMinBirthDate())
                .thenReturn(LocalDate.of(2002, 1, 1));
        mockMvc.perform(post("/users/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userRequest)))
                .andExpect(status().isBadRequest());

        // Birth date below minimum age
        String birthDateBellowMinimumAge = LocalDate.now().minusYears(16).toString();
        userRequest.setBirthDate(birthDateBellowMinimumAge);
        mockMvc.perform(post("/users/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userRequest)))
                .andExpect(status().isBadRequest());
    }


    @Test
    public void testUpdateUserFieldsValidUserReturnsOk() throws Exception {

        UserRequest userRequest = new UserRequest();
        userRequest.setFirstName("John");
        userRequest.setLastName("Doe");
        userRequest.setBirthDate("1990-01-01");
        userRequest.setEmail("john.doe@example.com");

        UserRequest userUpdatedRequest = new UserRequest();
        userRequest.setFirstName("Jane");

        List<User> users = new ArrayList<>();
        users.add(Util.convertToUser(userRequest));
        when(userService.getUsers()).thenReturn(users);

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/update/0")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userUpdatedRequest)))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateAllUserFieldsValidUserReturnsOk() throws Exception {
        UserRequest userRequest = new UserRequest();
        userRequest.setFirstName("John");
        userRequest.setLastName("Doe");
        userRequest.setBirthDate("1990-01-01");
        userRequest.setEmail("john.doe@example.com");

        List<User> users = new ArrayList<>();
        users.add(Util.convertToUser(userRequest));
        when(userService.getUsers()).thenReturn(users);

        UserRequest userUpdatedRequest = new UserRequest();
        userUpdatedRequest.setFirstName("John_Updated");
        userUpdatedRequest.setLastName("Doe_Updated");
        userUpdatedRequest.setBirthDate("1990-01-02");
        userUpdatedRequest.setEmail("john.doe_Updated@example.com");

        mockMvc.perform(MockMvcRequestBuilders.put("/users/update-all/0")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userUpdatedRequest)))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteUserExistingUserReturnsOk() throws Exception {
        UserRequest userRequest = new UserRequest();
        userRequest.setFirstName("John");
        userRequest.setLastName("Doe");
        userRequest.setBirthDate("1990-01-01");
        userRequest.setEmail("john.doe@example.com");

        List<User> users = new ArrayList<>();
        users.add(Util.convertToUser(userRequest));
        when(userService.getUsers()).thenReturn(users);

        mockMvc.perform(MockMvcRequestBuilders
                .delete("/users/delete/0"))
                .andExpect(status().isOk());
    }

    @Test
    public void searchUsersByBirthDateRange_ValidRange_ReturnsOk() throws Exception {
        LocalDate fromDate = LocalDate.of(1990, 1, 1);
        LocalDate toDate = LocalDate.of(2000, 12, 31);

        mockMvc.perform(MockMvcRequestBuilders.get("/users/search")
                .param("from", fromDate.toString())
                .param("to", toDate.toString()))
                .andExpect(status().isOk());
    }

    // Utility method to convert objects to JSON
    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
