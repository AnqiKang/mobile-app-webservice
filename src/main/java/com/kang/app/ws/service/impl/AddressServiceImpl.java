package com.kang.app.ws.service.impl;

import com.kang.app.ws.entity.AddressEntity;
import com.kang.app.ws.entity.UserEntity;
import com.kang.app.ws.repository.AddressRepository;
import com.kang.app.ws.repository.UserRepository;
import com.kang.app.ws.service.AddressService;
import com.kang.app.ws.shared.AddressDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    @Autowired
    public AddressServiceImpl(UserRepository userRepository, AddressRepository addressRepository) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
    }

    @Override
    public List<AddressDTO> getAddresses(String userId) {
        List<AddressDTO> addresses = new ArrayList<>();
        UserEntity userEntity = userRepository.findByUserId(userId);
        if (userEntity == null) {
            return addresses;
        }
        Iterable<AddressEntity> addressEntities = addressRepository.findAllByUserDetails(userEntity);
        for (AddressEntity addressEntity : addressEntities) {
            addresses.add(new ModelMapper().map(addressEntity, AddressDTO.class));
        }

        return addresses;
    }

    @Override
    public AddressDTO getAddress(String addressId) {
        AddressDTO addressDTO = new AddressDTO();
        AddressEntity addressEntity = addressRepository.findByAddressId(addressId);
        if (addressEntity != null) {
            addressDTO = new ModelMapper().map(addressEntity, AddressDTO.class);
        }
        return addressDTO;
    }


}
