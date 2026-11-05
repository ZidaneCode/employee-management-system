package com.employeemanagement.backend.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DepartmentResponseDTO {

    private Long id;
    private String name;
    private String description;
}