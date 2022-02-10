package com.kang.app.ws.service.impl;

import com.kang.app.ws.entity.UserEntity;
import com.kang.app.ws.repository.UserRepository;
import com.kang.app.ws.service.UserService;
import com.kang.app.ws.shared.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void should_ThrowUsernameNotFoundException_WhenEmailNotFound() {
        when(userRepository.findByEmail(anyString())).thenThrow(UsernameNotFoundException.class);

        Executable executable = () -> userService.getUser("test@test.com");

        assertThrows(UsernameNotFoundException.class, executable);

    }

    @Test
    void should_ReturnCorrectUser_WhenGetUserByCorrectEmail() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setFirstName("Anqi");
        userEntity.setUserId("adgsiuy3g2uyeg");
        userEntity.setEncryptPassword("qwgdjkbqwdkugiugu");
        when(userRepository.findByEmail(anyString())).thenReturn(userEntity);

        UserDto userDto = userService.getUser("test@test.com");

        assertAll(
                () -> assertEquals(userEntity.getId(), userDto.getId()),
                () -> assertEquals(userEntity.getFirstName(), userDto.getFirstName()),
                () -> assertEquals(userEntity.getEncryptPassword(), userDto.getEncryptPassword())
        );

    }
}