package com.dejanvuk.microservices.user.mappers;

import com.dejanvuk.microservices.user.persistence.UserEntity;
import com.dejanvuk.microservices.user.response.UserResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserEntityMapper {
    UserResponse userEntityToUserResponse(UserEntity userEntity);
}
