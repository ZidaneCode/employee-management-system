package com.employeemanagement.backend.mapper;

import com.employeemanagement.backend.dto.UserRequestDTO;
import com.employeemanagement.backend.dto.UserResponseDTO;
import com.employeemanagement.backend.entity.Role;
import com.employeemanagement.backend.entity.User;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserMapperTest {

    private final UserMapper userMapper = new UserMapper();

    @Test
    void toEntity_shouldMapUserFieldsAndLeavePasswordUnset() {
        // Given
        UserRequestDTO dto = createRequestDTO();

        // When
        User result = userMapper.toEntity(dto);

        // Then
        assertEquals(dto.getFirstName(), result.getFirstName());
        assertEquals(dto.getLastName(), result.getLastName());
        assertEquals(dto.getEmail(), result.getEmail());
        assertEquals(dto.getRole(), result.getRole());
        assertTrue(result.isEnabled());
        assertNull(result.getPassword());
    }

    @Test
    void toResponseDTO_shouldMapExpectedFieldsWithoutExposingPassword() {
        // Given
        User user = createUser();

        // When
        UserResponseDTO result = userMapper.toResponseDTO(user);

        // Then
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getFirstName(), result.getFirstName());
        assertEquals(user.getLastName(), result.getLastName());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getRole(), result.getRole());
        assertEquals(user.isEnabled(), result.isEnabled());
        assertFalse(Arrays.stream(UserResponseDTO.class.getDeclaredFields())
                .map(Field::getName)
                .anyMatch("password"::equals));
    }

    @Test
    void updateEntity_shouldUpdateUserFieldsAndKeepExistingPassword() {
        // Given
        User user = createUser();
        user.setPassword("existing-encoded-password");
        UserRequestDTO dto = createRequestDTO();
        dto.setFirstName("Updated");
        dto.setLastName("Person");
        dto.setEmail("updated@example.com");
        dto.setRole(Role.ADMIN);
        dto.setEnabled(false);
        dto.setPassword("new-plain-password");

        // When
        userMapper.updateEntity(user, dto);

        // Then
        assertEquals(dto.getFirstName(), user.getFirstName());
        assertEquals(dto.getLastName(), user.getLastName());
        assertEquals(dto.getEmail(), user.getEmail());
        assertEquals(dto.getRole(), user.getRole());
        assertEquals(dto.isEnabled(), user.isEnabled());
        assertEquals("existing-encoded-password", user.getPassword());
    }

    private static UserRequestDTO createRequestDTO() {
        UserRequestDTO dto = new UserRequestDTO();
        dto.setFirstName("Jane");
        dto.setLastName("Doe");
        dto.setEmail("jane@example.com");
        dto.setPassword("plain-password");
        dto.setRole(Role.HR);
        dto.setEnabled(true);
        return dto;
    }

    private static User createUser() {
        User user = new User();
        user.setId(7L);
        user.setFirstName("Jane");
        user.setLastName("Doe");
        user.setEmail("jane@example.com");
        user.setPassword("encoded-password");
        user.setRole(Role.HR);
        user.setEnabled(true);
        return user;
    }
}
