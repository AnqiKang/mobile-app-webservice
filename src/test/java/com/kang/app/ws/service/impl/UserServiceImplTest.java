package com.kang.app.ws.service.impl;

import com.kang.app.ws.entity.UserEntity;
import com.kang.app.ws.repository.PasswordResetTokenRepository;
import com.kang.app.ws.repository.UserRepository;
import com.kang.app.ws.service.UserService;
import com.kang.app.ws.shared.AddressDTO;
import com.kang.app.ws.shared.UserDto;
import com.kang.app.ws.shared.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private Utils utils;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private String userId = "adgsiuy3g2uyeg";
    private String encryptedPassword = "wqdhjwqdgqduygewd";
    private UserEntity userEntity;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setFirstName("Anqi");
        userEntity.setUserId(userId);
        userEntity.setEncryptPassword(encryptedPassword);
        userEntity.setEmail("kanganqi666@gmail.com");
        userEntity.setEmailVerificationToken("diqdhiuwqhdiu");
    }

    @Test
    void should_ThrowUsernameNotFoundException_WhenEmailNotFound() {
        when(userRepository.findByEmail(anyString())).thenThrow(UsernameNotFoundException.class);

        Executable executable = () -> userService.getUser("kanganqi666@gmail.com");

        assertThrows(UsernameNotFoundException.class, executable);
    }

    @Test
    void should_ReturnCorrectUser_WhenGetUserByCorrectEmail() {
        when(userRepository.findByEmail(anyString())).thenReturn(userEntity);

        UserDto userDto = userService.getUser("kanganqi666@gmail.com");

        assertAll(
                () -> assertEquals(userEntity.getId(), userDto.getId()),
                () -> assertEquals(userEntity.getFirstName(), userDto.getFirstName()),
                () -> assertEquals(userEntity.getEncryptPassword(), userDto.getEncryptPassword())
        );

    }

    @Test
    void should_ReturnCorrect_WhenCreateUser() {

        when(userRepository.findByEmail(anyString())).thenReturn(null);
        when(utils.generateAddressId(anyInt())).thenReturn("hskqwshwkhswsh887");
        when(utils.generateUserId(anyInt())).thenReturn(userId);
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn(encryptedPassword);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setType("shipping");

        List<AddressDTO> addressDTOList = new ArrayList<>();
        addressDTOList.add(addressDTO);

        UserDto userDto = new UserDto();
        userDto.setAddresses(addressDTOList);

        UserDto storedUserDetails = userService.createUser(userDto);

        assertAll(
                () -> assertNotNull(storedUserDetails),
                () -> assertEquals(userEntity.getFirstName(), storedUserDetails.getFirstName())
        );


    }


}