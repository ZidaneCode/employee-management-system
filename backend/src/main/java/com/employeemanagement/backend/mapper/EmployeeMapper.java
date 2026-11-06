package com.employeemanagement.backend.mapper;

import com.employeemanagement.backend.dto.EmployeeRequestDTO;
import com.employeemanagement.backend.dto.EmployeeResponseDTO;
import com.employeemanagement.backend.entity.Department;
import com.employeemanagement.backend.entity.Employee;
import com.employeemanagement.backend.exception.DepartmentNotFoundException;
import com.employeemanagement.backend.repository.DepartmentRepository;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    private final DepartmentRepository departmentRepository;

    public EmployeeMapper(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public Employee toEntity(EmployeeRequestDTO dto) {
        Department department = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new DepartmentNotFoundException(
                        "Department not found with id: " + dto.getDepartmentId()
                ));

        Employee employee = new Employee();
        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setEmail(dto.getEmail());
        employee.setPhone(dto.getPhone());
        employee.setHireDate(dto.getHireDate());
        employee.setSalary(dto.getSalary());
        employee.setPosition(dto.getPosition());
        employee.setStatus(dto.getStatus());
        employee.setDepartment(department);
        return employee;
    }

    public EmployeeResponseDTO toResponseDTO(Employee employee) {
        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setId(employee.getId());
        dto.setFirstName(employee.getFirstName());
        dto.setLastName(employee.getLastName());
        dto.setEmail(employee.getEmail());
        dto.setPhone(employee.getPhone());
        dto.setHireDate(employee.getHireDate());
        dto.setSalary(employee.getSalary());
        dto.setPosition(employee.getPosition());
        dto.setStatus(employee.getStatus());
        dto.setDepartmentId(employee.getDepartment().getId());
        dto.setDepartmentName(employee.getDepartment().getName());
        return dto;
    }
}
