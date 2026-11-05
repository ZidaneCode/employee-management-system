package com.employeemanagement.backend.mapper;

import com.employeemanagement.backend.dto.DepartmentRequestDTO;
import com.employeemanagement.backend.dto.DepartmentResponseDTO;
import com.employeemanagement.backend.entity.Department;
import org.springframework.stereotype.Component;
@Component
public class DepartmentMapper {
    // Convertir un DTO en entité
    /**
     * Convert a DepartmentRequestDTO to a Department entity.
     *
     * @param dto the DepartmentRequestDTO to convert
     * @return the corresponding Department entity
     */
    public Department toEntity(DepartmentRequestDTO dto) {
        Department department = new Department();
        department.setName(dto.getName());
        department.setDescription(dto.getDescription());
        return department;
    }

    // Convertir une entité en DTO
    /**
     * Convert a Department entity to a DepartmentResponseDTO.
     *
     * @param department the Department entity to convert
     * @return the corresponding DepartmentResponseDTO
     */
    public DepartmentResponseDTO toResponseDTO(Department department) {
        DepartmentResponseDTO dto = new DepartmentResponseDTO();
        dto.setId(department.getId());
        dto.setName(department.getName());
        dto.setDescription(department.getDescription());
        return dto;
    }

}
