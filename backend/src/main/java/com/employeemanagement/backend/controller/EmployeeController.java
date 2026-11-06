package com.employeemanagement.backend.controller;

import com.employeemanagement.backend.dto.EmployeeRequestDTO;
import com.employeemanagement.backend.dto.EmployeeResponseDTO;
import com.employeemanagement.backend.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }
    /**
     * Get all employees.
     *
     * @return a list of EmployeeResponseDTO representing all employees
     */
    @GetMapping
    public List<EmployeeResponseDTO> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    /**
     * Get an employee by ID.
     *
     * @param id the ID of the employee to retrieve
     * @return the EmployeeResponseDTO representing the employee
     */
    @GetMapping("/{id}")
    public EmployeeResponseDTO getEmployeeById(@PathVariable Long id) {
        return employeeService.getEmployeeById(id);
    }

    /**
     * Create a new employee.
     *
     * @param dto the DTO containing the employee data
     * @return the EmployeeResponseDTO representing the created employee
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeResponseDTO createEmployee(@Valid @RequestBody EmployeeRequestDTO dto) {
        return employeeService.createEmployee(dto);
    }

    /**
     * Update an existing employee.
     *
     * @param id the ID of the employee to update
     * @param dto the DTO containing the updated employee data
     * @return the EmployeeResponseDTO representing the updated employee
     */
    @PutMapping("/{id}")
    public EmployeeResponseDTO updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequestDTO dto
    ) {
        return employeeService.updateEmployee(id, dto);
    }

    /**
     * Delete an employee.
     *
     * @param id the ID of the employee to delete
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
    }
}
