package com.employeemanagement.backend.service;

import com.employeemanagement.backend.dto.EmployeeRequestDTO;
import com.employeemanagement.backend.dto.EmployeeResponseDTO;
import com.employeemanagement.backend.entity.Employee;
import com.employeemanagement.backend.exception.EmployeeAlreadyExistsException;
import com.employeemanagement.backend.exception.EmployeeNotFoundException;
import com.employeemanagement.backend.mapper.EmployeeMapper;
import com.employeemanagement.backend.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    void getAllEmployees_shouldReturnEmployeeList() {
        // Given
        Employee employee = new Employee();
        employee.setId(1L);
        EmployeeResponseDTO responseDTO = new EmployeeResponseDTO();
        responseDTO.setId(1L);

        when(employeeRepository.findAll()).thenReturn(List.of(employee));
        when(employeeMapper.toResponseDTO(employee)).thenReturn(responseDTO);

        // When
        List<EmployeeResponseDTO> result = employeeService.getAllEmployees();

        // Then
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        verify(employeeRepository).findAll();
        verify(employeeMapper).toResponseDTO(employee);
    }

    @Test
    void getEmployeeById_shouldReturnExistingEmployee() {
        // Given
        Long employeeId = 1L;
        Employee employee = new Employee();
        employee.setId(employeeId);
        EmployeeResponseDTO responseDTO = new EmployeeResponseDTO();
        responseDTO.setId(employeeId);

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(employeeMapper.toResponseDTO(employee)).thenReturn(responseDTO);

        // When
        EmployeeResponseDTO result = employeeService.getEmployeeById(employeeId);

        // Then
        assertEquals(employeeId, result.getId());
        verify(employeeRepository).findById(employeeId);
        verify(employeeMapper).toResponseDTO(employee);
    }

    @Test
    void getEmployeeById_shouldThrowWhenEmployeeDoesNotExist() {
        // Given
        Long employeeId = 1L;
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(
                EmployeeNotFoundException.class,
                () -> employeeService.getEmployeeById(employeeId)
        );
        verify(employeeRepository).findById(employeeId);
        verify(employeeMapper, never()).toResponseDTO(any());
    }

    @Test
    void createEmployee_shouldSaveAndReturnEmployee() {
        // Given
        EmployeeRequestDTO requestDTO = new EmployeeRequestDTO();
        requestDTO.setEmail("sam@example.com");
        Employee employee = new Employee();
        Employee savedEmployee = new Employee();
        savedEmployee.setId(1L);
        EmployeeResponseDTO responseDTO = new EmployeeResponseDTO();
        responseDTO.setId(1L);

        when(employeeRepository.existsByEmail(requestDTO.getEmail())).thenReturn(false);
        when(employeeMapper.toEntity(requestDTO)).thenReturn(employee);
        when(employeeRepository.save(employee)).thenReturn(savedEmployee);
        when(employeeMapper.toResponseDTO(savedEmployee)).thenReturn(responseDTO);

        // When
        EmployeeResponseDTO result = employeeService.createEmployee(requestDTO);

        // Then
        assertEquals(1L, result.getId());
        verify(employeeRepository).existsByEmail(requestDTO.getEmail());
        verify(employeeMapper).toEntity(requestDTO);
        verify(employeeRepository).save(employee);
        verify(employeeMapper).toResponseDTO(savedEmployee);
    }

    @Test
    void createEmployee_shouldThrowWhenEmailAlreadyExists() {
        // Given
        EmployeeRequestDTO requestDTO = new EmployeeRequestDTO();
        requestDTO.setEmail("sam@example.com");
        when(employeeRepository.existsByEmail(requestDTO.getEmail())).thenReturn(true);

        // When / Then
        assertThrows(
                EmployeeAlreadyExistsException.class,
                () -> employeeService.createEmployee(requestDTO)
        );
        verify(employeeRepository).existsByEmail(requestDTO.getEmail());
        verify(employeeMapper, never()).toEntity(any());
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void updateEmployee_shouldUpdateEmployeeWhileKeepingItsEmail() {
        // Given
        Long employeeId = 1L;
        EmployeeRequestDTO requestDTO = new EmployeeRequestDTO();
        requestDTO.setFirstName("Sam");
        requestDTO.setEmail("sam@example.com");

        Employee employee = new Employee();
        employee.setId(employeeId);
        employee.setEmail("sam@example.com");

        Employee updatedValues = new Employee();
        updatedValues.setFirstName("Samuel");
        updatedValues.setEmail("sam@example.com");

        EmployeeResponseDTO responseDTO = new EmployeeResponseDTO();
        responseDTO.setId(employeeId);
        responseDTO.setFirstName("Samuel");
        responseDTO.setEmail("sam@example.com");

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(employeeRepository.findByEmail(requestDTO.getEmail())).thenReturn(Optional.of(employee));
        when(employeeMapper.toEntity(requestDTO)).thenReturn(updatedValues);
        when(employeeRepository.save(employee)).thenReturn(employee);
        when(employeeMapper.toResponseDTO(employee)).thenReturn(responseDTO);

        // When
        EmployeeResponseDTO result = employeeService.updateEmployee(employeeId, requestDTO);

        // Then
        assertEquals("Samuel", employee.getFirstName());
        assertEquals("sam@example.com", employee.getEmail());
        assertEquals(employeeId, result.getId());
        assertEquals("sam@example.com", result.getEmail());
        verify(employeeRepository).findById(employeeId);
        verify(employeeRepository).findByEmail(requestDTO.getEmail());
        verify(employeeMapper).toEntity(requestDTO);
        verify(employeeRepository).save(employee);
        verify(employeeMapper).toResponseDTO(employee);
    }

    @Test
    void updateEmployee_shouldUpdateWhenNewEmailIsAvailable() {
        // Given
        Long employeeId = 1L;
        String newEmail = "new.sam@example.com";
        EmployeeRequestDTO requestDTO = new EmployeeRequestDTO();
        requestDTO.setFirstName("Sam");
        requestDTO.setEmail(newEmail);

        Employee employee = new Employee();
        employee.setId(employeeId);
        employee.setEmail("sam@example.com");

        Employee updatedValues = new Employee();
        updatedValues.setFirstName("Sam");
        updatedValues.setEmail(newEmail);

        EmployeeResponseDTO responseDTO = new EmployeeResponseDTO();
        responseDTO.setId(employeeId);
        responseDTO.setEmail(newEmail);

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(employeeRepository.findByEmail(newEmail)).thenReturn(Optional.empty());
        when(employeeMapper.toEntity(requestDTO)).thenReturn(updatedValues);
        when(employeeRepository.save(employee)).thenReturn(employee);
        when(employeeMapper.toResponseDTO(employee)).thenReturn(responseDTO);

        // When
        EmployeeResponseDTO result = employeeService.updateEmployee(employeeId, requestDTO);

        // Then
        assertEquals(newEmail, employee.getEmail());
        assertEquals(newEmail, result.getEmail());
        verify(employeeRepository).findById(employeeId);
        verify(employeeRepository).findByEmail(newEmail);
        verify(employeeMapper).toEntity(requestDTO);
        verify(employeeRepository).save(employee);
        verify(employeeMapper).toResponseDTO(employee);
    }

    @Test
    void updateEmployee_shouldThrowWhenEmailBelongsToAnotherEmployee() {
        // Given
        Long employeeId = 1L;
        EmployeeRequestDTO requestDTO = new EmployeeRequestDTO();
        requestDTO.setEmail("other@example.com");
        Employee employee = new Employee();
        employee.setId(employeeId);
        Employee otherEmployee = new Employee();
        otherEmployee.setId(2L);

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(employeeRepository.findByEmail(requestDTO.getEmail())).thenReturn(Optional.of(otherEmployee));

        // When / Then
        assertThrows(
                EmployeeAlreadyExistsException.class,
                () -> employeeService.updateEmployee(employeeId, requestDTO)
        );
        verify(employeeRepository).findById(employeeId);
        verify(employeeRepository).findByEmail(requestDTO.getEmail());
        verify(employeeMapper, never()).toEntity(any());
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void updateEmployee_shouldThrowWhenEmployeeDoesNotExist() {
        // Given
        Long employeeId = 1L;
        EmployeeRequestDTO requestDTO = new EmployeeRequestDTO();
        requestDTO.setEmail("sam@example.com");
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(
                EmployeeNotFoundException.class,
                () -> employeeService.updateEmployee(employeeId, requestDTO)
        );
        verify(employeeRepository).findById(employeeId);
        verify(employeeRepository, never()).findByEmail(anyString());
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void deleteEmployee_shouldDeleteExistingEmployee() {
        // Given
        Long employeeId = 1L;
        Employee employee = new Employee();
        employee.setId(employeeId);
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

        // When
        employeeService.deleteEmployee(employeeId);

        // Then
        verify(employeeRepository).findById(employeeId);
        verify(employeeRepository).delete(employee);
    }

    @Test
    void deleteEmployee_shouldThrowWhenEmployeeDoesNotExist() {
        // Given
        Long employeeId = 1L;
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(
                EmployeeNotFoundException.class,
                () -> employeeService.deleteEmployee(employeeId)
        );
        verify(employeeRepository).findById(employeeId);
        verify(employeeRepository, never()).delete(any());
    }
}
