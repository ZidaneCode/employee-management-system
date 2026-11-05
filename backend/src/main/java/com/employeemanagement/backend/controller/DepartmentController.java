package com.employeemanagement.backend.controller;

import com.employeemanagement.backend.dto.DepartmentRequestDTO;
import com.employeemanagement.backend.dto.DepartmentResponseDTO;
import com.employeemanagement.backend.service.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RequestMapping("/api/departments")
@RestController
public class DepartmentController {

    private final DepartmentService departmentService;
    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }
    /**
     * Get all departments.
     *
     * @return a list of DepartmentResponseDTO representing all departments
     */
    @GetMapping
    public List<DepartmentResponseDTO> getAllDepartments() {
        return departmentService.getAllDepartments();
    }
    /**
     * Get a department by its ID.
     *
     * @param id the ID of the department to retrieve
     * @return the DepartmentResponseDTO corresponding to the specified ID
     */
    @GetMapping("/{id}")
    public DepartmentResponseDTO getDepartmentById(@PathVariable Long id) {
        return departmentService.getDepartmentById(id);
    }
    /**
     * Create a new department.
     *
     * @param departmentRequestDTO the DepartmentRequestDTO containing the details of the department to create
     * @return the created DepartmentResponseDTO
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DepartmentResponseDTO createDepartment(
            @Valid @RequestBody DepartmentRequestDTO departmentRequestDTO) {

        return departmentService.createDepartment(departmentRequestDTO);
    }

    /**
     * Update an existing department.
     *
     * @param id the ID of the department to update
     * @param departmentRequestDTO the DepartmentRequestDTO containing the updated details of the department
     * @return the updated DepartmentResponseDTO
     */
    @PutMapping("/{id}")
    public DepartmentResponseDTO updateDepartment(
            @PathVariable Long id,
            @Valid @RequestBody DepartmentRequestDTO departmentRequestDTO) {
        return departmentService.updateDepartment(id, departmentRequestDTO);
    }
    /**
     * Delete a department by its ID.
     *
     * @param id the ID of the department to delete
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
    }
}
