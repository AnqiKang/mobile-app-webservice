package com.kang.app.ws.service;

import com.kang.app.ws.shared.AddressDTO;

import java.util.List;

public interface AddressService {

    // return a list of addresses
    List<AddressDTO> getAddresses(String userId);
}
