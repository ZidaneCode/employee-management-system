package com.employeemanagement.backend.service;

import com.employeemanagement.backend.dto.EmployeeRequestDTO;
import com.employeemanagement.backend.dto.EmployeeResponseDTO;
import com.employeemanagement.backend.entity.Employee;
import com.employeemanagement.backend.exception.EmployeeAlreadyExistsException;
import com.employeemanagement.backend.exception.EmployeeNotFoundException;
import com.employeemanagement.backend.mapper.EmployeeMapper;
import com.employeemanagement.backend.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Transactional
@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    public EmployeeService(EmployeeRepository employeeRepository, EmployeeMapper employeeMapper) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
    }

    public List<EmployeeResponseDTO> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(employeeMapper::toResponseDTO)
                .toList();
    }

    public EmployeeResponseDTO getEmployeeById(Long id) {
        return employeeMapper.toResponseDTO(findEmployeeById(id));
    }

    public EmployeeResponseDTO createEmployee(EmployeeRequestDTO dto) {
        if (employeeRepository.existsByEmail(dto.getEmail())) {
            throw new EmployeeAlreadyExistsException(
                    "Employee already exists with email: " + dto.getEmail()
            );
        }

        Employee employee = employeeMapper.toEntity(dto);
        Employee savedEmployee = employeeRepository.save(employee);
        return employeeMapper.toResponseDTO(savedEmployee);
    }

    public EmployeeResponseDTO updateEmployee(Long id, EmployeeRequestDTO dto) {
        Employee employee = findEmployeeById(id);

        employeeRepository.findByEmail(dto.getEmail())
                .filter(employeeWithEmail -> !Objects.equals(employeeWithEmail.getId(), employee.getId()))
                .ifPresent(employeeWithEmail -> {
                    throw new EmployeeAlreadyExistsException(
                            "Employee already exists with email: " + dto.getEmail()
                    );
                });

        Employee updatedEmployee = employeeMapper.toEntity(dto);
        employee.setFirstName(updatedEmployee.getFirstName());
        employee.setLastName(updatedEmployee.getLastName());
        employee.setEmail(updatedEmployee.getEmail());
        employee.setPhone(updatedEmployee.getPhone());
        employee.setHireDate(updatedEmployee.getHireDate());
        employee.setSalary(updatedEmployee.getSalary());
        employee.setPosition(updatedEmployee.getPosition());
        employee.setStatus(updatedEmployee.getStatus());
        employee.setDepartment(updatedEmployee.getDepartment());

        Employee savedEmployee = employeeRepository.save(employee);
        return employeeMapper.toResponseDTO(savedEmployee);
    }

    public void deleteEmployee(Long id) {
        employeeRepository.delete(findEmployeeById(id));
    }

    private Employee findEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));
    }
}
