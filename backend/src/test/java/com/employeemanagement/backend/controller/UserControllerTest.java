package com.employeemanagement.backend.controller;

import com.employeemanagement.backend.dto.UserRequestDTO;
import com.employeemanagement.backend.dto.UserResponseDTO;
import com.employeemanagement.backend.entity.Role;
import com.employeemanagement.backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;


import java.util.List;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    UserService userService;

    @Test
    void getAllUsers_shouldReturnUsersList()throws Exception {
        //Given
        when(userService.getAllUsers()).thenReturn(List.of(
                createResponseDTO(1L, "John", "Doe"),
                createResponseDTO(2L, "Jane", "Smith")
        ));

        //When
        mockMvc.perform(get("/api/users"))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].firstName").value("Jane"))
                .andExpect(jsonPath("$[1].lastName").value("Smith"));
        verify(userService).getAllUsers();

    }

    @Test
    void getUserById_shouldReturnUser() throws Exception {
        // Given
        Long userId = 1L;
        when(userService.getUserById(userId)).thenReturn(createResponseDTO(userId, "John", "Doe"));
        // When
        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("emailtest@gmail.com"))
                .andExpect(jsonPath("$.role").value("HR"))
                .andExpect(jsonPath("$.enabled").value(true));
        // Then
        verify(userService).getUserById(userId);
    }

    @Test
    void deleteUser_shouldReturnNoContent() throws Exception {
        // Given
        Long userId = 1L;
        doNothing().when(userService).deleteUser(userId);

        // When
        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isNoContent());

        // Then
        verify(userService).deleteUser(userId);
    }

    @Test
    void createUser_shouldReturnCreatedUser() throws Exception {
        // Given
        UserRequestDTO requestDTO = createRequestDTO("John", "Doe");
        when(userService.createUser(any(UserRequestDTO.class)))
                .thenReturn(createResponseDTO(1L, "John", "Doe"));
        // When
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.id").value(1L))
                        .andExpect(jsonPath("$.firstName").value("John"))
                        .andExpect(jsonPath("$.lastName").value("Doe"))
                        .andExpect(jsonPath("$.email").value("emailtest@gmail.com"))
                        .andExpect(jsonPath("$.role").value("HR"))
                        .andExpect(jsonPath("$.enabled").value(true));
        // Then
        verify(userService).createUser(any(UserRequestDTO.class));
    }
    @Test
    void createUser_shouldReturnBadRequestWhenInvalidData() throws Exception {
        // Given
        UserRequestDTO userRequestDTO = createRequestDTO("", "Doe");

        // When
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isBadRequest());

        // Then
        verify(userService, never()).createUser(any(UserRequestDTO.class));
    }

    @Test
    void updateUser_shouldReturnUpdatedUser() throws Exception {
        // Given
        Long userId = 1L;
        UserRequestDTO userRequestDTO = createRequestDTO("Jane", "Doe");

        when(userService.updateUser(eq(userId), any(UserRequestDTO.class)))
                .thenReturn(createResponseDTO(userId, "Jane", "Doe"));

        // When
        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("emailtest@gmail.com"))
                .andExpect(jsonPath("$.role").value("HR"))
                .andExpect(jsonPath("$.enabled").value(true));

        // Then
        verify(userService).updateUser(eq(userId), any(UserRequestDTO.class));
    }
    @Test
    void updateUser_shouldReturnBadRequestWhenInvalidData() throws Exception {
        // Given
        Long userId = 1L;
        UserRequestDTO userRequestDTO = createRequestDTO("", "Doe");

        // When
        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDTO)))
                .andExpect(status().isBadRequest());

        // Then
        verify(userService, never()).updateUser(
                eq(userId),
                any(UserRequestDTO.class)
        );
    }

    public UserRequestDTO createRequestDTO(String firstName, String lastName) {
        UserRequestDTO dto = new UserRequestDTO();
        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        dto.setEmail("emailtest@gmail.com");
        dto.setPassword("password");
        dto.setRole(Role.HR);
        return dto;
    }



    public UserResponseDTO createResponseDTO(Long id,String firstName, String lastName) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(id);
        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        dto.setEmail("emailtest@gmail.com");
        dto.setRole(Role.HR);
        dto.setEnabled(true);
        return dto;
    }
}