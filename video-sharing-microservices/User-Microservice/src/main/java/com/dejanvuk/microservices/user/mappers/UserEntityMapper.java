package com.dejanvuk.microservices.user.mappers;

import com.dejanvuk.microservices.api.user.User;
import com.dejanvuk.microservices.user.persistence.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserEntityMapper {
    User userEntityToUserResponse(UserEntity userEntity);
}
