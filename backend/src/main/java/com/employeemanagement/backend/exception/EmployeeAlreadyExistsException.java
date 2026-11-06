package com.employeemanagement.backend.exception;

public class EmployeeAlreadyExistsException extends RuntimeException {

    public EmployeeAlreadyExistsException(String message) {
        super(message);
    }
}
