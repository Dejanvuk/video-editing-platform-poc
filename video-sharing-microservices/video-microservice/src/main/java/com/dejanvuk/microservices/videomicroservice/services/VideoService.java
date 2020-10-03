package com.dejanvuk.microservices.videomicroservice.services;

import com.dejanvuk.microservices.videomicroservice.exceptions.InvalidVideoIdException;
import com.dejanvuk.microservices.videomicroservice.exceptions.VideoNotFoundException;
import com.dejanvuk.microservices.videomicroservice.mappers.VideoMapper;
import com.dejanvuk.microservices.videomicroservice.payload.VideoPayload;
import com.dejanvuk.microservices.videomicroservice.persistence.VideoEntity;
import com.dejanvuk.microservices.videomicroservice.persistence.VideoRepository;
import com.dejanvuk.microservices.videomicroservice.response.Video;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class VideoService implements IVideoService {

    @Autowired
    VideoRepository videoRepository;

    @Autowired
    VideoMapper videoMapper;

    @Override
    public void create(VideoPayload videoPayload) {
        VideoEntity videoEntity = new VideoEntity();
        videoEntity.setOwnerId(videoPayload.getOwnerId());
        videoEntity.setLocationUrl(videoPayload.getLocationUrl());
        videoEntity.setTitle(videoPayload.getTitle());
        videoEntity.setDescription(videoPayload.getDescription());
        videoEntity.setTags(videoPayload.getTags());
        videoEntity.setLikes(0);
        videoEntity.setDislikes(0);

        videoRepository.save(videoEntity).log().block();
    }

    @Override
    public Mono<Video> getVideo(String videoId) {
        if(Integer.parseInt(videoId) < 1) throw new InvalidVideoIdException(videoId);

        return videoRepository.findById(videoId).log().switchIfEmpty(Mono.error(new VideoNotFoundException(videoId)))
                .map(videoEntity -> videoMapper.videoEntityToApi(videoEntity));
    }

    @Override
    public void deleteVideo(String videoId) {
        if(Integer.parseInt(videoId) < 1) throw new InvalidVideoIdException(videoId);

        videoRepository.deleteById(videoId).log().block();
    }

}
