package com.kang.app.ws.controller;

import com.kang.app.ws.exceptions.UserServiceException;
import com.kang.app.ws.model.request.UserDetailsRequestModel;
import com.kang.app.ws.model.response.*;
import com.kang.app.ws.service.UserService;
import com.kang.app.ws.shared.UserDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.awt.*;

@RestController
@RequestMapping("users") // http://localhost:8080/users
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping(value = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserRest getUser(@PathVariable String id) {
        UserRest userRest = new UserRest();

        UserDto userDto = userService.getUserByUserId(id);
        BeanUtils.copyProperties(userDto, userRest);

        return userRest;
    }

    @PostMapping(
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception {
        UserRest userRest = new UserRest();

        if (userDetails.getFirstName().isEmpty()) {
            throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FILED.getErrorMessage());
        }

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetails, userDto);

        UserDto createdUser = userService.createUser(userDto);
        BeanUtils.copyProperties(createdUser, userRest);

        return userRest;
    }

    @PutMapping(value = "/{id}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public UserRest updateUser(@PathVariable String id,
                               @RequestBody UserDetailsRequestModel userDetailsRequestModel) {
        UserRest userRest = new UserRest();
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetailsRequestModel, userDto);

        UserDto updatedUser = userService.updateUser(id, userDto);
        BeanUtils.copyProperties(updatedUser, userRest);

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
}
