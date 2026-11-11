package com.employeemanagement.backend.mapper;

import com.employeemanagement.backend.dto.DepartmentRequestDTO;
import com.employeemanagement.backend.dto.DepartmentResponseDTO;
import com.employeemanagement.backend.entity.Department;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DepartmentMapperTest {

    @Test
    void toEntity_shouldMapDepartmentFields() {
        // Implement test for toEntity method
        //Given
        DepartmentMapper departmentMapper = new DepartmentMapper();
        DepartmentRequestDTO dto = new DepartmentRequestDTO();
        dto.setName("Test Department");
        dto.setDescription("Test Description");
        //When
        Department department = departmentMapper.toEntity(dto);
        //Then
        assertEquals("Test Department", department.getName());
        assertEquals("Test Description", department.getDescription());
    }

    @Test
    void toResponseDTO_shouldMapDepartmentFields(){
        //Given
        Department department=new Department();
        department.setId(1L);
        department.setName("Test Department");
        department.setDescription("Test Description");
        DepartmentMapper departmentMapper=new DepartmentMapper();

        //When
        DepartmentResponseDTO ResponsDTO=departmentMapper.toResponseDTO(department);

        //Then
        assertEquals(1L, ResponsDTO.getId());
        assertEquals("Test Department", ResponsDTO.getName());
        assertEquals("Test Description", ResponsDTO.getDescription());
    }

}