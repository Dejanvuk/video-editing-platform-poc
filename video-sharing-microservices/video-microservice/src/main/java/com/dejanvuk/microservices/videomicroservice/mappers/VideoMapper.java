package com.dejanvuk.microservices.videomicroservice.mappers;

import com.dejanvuk.microservices.videomicroservice.persistence.VideoEntity;
import com.dejanvuk.microservices.api.video.Video;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface VideoMapper {
    Video videoEntityToApi(VideoEntity videoEntity);
}
