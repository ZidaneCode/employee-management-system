package com.employeemanagement.backend.exception;

import com.employeemanagement.backend.controller.DepartmentController;
import com.employeemanagement.backend.dto.DepartmentRequestDTO;
import com.employeemanagement.backend.service.DepartmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DepartmentController.class)
@AutoConfigureMockMvc(addFilters = false)
public class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DepartmentService departmentService;

    @Test
    void shouldReturn404WhenDepartmentNotFound() throws Exception {
        // Arrange
        when(departmentService.getDepartmentById(1L))
                .thenThrow(new DepartmentNotFoundException("Department not found with id: 1"));
        // Act & Assert
        mockMvc.perform(get("/api/departments/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Department not found with id: 1"));
    }

    @Test
    void shouldReturn409WhenDepartmentAlreadyExists() throws Exception {
        // Arrange
        when(departmentService.createDepartment(any(DepartmentRequestDTO.class)))
                .thenThrow(new DepartmentAlreadyExistsException("Department already exists with name: IT"));
        // Act & Assert
        mockMvc.perform(post("/api/departments")
                        .contentType("application/json")
                        .content("{\"name\": \"IT\", \"description\": \"Information Technology\"}"))
                .andExpect(status().isConflict())
                .andExpect(content().string("Department already exists with name: IT"));

    }

    @Test
    void shouldReturn404WhenEmployeeNotFound() throws Exception {
        // Arrange
        when(departmentService.getDepartmentById(1L))
                .thenThrow(new EmployeeNotFoundException("Employee not found with id: 1"));
        // Act & Assert
        mockMvc.perform(get("/api/departments/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Employee not found with id: 1"));
    }

    @Test
    void shouldReturn409WhenEmployeeAlreadyExists() throws Exception {
        // Arrange
        when(departmentService.createDepartment(any(DepartmentRequestDTO.class)))
                .thenThrow(new EmployeeAlreadyExistsException("Employee already exists with email: john.doe@example.com"));
        // Act & Assert
        mockMvc.perform(post("/api/departments")
                        .contentType("application/json")
                        .content("{\"name\": \"John Doe\", \"email\": \"john.doe@example.com\"}"))
                .andExpect(status().isConflict())
                .andExpect(content().string("Employee already exists with email: john.doe@example.com"));
    }

    @Test
    void sholdReturn404WhenUserNotFound() throws Exception {
        // Arrange
        when(departmentService.getDepartmentById(1L))
                .thenThrow(new UserNotFoundException("User not found with id: 1"));
        // Act & Assert
        mockMvc.perform(get("/api/departments/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found with id: 1"));
    }

    @Test
    void shouldReturn409WhenUserAlreadyExists() throws Exception {
        // Arrange
        when(departmentService.createDepartment(any(DepartmentRequestDTO.class)))
                .thenThrow(new UserAlreadyExistsException("User already exists with email: john.doe@example.com"));
        // Act & Assert
        mockMvc.perform(post("/api/departments")
                        .contentType("application/json")
                        .content("{\"name\": \"John Doe\", \"email\": \"john.doe@example.com\"}"))
                .andExpect(status().isConflict())
                .andExpect(content().string("User already exists with email: john.doe@example.com"));
    }
}