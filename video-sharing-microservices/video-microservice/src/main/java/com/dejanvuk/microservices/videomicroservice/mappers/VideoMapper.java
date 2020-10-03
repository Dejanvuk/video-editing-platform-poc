package com.dejanvuk.microservices.videomicroservice.mappers;

import com.dejanvuk.microservices.videomicroservice.persistence.VideoEntity;
import com.dejanvuk.microservices.videomicroservice.response.Video;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VideoMapper {

    Video videoEntityToApi(VideoEntity videoEntity);
}
