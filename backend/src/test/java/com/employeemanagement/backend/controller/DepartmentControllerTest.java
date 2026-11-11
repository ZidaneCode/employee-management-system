package com.employeemanagement.backend.controller;

import com.employeemanagement.backend.dto.DepartmentRequestDTO;
import com.employeemanagement.backend.dto.DepartmentResponseDTO;
import com.employeemanagement.backend.service.DepartmentService;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DepartmentController.class)
@AutoConfigureMockMvc(addFilters = false)
class DepartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DepartmentService departmentService;

    @Test
    void getAllDepartments_shouldReturnDepartmentList() throws Exception {
        // Given
        when(departmentService.getAllDepartments()).thenReturn(List.of(
                createResponseDTO(1L, "Engineering", "Engineering team"),
                createResponseDTO(2L, "Human Resources", "People operations")
        ));

        // When / Then
        mockMvc.perform(get("/api/departments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Engineering"))
                .andExpect(jsonPath("$[0].description").value("Engineering team"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Human Resources"))
                .andExpect(jsonPath("$[1].description").value("People operations"));

        verify(departmentService).getAllDepartments();
    }

    @Test
    void getDepartmentById_shouldReturnDepartment() throws Exception {
        // Given
        Long departmentId = 1L;
        when(departmentService.getDepartmentById(departmentId))
                .thenReturn(createResponseDTO(departmentId, "Engineering", "Engineering team"));

        // When / Then
        mockMvc.perform(get("/api/departments/{id}", departmentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Engineering"))
                .andExpect(jsonPath("$.description").value("Engineering team"));

        verify(departmentService).getDepartmentById(departmentId);
    }

    @Test
    void createDepartment_shouldReturnCreatedDepartment() throws Exception {
        // Given
        DepartmentRequestDTO requestDTO = createRequestDTO("Engineering", "Engineering team");
        when(departmentService.createDepartment(any(DepartmentRequestDTO.class)))
                .thenReturn(createResponseDTO(1L, "Engineering", "Engineering team"));

        // When / Then
        mockMvc.perform(post("/api/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Engineering"))
                .andExpect(jsonPath("$.description").value("Engineering team"));

        verify(departmentService).createDepartment(argThat(dto ->
                "Engineering".equals(dto.getName())
                        && "Engineering team".equals(dto.getDescription())
        ));
    }

    @Test
    void createDepartment_shouldReturnBadRequestWhenNameIsBlank() throws Exception {
        // Given
        DepartmentRequestDTO requestDTO = createRequestDTO("  ", "Engineering team");

        // When / Then
        mockMvc.perform(post("/api/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());

        verify(departmentService, never()).createDepartment(any(DepartmentRequestDTO.class));
    }

    @Test
    void createDepartment_shouldReturnBadRequestWhenNameExceedsMaxLength() throws Exception {
        // Given
        DepartmentRequestDTO requestDTO = createRequestDTO("D".repeat(101), "Engineering team");

        // When / Then
        mockMvc.perform(post("/api/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());

        verify(departmentService, never()).createDepartment(any(DepartmentRequestDTO.class));
    }

    @Test
    void updateDepartment_shouldReturnUpdatedDepartment() throws Exception {
        // Given
        Long departmentId = 1L;
        DepartmentRequestDTO requestDTO = createRequestDTO("Research", "Research and development");
        when(departmentService.updateDepartment(any(Long.class), any(DepartmentRequestDTO.class)))
                .thenReturn(createResponseDTO(departmentId, "Research", "Research and development"));

        // When / Then
        mockMvc.perform(put("/api/departments/{id}", departmentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Research"))
                .andExpect(jsonPath("$.description").value("Research and development"));

        verify(departmentService).updateDepartment(
                org.mockito.ArgumentMatchers.eq(departmentId),
                argThat(dto -> "Research".equals(dto.getName())
                        && "Research and development".equals(dto.getDescription()))
        );
    }

    @Test
    void deleteDepartment_shouldReturnNoContent() throws Exception {
        // Given
        Long departmentId = 1L;

        // When / Then
        mockMvc.perform(delete("/api/departments/{id}", departmentId))
                .andExpect(status().isNoContent());

        verify(departmentService).deleteDepartment(departmentId);
    }

    private static DepartmentRequestDTO createRequestDTO(String name, String description) {
        DepartmentRequestDTO dto = new DepartmentRequestDTO();
        dto.setName(name);
        dto.setDescription(description);
        return dto;
    }

    private static DepartmentResponseDTO createResponseDTO(Long id, String name, String description) {
        DepartmentResponseDTO dto = new DepartmentResponseDTO();
        dto.setId(id);
        dto.setName(name);
        dto.setDescription(description);
        return dto;
    }
}
