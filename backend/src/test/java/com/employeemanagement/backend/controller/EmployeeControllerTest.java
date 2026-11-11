package com.employeemanagement.backend.controller;

import com.employeemanagement.backend.dto.EmployeeRequestDTO;
import com.employeemanagement.backend.dto.EmployeeResponseDTO;
import com.employeemanagement.backend.entity.EmployeeStatus;
import com.employeemanagement.backend.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeController.class)
@AutoConfigureMockMvc(addFilters = false)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EmployeeService employeeService;

    @Test
    void getAllEmployees_shouldReturnEmployees() throws Exception {
        // Given
        when(employeeService.getAllEmployees()).thenReturn(List.of(
                createResponseDTO(1L, "John", "Doe", "john.doe@example.com"),
                createResponseDTO(2L, "Jane", "Smith", "jane.smith@example.com")
        ));

        // When / Then
        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"))
                .andExpect(jsonPath("$[0].email").value("john.doe@example.com"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].firstName").value("Jane"))
                .andExpect(jsonPath("$[1].lastName").value("Smith"))
                .andExpect(jsonPath("$[1].email").value("jane.smith@example.com"));

        verify(employeeService).getAllEmployees();
    }

    @Test
    void getEmployeeById_shouldReturnEmployee() throws Exception {
        // Given
        Long employeeId = 1L;
        when(employeeService.getEmployeeById(employeeId))
                .thenReturn(createResponseDTO(employeeId, "John", "Doe", "john.doe@example.com"));

        // When / Then
        mockMvc.perform(get("/api/employees/{id}", employeeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.phone").value("555-0100"))
                .andExpect(jsonPath("$.hireDate").value("2024-01-15"))
                .andExpect(jsonPath("$.salary").value(65000.00))
                .andExpect(jsonPath("$.position").value("Developer"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.departmentId").value(10))
                .andExpect(jsonPath("$.departmentName").value("Engineering"));

        verify(employeeService).getEmployeeById(eq(employeeId));
    }

    @Test
    void createEmployee_shouldReturnCreatedEmployee() throws Exception {
        // Given
        EmployeeRequestDTO requestDTO = createRequestDTO("John", "Doe", "john.doe@example.com");
        when(employeeService.createEmployee(any(EmployeeRequestDTO.class)))
                .thenReturn(createResponseDTO(1L, "John", "Doe", "john.doe@example.com"));

        // When / Then
        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.departmentId").value(10));

        verify(employeeService).createEmployee(any(EmployeeRequestDTO.class));
    }

    @Test
    void createEmployee_shouldReturnBadRequestWhenInvalidData() throws Exception {
        // Given
        EmployeeRequestDTO requestDTO = createRequestDTO("", "Doe", "john.doe@example.com");

        // When / Then
        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());

        verify(employeeService, never()).createEmployee(any(EmployeeRequestDTO.class));
    }

    @Test
    void updateEmployee_shouldReturnUpdatedEmployee() throws Exception {
        // Given
        Long employeeId = 1L;
        EmployeeRequestDTO requestDTO = createRequestDTO("Jane", "Doe", "jane.doe@example.com");
        when(employeeService.updateEmployee(eq(employeeId), any(EmployeeRequestDTO.class)))
                .thenReturn(createResponseDTO(employeeId, "Jane", "Doe", "jane.doe@example.com"));

        // When / Then
        mockMvc.perform(put("/api/employees/{id}", employeeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("jane.doe@example.com"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.departmentId").value(10));

        verify(employeeService).updateEmployee(eq(employeeId), any(EmployeeRequestDTO.class));
    }

    @Test
    void updateEmployee_shouldReturnBadRequestWhenInvalidData() throws Exception {
        // Given
        Long employeeId = 1L;
        EmployeeRequestDTO requestDTO = createRequestDTO("", "Doe", "john.doe@example.com");

        // When / Then
        mockMvc.perform(put("/api/employees/{id}", employeeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());

        verify(employeeService, never()).updateEmployee(
                eq(employeeId),
                any(EmployeeRequestDTO.class)
        );
    }

    @Test
    void deleteEmployee_shouldReturnNoContent() throws Exception {
        // Given
        Long employeeId = 1L;

        // When / Then
        mockMvc.perform(delete("/api/employees/{id}", employeeId))
                .andExpect(status().isNoContent());

        verify(employeeService).deleteEmployee(eq(employeeId));
    }

    private static EmployeeRequestDTO createRequestDTO(String firstName, String lastName, String email) {
        EmployeeRequestDTO dto = new EmployeeRequestDTO();
        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        dto.setEmail(email);
        dto.setPhone("555-0100");
        dto.setHireDate(LocalDate.of(2024, 1, 15));
        dto.setSalary(new BigDecimal("65000.00"));
        dto.setPosition("Developer");
        dto.setStatus(EmployeeStatus.ACTIVE);
        dto.setDepartmentId(10L);
        return dto;
    }

    private static EmployeeResponseDTO createResponseDTO(
            Long id,
            String firstName,
            String lastName,
            String email
    ) {
        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setId(id);
        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        dto.setEmail(email);
        dto.setPhone("555-0100");
        dto.setHireDate(LocalDate.of(2024, 1, 15));
        dto.setSalary(new BigDecimal("65000.00"));
        dto.setPosition("Developer");
        dto.setStatus(EmployeeStatus.ACTIVE);
        dto.setDepartmentId(10L);
        dto.setDepartmentName("Engineering");
        return dto;
    }
}
