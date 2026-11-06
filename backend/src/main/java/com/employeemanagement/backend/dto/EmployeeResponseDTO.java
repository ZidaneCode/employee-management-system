package com.employeemanagement.backend.dto;

import com.employeemanagement.backend.entity.EmployeeStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class EmployeeResponseDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private LocalDate hireDate;
    private BigDecimal salary;
    private String position;
    private EmployeeStatus status;
    private Long departmentId;
    private String departmentName;
}
