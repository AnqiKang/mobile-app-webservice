package com.kang.app.ws.service.impl;

import com.kang.app.ws.entity.AddressEntity;
import com.kang.app.ws.entity.UserEntity;
import com.kang.app.ws.exceptions.UserServiceException;
import com.kang.app.ws.model.response.ErrorMessages;
import com.kang.app.ws.repository.UserRepository;
import com.kang.app.ws.service.UserService;
import com.kang.app.ws.shared.AddressDTO;
import com.kang.app.ws.shared.UserDto;
import com.kang.app.ws.shared.Utils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final Utils utils;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, Utils utils, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.utils = utils;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public UserDto createUser(UserDto user) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new RuntimeException("record already exists");
        }

        for (int i = 0; i < user.getAddresses().size(); i++) {
            AddressDTO addressDTO = user.getAddresses().get(i);
            addressDTO.setUserDetails(user);
            addressDTO.setAddressId(utils.generateAddressId(30));
            user.getAddresses().set(i, addressDTO);
        }

        ModelMapper modelMapper = new ModelMapper();
        UserEntity userEntity = modelMapper.map(user, UserEntity.class);

        String publicUserId = utils.generateUserId(30);
        userEntity.setUserId(publicUserId);
        userEntity.setEncryptPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userEntity.setEmailVerificationToken(utils.generateEmailVerificationToken(publicUserId));
        userEntity.setEmailVerificationStatus(false);

        UserEntity storedUserDetails = userRepository.save(userEntity);
        UserDto returnValue = modelMapper.map(storedUserDetails, UserDto.class);

        return returnValue;
    }

    @Override
    public UserDto getUser(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);
        if (userEntity == null) {
            throw new UsernameNotFoundException(email);
        }
        UserDto returnValue = new UserDto();
        BeanUtils.copyProperties(userEntity, returnValue);
        // UserDto returnValue = new ModelMapper().map(userEntity, UserDto.class);

        return returnValue;
    }

    @Override
    public UserDto getUserByUserId(String userId) {
        //UserDto userDto = new UserDto();
        UserEntity userEntity = userRepository.findByUserId(userId);
        if (userEntity == null) {
            throw new UsernameNotFoundException("User with ID " + userId + " Not Found");
        }
        //BeanUtils.copyProperties(userEntity, userDto);
        UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);
        return userDto;
    }

    @Override
    public UserDto updateUser(String id, UserDto userDto) {
        UserEntity userEntity = userRepository.findByUserId(id);
        if (userEntity == null) {
            throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        }

        userEntity.setFirstName(userDto.getFirstName());
        userEntity.setLastName(userDto.getLastName());
        UserEntity updatedUserEntity = userRepository.save(userEntity);
//        UserDto returnValue = new UserDto();
//        BeanUtils.copyProperties(updatedUserEntity, returnValue);
        UserDto returnValue = new ModelMapper().map(updatedUserEntity, UserDto.class);
        return returnValue;
    }

    @Override
    public void deleteUser(String id) {
        UserEntity userEntity = userRepository.findByUserId(id);
        if (userEntity == null) {
            throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        }
        userRepository.delete(userEntity);
    }

    @Override
    public List<UserDto> getUsers(int page, int limit) {
        List<UserDto> userDtoList = new ArrayList<>();

        if (page > 0) {
            page -= 1;
        }

        Pageable pageableResult = PageRequest.of(page, limit);
        Page<UserEntity> usersPage = userRepository.findAll(pageableResult);
        List<UserEntity> userEntityList = usersPage.getContent();

        for (UserEntity userEntity : userEntityList) {
//            UserDto userDto = new UserDto();
//            BeanUtils.copyProperties(userEntity, userDto);
            UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);
            userDtoList.add(userDto);
        }

        return userDtoList;
    }

    @Override
    public boolean verifyEmailToken(String token) {
        boolean isVerified = false;

        UserEntity userEntity = userRepository.findUserByEmailVerificationToken(token);
        if (userEntity != null) {
            boolean hasTokenExpired = Utils.hasTokenExpired(token);
            if (!hasTokenExpired) {
                userEntity.setEmailVerificationToken(null);
                userEntity.setEmailVerificationStatus(Boolean.TRUE);
                userRepository.save(userEntity);
                isVerified = true;
            }
        }
        return isVerified;
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email);
        if (userEntity == null) {
            throw new UsernameNotFoundException(email);
        }

        return new User(userEntity.getEmail(), userEntity.getEncryptPassword(), userEntity.getEmailVerificationStatus(),
                true, true, true, new ArrayList<>());
        //return new User(userEntity.getEmail(), userEntity.getEncryptPassword(), new ArrayList<>());

    }


}
