package com.employeemanagement.backend.controller;

import com.employeemanagement.backend.dto.UserRequestDTO;
import com.employeemanagement.backend.dto.UserResponseDTO;
import com.employeemanagement.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }
    /**
     * Get all users.
     *
     * @return a list of UserResponseDTO representing all users
     */
    @GetMapping
    public List<UserResponseDTO> getAllUsers() {
    return userService.getAllUsers();

    }

    @GetMapping("/{id}")
    public UserResponseDTO getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    /**
     * Create a new user.
     *
     * @param userRequestDTO the UserRequestDTO containing the details of the user to create
     * @return the created UserResponseDTO
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDTO createUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        return userService.createUser(userRequestDTO);
    }

    /**
     * Update an existing user.
     *
     * @param id the ID of the user to update
     * @param userRequestDTO the UserRequestDTO containing the updated details of the user
     * @return the updated UserResponseDTO
     */
    @PutMapping("/{id}")
    public UserResponseDTO updateUser(@PathVariable Long id , @Valid @RequestBody UserRequestDTO userRequestDTO) {
        return userService.updateUser(id, userRequestDTO);
    }

}