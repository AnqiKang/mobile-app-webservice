package com.kang.app.ws.controller;

import com.kang.app.ws.model.response.UserRest;
import com.kang.app.ws.service.UserService;
import com.kang.app.ws.shared.AddressDTO;
import com.kang.app.ws.shared.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class UserControllerTest extends Object {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    private UserDto userDto;
    private final String USER_ID = "sdferferf";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userDto = new UserDto();
        userDto.setFirstName("Karen");
        userDto.setLastName("Kang");
        userDto.setEmail("test@test.com");
        userDto.setEmailVerificationStatus(Boolean.FALSE);
        userDto.setEmailVerificationToken(null);
        userDto.setUserId(USER_ID);
        userDto.setAddresses(getAddressesDto());
        userDto.setEncryptPassword("sdfkji23hr");

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

    @Test
    void getUser_ShouldNotNull_WhenProvideUserId() {
        when(userService.getUserByUserId(anyString())).thenReturn(userDto);

        UserRest userRest = userController.getUser(USER_ID);

        assertNotNull(userRest);
    }

    @Test
    void getUser_ShouldReturnSameId_WhenProvideUserId() {
        when(userService.getUserByUserId(anyString())).thenReturn(userDto);
        UserRest userRest = userController.getUser(USER_ID);
        assertEquals(USER_ID, userRest.getUserId());
    }

    @Test
    void getUser_ShouldReturnSameName_WhenProvideUserId() {
        when(userService.getUserByUserId(anyString())).thenReturn(userDto);
        UserRest userRest = userController.getUser(USER_ID);
        assertAll(
                () -> assertEquals(userDto.getFirstName(), userRest.getFirstName()),
                () -> assertEquals(userDto.getLastName(), userRest.getLastName())
        );
    }

    @Test
    void getUser_ShouldReturnSameAddressesSize_WhenProvideUserId() {
        when(userService.getUserByUserId(anyString())).thenReturn(userDto);
        UserRest userRest = userController.getUser(USER_ID);
        assertEquals(userDto.getAddresses().size(), userRest.getAddresses().size());
    }
}