package com.employeemanagement.backend.exception;

public class DepartmentNotFoundException extends RuntimeException {


    public DepartmentNotFoundException(String message) {
        super(message);
    }
}
