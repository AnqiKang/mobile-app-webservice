package com.kang.app.ws.repository;

import com.kang.app.ws.entity.AddressEntity;
import com.kang.app.ws.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends CrudRepository<AddressEntity, Long> {

    List<AddressEntity> findAllByUserDetails(UserEntity userEntity);

}
