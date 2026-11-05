package com.employeemanagement.backend.service;

import com.employeemanagement.backend.dto.DepartmentRequestDTO;
import com.employeemanagement.backend.dto.DepartmentResponseDTO;
import com.employeemanagement.backend.entity.Department;
import com.employeemanagement.backend.exception.DepartmentAlreadyExistsException;
import com.employeemanagement.backend.exception.DepartmentNotFoundException;
import com.employeemanagement.backend.mapper.DepartmentMapper;
import com.employeemanagement.backend.repository.DepartmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DepartmentServiceTest {
    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private DepartmentMapper departmentMapper;

    @InjectMocks
    private DepartmentService departmentService;

    @Test
    void getAllDepartments_shouldReturnAllDepartments() {

        // Given
        Department department = new Department();
        department.setId(1L);
        department.setName("IT");
        department.setDescription("IT Department");

        DepartmentResponseDTO responseDTO = new DepartmentResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setName("IT");
        responseDTO.setDescription("IT Department");

        when(departmentRepository.findAll())
                .thenReturn(List.of(department));

        when(departmentMapper.toResponseDTO(department))
                .thenReturn(responseDTO);

        // When
        List<DepartmentResponseDTO> result =
                departmentService.getAllDepartments();

        // Then
        assertEquals(1, result.size());
        assertEquals("IT", result.get(0).getName());

        verify(departmentRepository).findAll();
        verify(departmentMapper).toResponseDTO(department);
    }

    @Test
    void getAllDepartments_shouldReturnEmptyList() {

        // Given
        when(departmentRepository.findAll())
                .thenReturn(List.of());

        // When
        List<DepartmentResponseDTO> result =
                departmentService.getAllDepartments();

        // Then
        assertTrue(result.isEmpty());

        verify(departmentRepository).findAll();
        verify(departmentMapper, never()).toResponseDTO(any());
    }

    @Test
    void createDepartment_shouldSaveAndReturnDepartment() {

        // Given
        DepartmentRequestDTO requestDTO = new DepartmentRequestDTO();
        requestDTO.setName("IT");
        requestDTO.setDescription("IT Department");

        Department department = new Department();
        department.setName("IT");
        department.setDescription("IT Department");

        Department savedDepartment = new Department();
        savedDepartment.setId(1L);
        savedDepartment.setName("IT");
        savedDepartment.setDescription("IT Department");

        DepartmentResponseDTO responseDTO = new DepartmentResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setName("IT");
        responseDTO.setDescription("IT Department");

        when(departmentRepository.existsByName("IT")).thenReturn(false);
        when(departmentMapper.toEntity(requestDTO)).thenReturn(department);
        when(departmentRepository.save(department)).thenReturn(savedDepartment);
        when(departmentMapper.toResponseDTO(savedDepartment)).thenReturn(responseDTO);

        // When
        DepartmentResponseDTO result = departmentService.createDepartment(requestDTO);

        // Then
        assertEquals(1L, result.getId());
        assertEquals("IT", result.getName());
        assertEquals("IT Department", result.getDescription());
        verify(departmentRepository).existsByName("IT");
        verify(departmentMapper).toEntity(requestDTO);
        verify(departmentRepository).save(department);
        verify(departmentMapper).toResponseDTO(savedDepartment);
    }

    @Test
    void createDepartment_shouldThrowWhenDepartmentAlreadyExists() {

        // Given
        DepartmentRequestDTO requestDTO = new DepartmentRequestDTO();
        requestDTO.setName("IT");
        when(departmentRepository.existsByName("IT")).thenReturn(true);

        // When
        RuntimeException exception = assertThrows(
                DepartmentAlreadyExistsException.class,
                () -> departmentService.createDepartment(requestDTO)
        );

        // Then
        assertEquals("Department already exists with name: "+requestDTO.getName(), exception.getMessage());
        verify(departmentRepository).existsByName("IT");
        verify(departmentMapper, never()).toEntity(any());
        verify(departmentRepository, never()).save(any());
    }

    @Test
    void updateDepartment_shouldSaveAndReturnUpdatedDepartment() {

        // Given
        Long departmentId = 1L;
        DepartmentRequestDTO requestDTO = new DepartmentRequestDTO();
        requestDTO.setName("Engineering");
        requestDTO.setDescription("Engineering Department");

        Department department = new Department();
        department.setId(departmentId);
        department.setName("IT");
        department.setDescription("IT Department");

        DepartmentResponseDTO responseDTO = new DepartmentResponseDTO();
        responseDTO.setId(departmentId);
        responseDTO.setName("Engineering");
        responseDTO.setDescription("Engineering Department");

        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(department));
        when(departmentRepository.save(department)).thenReturn(department);
        when(departmentMapper.toResponseDTO(department)).thenReturn(responseDTO);

        // When
        DepartmentResponseDTO result = departmentService.updateDepartment(departmentId, requestDTO);

        // Then
        assertEquals("Engineering", department.getName());
        assertEquals("Engineering Department", department.getDescription());
        assertEquals(departmentId, result.getId());
        assertEquals("Engineering", result.getName());
        verify(departmentRepository).findById(departmentId);
        verify(departmentRepository).save(department);
        verify(departmentMapper).toResponseDTO(department);
    }

    @Test
    void updateDepartment_shouldThrowWhenDepartmentDoesNotExist() {

        // Given
        Long departmentId = 1L;
        DepartmentRequestDTO requestDTO = new DepartmentRequestDTO();
        requestDTO.setName("Engineering");
        requestDTO.setDescription("Engineering Department");
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.empty());

        // When
        RuntimeException exception = assertThrows(
                DepartmentNotFoundException.class,
                () -> departmentService.updateDepartment(departmentId, requestDTO)
        );

        // Then
        assertEquals("Department not found with id: 1", exception.getMessage());
        verify(departmentRepository).findById(departmentId);
        verify(departmentRepository, never()).save(any());
        verify(departmentMapper, never()).toResponseDTO(any());
    }

    @Test
    void deleteDepartment_shouldDeleteExistingDepartment() {

        // Given
        Long departmentId = 1L;
        Department department = new Department();
        department.setId(departmentId);
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(department));

        // When
        departmentService.deleteDepartment(departmentId);

        // Then
        verify(departmentRepository).findById(departmentId);
        verify(departmentRepository).delete(department);
    }

    @Test
    void deleteDepartment_shouldThrowWhenDepartmentDoesNotExist() {

        // Given
        Long departmentId = 1L;
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.empty());

        // When
        RuntimeException exception = assertThrows(
                DepartmentNotFoundException.class,
                () -> departmentService.deleteDepartment(departmentId)
        );

        // Then
        assertEquals("Department not found with id: 1", exception.getMessage());
        verify(departmentRepository).findById(departmentId);
        verify(departmentRepository, never()).delete(any());
    }

    @Test
    void getDepartmentById_shouldReturnDepartmentWhenItExists() {

        // Given
        Long departmentId = 1L;
        Department department = new Department();
        department.setId(departmentId);
        department.setName("IT");
        department.setDescription("IT Department");

        DepartmentResponseDTO responseDTO = new DepartmentResponseDTO();
        responseDTO.setId(departmentId);
        responseDTO.setName("IT");
        responseDTO.setDescription("IT Department");

        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(department));
        when(departmentMapper.toResponseDTO(department)).thenReturn(responseDTO);

        // When
        DepartmentResponseDTO result = departmentService.getDepartmentById(departmentId);

        // Then
        assertEquals(departmentId, result.getId());
        assertEquals("IT", result.getName());
        assertEquals("IT Department", result.getDescription());
        verify(departmentRepository).findById(departmentId);
        verify(departmentMapper).toResponseDTO(department);
    }

    @Test
    void getDepartmentById_shouldThrowWhenDepartmentDoesNotExist() {

        // Given
        Long departmentId = 1L;
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.empty());

        // When
        RuntimeException exception = assertThrows(
                DepartmentNotFoundException.class,
                () -> departmentService.getDepartmentById(departmentId)
        );

        // Then
        assertEquals("Department not found with id: 1", exception.getMessage());
        verify(departmentRepository).findById(departmentId);
        verify(departmentMapper, never()).toResponseDTO(any());
    }
}
