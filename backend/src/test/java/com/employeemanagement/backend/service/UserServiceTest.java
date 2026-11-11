package com.employeemanagement.backend.service;

import com.employeemanagement.backend.dto.UserRequestDTO;
import com.employeemanagement.backend.dto.UserResponseDTO;
import com.employeemanagement.backend.entity.Role;
import com.employeemanagement.backend.entity.User;
import com.employeemanagement.backend.exception.UserAlreadyExistsException;
import com.employeemanagement.backend.exception.UserNotFoundException;
import com.employeemanagement.backend.mapper.UserMapper;
import com.employeemanagement.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void getAllUsers_shouldReturnUserList() {
        // Given
        User user = createUser(1L, "jane@example.com", "encoded-password");
        UserResponseDTO responseDTO = createResponseDTO(1L, "jane@example.com");
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toResponseDTO(user)).thenReturn(responseDTO);

        // When
        List<UserResponseDTO> result = userService.getAllUsers();

        // Then
        assertEquals(1, result.size());
        assertEquals("jane@example.com", result.get(0).getEmail());
        verify(userRepository).findAll();
        verify(userMapper).toResponseDTO(user);
    }

    @Test
    void getAllUsers_shouldReturnEmptyListWhenNoUsersExist() {
        // Given
        when(userRepository.findAll()).thenReturn(List.of());

        // When
        List<UserResponseDTO> result = userService.getAllUsers();

        // Then
        assertTrue(result.isEmpty());
        verify(userRepository).findAll();
        verifyNoInteractions(userMapper);
    }

    @Test
    void getUserById_shouldReturnUserWhenIdExists() {
        // Given
        Long userId = 1L;
        User user = createUser(userId, "jane@example.com", "encoded-password");
        UserResponseDTO responseDTO = createResponseDTO(userId, "jane@example.com");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toResponseDTO(user)).thenReturn(responseDTO);

        // When
        UserResponseDTO result = userService.getUserById(userId);

        // Then
        assertEquals(userId, result.getId());
        assertEquals("jane@example.com", result.getEmail());
        verify(userRepository).findById(userId);
        verify(userMapper).toResponseDTO(user);
    }

    @Test
    void getUserById_shouldThrowWhenUserDoesNotExist() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(userId));
        verify(userRepository).findById(userId);
        verifyNoInteractions(userMapper);
    }

    @Test
    void createUser_shouldCreateAndReturnUser() {
        // Given
        UserRequestDTO requestDTO = createRequestDTO("jane@example.com", "plain-password");
        User user = createUser(null, "jane@example.com", null);
        UserResponseDTO responseDTO = createResponseDTO(1L, "jane@example.com");
        when(userRepository.existsByEmail(requestDTO.getEmail())).thenReturn(false);
        when(userMapper.toEntity(requestDTO)).thenReturn(user);
        when(passwordEncoder.encode("plain-password")).thenReturn("encoded-password");
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponseDTO(user)).thenReturn(responseDTO);

        // When
        UserResponseDTO result = userService.createUser(requestDTO);

        // Then
        assertEquals(1L, result.getId());
        assertEquals("jane@example.com", result.getEmail());
        verify(userRepository).existsByEmail(requestDTO.getEmail());
        verify(userMapper).toEntity(requestDTO);
        verify(userRepository).save(user);
        verify(userMapper).toResponseDTO(user);
        verify(passwordEncoder).encode("plain-password");
    }

    @Test
    void createUser_shouldEncodePasswordBeforeSavingUser() {
        // Given
        UserRequestDTO requestDTO = createRequestDTO("jane@example.com", "plain-password");
        User user = createUser(null, "jane@example.com", null);
        when(userRepository.existsByEmail(requestDTO.getEmail())).thenReturn(false);
        when(userMapper.toEntity(requestDTO)).thenReturn(user);
        when(passwordEncoder.encode("plain-password")).thenReturn("encoded-password");
        when(userRepository.save(user)).thenReturn(user);

        // When
        userService.createUser(requestDTO);

        // Then
        assertEquals("encoded-password", user.getPassword());
        InOrder inOrder = inOrder(passwordEncoder, userRepository);
        inOrder.verify(passwordEncoder).encode("plain-password");
        inOrder.verify(userRepository).save(user);
    }

    @Test
    void createUser_shouldThrowWhenEmailAlreadyExists() {
        // Given
        UserRequestDTO requestDTO = createRequestDTO("jane@example.com", "plain-password");
        when(userRepository.existsByEmail(requestDTO.getEmail())).thenReturn(true);

        // When / Then
        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(requestDTO));
        verify(userRepository).existsByEmail(requestDTO.getEmail());
        verifyNoInteractions(userMapper, passwordEncoder);
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_shouldUpdateExistingUser() {
        // Given
        Long userId = 1L;
        UserRequestDTO requestDTO = createRequestDTO("new.jane@example.com", "new-password");
        requestDTO.setFirstName("Janet");
        User user = createUser(userId, "jane@example.com", "encoded-password");
        UserResponseDTO responseDTO = createResponseDTO(userId, "new.jane@example.com");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(requestDTO.getEmail())).thenReturn(false);
        stubUpdateWithoutChangingPassword(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponseDTO(user)).thenReturn(responseDTO);

        // When
        UserResponseDTO result = userService.updateUser(userId, requestDTO);

        // Then
        assertEquals("Janet", user.getFirstName());
        assertEquals("new.jane@example.com", user.getEmail());
        assertEquals(userId, result.getId());
        assertEquals("new.jane@example.com", result.getEmail());
        assertEquals("encoded-password", user.getPassword());
        verify(userRepository).findById(userId);
        verify(userRepository).existsByEmail(requestDTO.getEmail());
        verify(userMapper).updateEntity(user, requestDTO);
        verify(userRepository).save(user);
        verify(userMapper).toResponseDTO(user);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void updateUser_shouldKeepEmailWhenUserKeepsTheirOwnEmail() {
        // Given
        Long userId = 1L;
        String email = "jane@example.com";
        UserRequestDTO requestDTO = createRequestDTO(email, "new-password");
        User user = createUser(userId, email, "encoded-password");
        UserResponseDTO responseDTO = createResponseDTO(userId, email);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(email)).thenReturn(true);
        stubUpdateWithoutChangingPassword(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponseDTO(user)).thenReturn(responseDTO);

        // When
        UserResponseDTO result = userService.updateUser(userId, requestDTO);

        // Then
        assertEquals(email, user.getEmail());
        assertEquals(email, result.getEmail());
        assertEquals("encoded-password", user.getPassword());
        verify(userRepository).existsByEmail(email);
        verify(userMapper).updateEntity(user, requestDTO);
        verify(userRepository).save(user);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void updateUser_shouldThrowWhenEmailBelongsToAnotherUser() {
        // Given
        Long userId = 1L;
        UserRequestDTO requestDTO = createRequestDTO("other@example.com", "new-password");
        User user = createUser(userId, "jane@example.com", "encoded-password");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(requestDTO.getEmail())).thenReturn(true);

        // When / Then
        assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.updateUser(userId, requestDTO)
        );
        verify(userRepository).findById(userId);
        verify(userRepository).existsByEmail(requestDTO.getEmail());
        verify(userMapper, never()).updateEntity(any(), any());
        verify(userRepository, never()).save(any());
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void updateUser_shouldThrowWhenUserDoesNotExist() {
        // Given
        Long userId = 1L;
        UserRequestDTO requestDTO = createRequestDTO("jane@example.com", "new-password");
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(UserNotFoundException.class, () -> userService.updateUser(userId, requestDTO));
        verify(userRepository).findById(userId);
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any());
        verifyNoInteractions(userMapper, passwordEncoder);
    }

    @Test
    void deleteUser_shouldDeleteExistingUser() {
        // Given
        Long userId = 1L;
        User user = createUser(userId, "jane@example.com", "encoded-password");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        userService.deleteUser(userId);

        // Then
        verify(userRepository).findById(userId);
        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_shouldThrowWhenUserDoesNotExist() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(userId));
        verify(userRepository).findById(userId);
        verify(userRepository, never()).delete(any());
    }

    private static User createUser(Long id, String email, String password) {
        User user = new User();
        user.setId(id);
        user.setFirstName("Jane");
        user.setLastName("Doe");
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(Role.HR);
        user.setEnabled(true);
        return user;
    }

    private static UserRequestDTO createRequestDTO(String email, String password) {
        UserRequestDTO dto = new UserRequestDTO();
        dto.setFirstName("Jane");
        dto.setLastName("Doe");
        dto.setEmail(email);
        dto.setPassword(password);
        dto.setRole(Role.HR);
        dto.setEnabled(true);
        return dto;
    }

    private static UserResponseDTO createResponseDTO(Long id, String email) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(id);
        dto.setFirstName("Jane");
        dto.setLastName("Doe");
        dto.setEmail(email);
        dto.setRole(Role.HR);
        dto.setEnabled(true);
        return dto;
    }

    private void stubUpdateWithoutChangingPassword(User user) {
        doAnswer(invocation -> {
            User target = invocation.getArgument(0);
            UserRequestDTO dto = invocation.getArgument(1);
            target.setFirstName(dto.getFirstName());
            target.setLastName(dto.getLastName());
            target.setEmail(dto.getEmail());
            target.setRole(dto.getRole());
            target.setEnabled(dto.isEnabled());
            return null;
        }).when(userMapper).updateEntity(eq(user), any(UserRequestDTO.class));
    }
}
