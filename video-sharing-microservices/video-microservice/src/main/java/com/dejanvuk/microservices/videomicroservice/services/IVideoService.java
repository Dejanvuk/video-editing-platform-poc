package com.dejanvuk.microservices.videomicroservice.services;

import com.dejanvuk.microservices.videomicroservice.payload.VideoPayload;
import com.dejanvuk.microservices.videomicroservice.persistence.VideoEntity;
import com.dejanvuk.microservices.videomicroservice.response.Video;
import reactor.core.publisher.Mono;

public interface IVideoService {
    void create(VideoPayload videoPayload);

    Mono<Video> getVideo(String videoId);

    void deleteVideo(String videoId);
}
