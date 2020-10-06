package com.dejanvuk.microservices.videomicroservice.services;

import com.dejanvuk.microservices.videomicroservice.payload.UpdateVideoPayload;
import com.dejanvuk.microservices.videomicroservice.payload.VideoPayload;
import com.dejanvuk.microservices.api.video.Video;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IVideoService {
    void create(VideoPayload videoPayload);

    Flux<Video> getOwnerVideos(String ownerId);

    Flux<Video> getVideos();

    void deleteVideo(String videoId);

    void updateVideo(String videoId, UpdateVideoPayload updateVideoPayload);


}
