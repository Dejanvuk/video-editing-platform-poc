package com.dejanvuk.microservices.videomicroservice.services;

import com.dejanvuk.microservices.api.comment.Comment;
import com.dejanvuk.microservices.videomicroservice.payload.VideoPayload;
import com.dejanvuk.microservices.api.video.Video;
import reactor.core.publisher.Flux;

public interface IVideoService {
    void create(VideoPayload videoPayload);

    Flux<Video> getOwnerVideos(String ownerId);

    Flux<Video> getVideos();

    void deleteVideo(String videoId);

    void updateVideo(String videoId, VideoPayload videoPayload);

    Flux<Comment> getVideoComments(String videoId);

}
