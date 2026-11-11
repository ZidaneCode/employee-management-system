package com.employeemanagement.backend.service;

import com.employeemanagement.backend.dto.UserRequestDTO;
import com.employeemanagement.backend.dto.UserResponseDTO;
import com.employeemanagement.backend.entity.User;
import com.employeemanagement.backend.exception.UserAlreadyExistsException;
import com.employeemanagement.backend.exception.UserNotFoundException;
import com.employeemanagement.backend.mapper.UserMapper;
import com.employeemanagement.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }
/**
     * Get all users.
     *
     * @return a list of UserResponseDTOs representing all users
     */
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponseDTO)
                .toList();
    }

    /**
     * Get a user by ID.
     *
     * @param id the ID of the user to retrieve
     * @return the UserResponseDTO representing the user with the specified ID
     * @throws UserNotFoundException if no user is found with the specified ID
     */
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return userMapper.toResponseDTO(user);
    }
/**
     * Create a new user.
     *
     * @param userRequestDTO the UserRequestDTO containing the details of the user to create
     * @return the created UserResponseDTO
     */
    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        if(userRepository.existsByEmail(userRequestDTO.getEmail())) {
            throw new UserAlreadyExistsException("User already exists with email: " + userRequestDTO.getEmail());

        }
        User user = userMapper.toEntity(userRequestDTO);
        String password = userRequestDTO.getPassword();
        String hashedPassword = passwordEncoder.encode(password);
        user.setPassword(hashedPassword);

        return userMapper.toResponseDTO(userRepository.save(user));
    }

    public UserResponseDTO updateUser(Long id ,UserRequestDTO userRequestDTO){

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        if(userRepository.existsByEmail(userRequestDTO.getEmail()) && !user.getEmail().equals(userRequestDTO.getEmail())) {
            throw new UserAlreadyExistsException("User already exists with email: " + userRequestDTO.getEmail());
        }
        userMapper.updateEntity(user, userRequestDTO);



        return userMapper.toResponseDTO(userRepository.save(user));
    }
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        userRepository.delete(user);

    }
}
