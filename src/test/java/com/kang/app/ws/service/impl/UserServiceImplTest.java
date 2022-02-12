package com.kang.app.ws.service.impl;

import com.kang.app.ws.entity.AddressEntity;
import com.kang.app.ws.entity.UserEntity;
import com.kang.app.ws.exceptions.UserServiceException;
import com.kang.app.ws.repository.PasswordResetTokenRepository;
import com.kang.app.ws.repository.UserRepository;
import com.kang.app.ws.service.UserService;
import com.kang.app.ws.shared.AddressDTO;
import com.kang.app.ws.shared.AmazonSES;
import com.kang.app.ws.shared.UserDto;
import com.kang.app.ws.shared.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private Utils utils;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private AmazonSES amazonSES;

    private String userId = "adgsiuy3g2uyeg";
    private String encryptedPassword = "wqdhjwqdgqduygewd";
    private UserEntity userEntity;
    private String email = "kanganqi666@gmail.com";


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setFirstName("Karen");
        userEntity.setLastName("Kang");
        userEntity.setUserId(userId);
        userEntity.setEncryptPassword(encryptedPassword);
        userEntity.setEmail(email);
        userEntity.setEmailVerificationToken("diqdhiuwqhdiu");
        userEntity.setAddresses(getAddressesEntity());

    }

    @Test
    void should_ThrowUsernameNotFoundException_WhenEmailNotFound() {
        when(userRepository.findByEmail(anyString())).thenThrow(UsernameNotFoundException.class);

        Executable executable = () -> userService.getUser(email);

        assertThrows(UsernameNotFoundException.class, executable);
    }

    @Test
    void createUser_Should_ThrownUserServiceException_WhenFindEmail() {
        when(userRepository.findByEmail(anyString())).thenReturn(userEntity);

        UserDto userDto = new UserDto();
        userDto.setAddresses(getAddressesDto());
        userDto.setFirstName("Karen");
        userDto.setLastName("Kang");
        userDto.setPassword("12345678");
        userDto.setEmail(email);

        Executable executable = () -> userService.createUser(userDto);

        assertThrows(UserServiceException.class, executable);

    }

    @Test
    void should_ReturnCorrectUser_WhenGetUserByCorrectEmail() {
        when(userRepository.findByEmail(anyString())).thenReturn(userEntity);

        UserDto userDto = userService.getUser(email);

        assertAll(
                () -> assertEquals(userEntity.getId(), userDto.getId()),
                () -> assertEquals(userEntity.getFirstName(), userDto.getFirstName()),
                () -> assertEquals(userEntity.getEncryptPassword(), userDto.getEncryptPassword())
        );

    }

    @Test
    void should_ReturnCorrect_WhenCreateUser_WithOneAddress() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        when(utils.generateAddressId(anyInt())).thenReturn("hskqwshwkhswsh887");
        when(utils.generateUserId(anyInt())).thenReturn(userId);
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn(encryptedPassword);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        doNothing().when(amazonSES).verifyEmail(any());

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

    @Test
    void should_ReturnCorrect_WhenCreateUser_WithAllFieldsOf2AddressRequestModel() {

        when(userRepository.findByEmail(anyString())).thenReturn(null);
        when(utils.generateAddressId(anyInt())).thenReturn("hskqwshwkhswsh887");
        when(utils.generateUserId(anyInt())).thenReturn(userId);
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn(encryptedPassword);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        doNothing().when(amazonSES).verifyEmail(any());

        UserDto userDto = new UserDto();
        userDto.setAddresses(getAddressesDto());
        userDto.setFirstName("Karen");
        userDto.setLastName("Kang");
        userDto.setPassword("12345678");
        userDto.setEmail(email);

        UserDto storedUserDetails = userService.createUser(userDto);

        assertAll(
                () -> assertNotNull(storedUserDetails),
                () -> assertEquals(userEntity.getFirstName(), storedUserDetails.getFirstName()),
                () -> assertEquals(userEntity.getLastName(), storedUserDetails.getLastName()),
                () -> assertNotNull(storedUserDetails.getUserId()),
                () -> assertEquals(storedUserDetails.getAddresses().size(), userEntity.getAddresses().size()),
                () -> verify(utils, times(storedUserDetails.getAddresses().size())).generateAddressId(30),
                () -> verify(bCryptPasswordEncoder, times(1)).encode("12345678"),
                () -> verify(userRepository, times(1)).save(any())
        );

    }

    private List<AddressDTO> getAddressesDto() {
        AddressDTO shippingAddressDTO = new AddressDTO();
        shippingAddressDTO.setType("shipping");
        shippingAddressDTO.setCity("Fort Collins");
        shippingAddressDTO.setCountry("Colorado");
        shippingAddressDTO.setPostalCode("80521");
        shippingAddressDTO.setStreetName("1600 W Plum");

        AddressDTO billingAddressDTO = new AddressDTO();
        billingAddressDTO.setType("billing");
        billingAddressDTO.setCity("Fort Collins");
        billingAddressDTO.setCountry("Colorado");
        billingAddressDTO.setPostalCode("80521");
        billingAddressDTO.setStreetName("1600 W Plum");

        List<AddressDTO> addressDTOList = new ArrayList<>();
        addressDTOList.add(shippingAddressDTO);
        addressDTOList.add(billingAddressDTO);
        return addressDTOList;
    }

    private List<AddressEntity> getAddressesEntity() {
        List<AddressDTO> addressDTOList = getAddressesDto();
        Type listTye = new TypeToken<List<AddressEntity>>() {
        }.getType();
        return new ModelMapper().map(addressDTOList, listTye);
    }


}