package com.employeemanagement.backend.service;

import com.employeemanagement.backend.dto.DepartmentRequestDTO;
import com.employeemanagement.backend.dto.DepartmentResponseDTO;
import com.employeemanagement.backend.entity.Department;
import com.employeemanagement.backend.exception.DepartmentAlreadyExistsException;
import com.employeemanagement.backend.exception.DepartmentNotFoundException;
import com.employeemanagement.backend.mapper.DepartmentMapper;
import com.employeemanagement.backend.repository.DepartmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    public DepartmentService(DepartmentRepository departmentRepository, DepartmentMapper departmentMapper) {
        this.departmentRepository = departmentRepository;
        this.departmentMapper = departmentMapper;
    }

    public List<DepartmentResponseDTO> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(departmentMapper::toResponseDTO)
                .toList();
    }

    public DepartmentResponseDTO getDepartmentById(Long id) {
        Department department = findDepartmentById(id);
        return departmentMapper.toResponseDTO(department);
    }

    private Department findDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new DepartmentNotFoundException("Department not found with id: " + id));
    }
    /**
     * Create a new department.
     *
     * @param departmentRequestDTO the DepartmentRequestDTO containing the details of the department to create
     * @return the created DepartmentResponseDTO
     */
    public DepartmentResponseDTO createDepartment(DepartmentRequestDTO departmentRequestDTO) {


        if (departmentRepository.existsByName(departmentRequestDTO.getName())) {
            throw new DepartmentAlreadyExistsException("Department already exists with name: " + departmentRequestDTO.getName());
        }
        Department department = departmentMapper.toEntity(departmentRequestDTO);

        Department savedDepartment= departmentRepository.save(department);
        return departmentMapper.toResponseDTO(savedDepartment);

    }
    /**
     * Update an existing department.
     *
     * @param id the ID of the department to update
     * @param departmentRequestDTO the DepartmentRequestDTO containing the updated details of the department
     * @return the updated DepartmentResponseDTO
     */
    public DepartmentResponseDTO updateDepartment(Long id, DepartmentRequestDTO departmentRequestDTO) {

        Department department = findDepartmentById(id);
        department.setName(departmentRequestDTO.getName());
        department.setDescription(departmentRequestDTO.getDescription());

        Department updatedDepartment = departmentRepository.save(department);

        return departmentMapper.toResponseDTO(updatedDepartment);
    }

    // Supprimer un département
    public void deleteDepartment(Long id) {
        Department department = findDepartmentById(id);
        departmentRepository.delete(department);
    }
}
