package com.employeemanagement.backend.mapper;

import com.employeemanagement.backend.dto.EmployeeRequestDTO;
import com.employeemanagement.backend.dto.EmployeeResponseDTO;
import com.employeemanagement.backend.entity.Department;
import com.employeemanagement.backend.entity.Employee;
import com.employeemanagement.backend.entity.EmployeeStatus;
import com.employeemanagement.backend.exception.DepartmentNotFoundException;
import com.employeemanagement.backend.repository.DepartmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeMapperTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private EmployeeMapper employeeMapper;

    @Test
    void toEntity_shouldMapEmployeeFieldsAndAssociateDepartment() {
        // Given
        Long departmentId = 5L;
        Department department = createDepartment(departmentId, "Engineering");
        EmployeeRequestDTO dto = createEmployeeRequestDTO(departmentId);
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(department));

        // When
        Employee result = employeeMapper.toEntity(dto);

        // Then
        assertEquals(dto.getFirstName(), result.getFirstName());
        assertEquals(dto.getLastName(), result.getLastName());
        assertEquals(dto.getEmail(), result.getEmail());
        assertEquals(dto.getPhone(), result.getPhone());
        assertEquals(dto.getHireDate(), result.getHireDate());
        assertEquals(dto.getSalary(), result.getSalary());
        assertEquals(dto.getPosition(), result.getPosition());
        assertEquals(dto.getStatus(), result.getStatus());
        assertEquals(department, result.getDepartment());
        verify(departmentRepository).findById(departmentId);
    }

    @Test
    void toEntity_shouldThrowWhenDepartmentDoesNotExist() {
        // Given
        Long departmentId = 5L;
        EmployeeRequestDTO dto = createEmployeeRequestDTO(departmentId);
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(DepartmentNotFoundException.class, () -> employeeMapper.toEntity(dto));
        verify(departmentRepository).findById(departmentId);
    }

    @Test
    void toResponseDTO_shouldMapEmployeeFieldsAndDepartmentInformation() {
        // Given
        Department department = createDepartment(5L, "Engineering");
        Employee employee = createEmployee(department);

        // When
        EmployeeResponseDTO result = employeeMapper.toResponseDTO(employee);

        // Then
        assertEquals(employee.getId(), result.getId());
        assertEquals(employee.getFirstName(), result.getFirstName());
        assertEquals(employee.getLastName(), result.getLastName());
        assertEquals(employee.getEmail(), result.getEmail());
        assertEquals(employee.getPhone(), result.getPhone());
        assertEquals(employee.getHireDate(), result.getHireDate());
        assertEquals(employee.getSalary(), result.getSalary());
        assertEquals(employee.getPosition(), result.getPosition());
        assertEquals(employee.getStatus(), result.getStatus());
        assertEquals(department.getId(), result.getDepartmentId());
        assertEquals(department.getName(), result.getDepartmentName());
    }

    private static EmployeeRequestDTO createEmployeeRequestDTO(Long departmentId) {
        EmployeeRequestDTO dto = new EmployeeRequestDTO();
        dto.setFirstName("Ada");
        dto.setLastName("Lovelace");
        dto.setEmail("ada.lovelace@example.com");
        dto.setPhone("+33123456789");
        dto.setHireDate(LocalDate.of(2024, 3, 15));
        dto.setSalary(new BigDecimal("65000.00"));
        dto.setPosition("Software Engineer");
        dto.setStatus(EmployeeStatus.ACTIVE);
        dto.setDepartmentId(departmentId);
        return dto;
    }

    private static Department createDepartment(Long id, String name) {
        Department department = new Department();
        department.setId(id);
        department.setName(name);
        department.setDescription(name + " Department");
        return department;
    }

    private static Employee createEmployee(Department department) {
        Employee employee = new Employee();
        employee.setId(12L);
        employee.setFirstName("Ada");
        employee.setLastName("Lovelace");
        employee.setEmail("ada.lovelace@example.com");
        employee.setPhone("+33123456789");
        employee.setHireDate(LocalDate.of(2024, 3, 15));
        employee.setSalary(new BigDecimal("65000.00"));
        employee.setPosition("Software Engineer");
        employee.setStatus(EmployeeStatus.ACTIVE);
        employee.setDepartment(department);
        return employee;
    }
}