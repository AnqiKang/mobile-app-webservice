package com.kang.app.ws.controller;

import com.kang.app.ws.exceptions.UserServiceException;
import com.kang.app.ws.model.request.UserDetailsRequestModel;
import com.kang.app.ws.model.response.*;
import com.kang.app.ws.service.AddressService;
import com.kang.app.ws.service.UserService;
import com.kang.app.ws.shared.AddressDTO;
import com.kang.app.ws.shared.UserDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("users") // http://localhost:8080/users
public class UserController {
    private final UserService userService;
    private final AddressService addressService;

    @Autowired
    public UserController(UserService userService, AddressService addressService) {
        this.userService = userService;
        this.addressService = addressService;
    }

    @GetMapping(
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public List<UserRest> getUsers(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "limit", defaultValue = "2") int limit) {

        List<UserRest> userRestList = new ArrayList<>();
        List<UserDto> userDtoList = userService.getUsers(page, limit);

        // convert DTO to UserRest
        for (UserDto userDto : userDtoList) {
//            UserRest userRest = new UserRest();
//            BeanUtils.copyProperties(userDto, userRest);
            UserRest userRest = new ModelMapper().map(userDto, UserRest.class);
            userRestList.add(userRest);
        }

        return userRestList;

    }


    @GetMapping(value = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserRest getUser(@PathVariable String id) {
        //UserRest userRest = new UserRest();

        ModelMapper modelMapper = new ModelMapper();
        UserDto userDto = userService.getUserByUserId(id);
        UserRest userRest = modelMapper.map(userDto, UserRest.class);
        //BeanUtils.copyProperties(userDto, userRest);

        return userRest;
    }

    @PostMapping(
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception {
        if (userDetails.getFirstName().isEmpty()) {
            throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FILED.getErrorMessage());
        }
//        UserDto userDto = new UserDto();
//        BeanUtils.copyProperties(userDetails, userDto);
        ModelMapper modelMapper = new ModelMapper();
        UserDto userDto = modelMapper.map(userDetails, UserDto.class);

        UserDto createdUser = userService.createUser(userDto);
        UserRest userRest = modelMapper.map(createdUser, UserRest.class);

        return userRest;
    }

    @PutMapping(value = "/{id}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public UserRest updateUser(@PathVariable String id,
                               @RequestBody UserDetailsRequestModel userDetailsRequestModel) {

        UserDto userDto = new ModelMapper().map(userDetailsRequestModel, UserDto.class);

        UserDto updatedUser = userService.updateUser(id, userDto);
        UserRest userRest = new ModelMapper().map(updatedUser, UserRest.class);

        return userRest;
    }

    @DeleteMapping(value = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public OperationStatusModel deleteUser(@PathVariable String id) {
        OperationStatusModel operationStatusModel = new OperationStatusModel();

        operationStatusModel.setOperationName(RequestOperationName.DELETE.name());
        userService.deleteUser(id);
        operationStatusModel.setOperationResult(RequestOperationStatus.SUCCESS.name());

        return operationStatusModel;
    }

    @GetMapping(value = "/{id}/addresses",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<AddressRest> getUserAddresses(@PathVariable String id) {
//        UserDto userDto = userService.getUserByUserId(id);
//        List<AddressDTO> addressDTOList = userDto.getAddresses();
//        List<AddressRest> addresses = new ArrayList<>();
//
//        for (AddressDTO addressDTO : addressDTOList) {
//            addresses.add(new ModelMapper().map(addressDTO, AddressRest.class));
//        }
//        return addresses;
        List<AddressRest> addresses = new ArrayList<>();
        List<AddressDTO> addressDTOList = addressService.getAddresses(id);

        if (addressDTOList != null && !addressDTOList.isEmpty()) {
            Type listType = new TypeToken<List<AddressRest>>() {
            }.getType();
            addresses = new ModelMapper().map(addressDTOList, listType);
        }

        return addresses;

    }


}
