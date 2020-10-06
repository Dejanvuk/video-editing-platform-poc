package com.dejanvuk.microservices.videomicroservice.services;

import com.dejanvuk.microservices.videomicroservice.exceptions.InvalidVideoIdException;
import com.dejanvuk.microservices.videomicroservice.exceptions.VideoNotFoundException;
import com.dejanvuk.microservices.videomicroservice.mappers.VideoMapper;
import com.dejanvuk.microservices.videomicroservice.payload.UpdateVideoPayload;
import com.dejanvuk.microservices.videomicroservice.payload.VideoPayload;
import com.dejanvuk.microservices.videomicroservice.persistence.VideoEntity;
import com.dejanvuk.microservices.videomicroservice.persistence.VideoRepository;
import com.dejanvuk.microservices.api.video.Video;
import com.dejanvuk.microservices.api.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@Service
public class VideoService implements IVideoService {

    private static final Logger log = LoggerFactory.getLogger(VideoService.class);

    @Autowired
    VideoRepository videoRepository;

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

    @Autowired
    VideoMapper videoMapper;

    private final String userServiceUrl = "http://user-service";

    private WebClient webClient = WebClient.create(userServiceUrl);

    public VideoService() {
    }

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
    public Flux<Video> getOwnerVideos(String ownerId) {
        if (Integer.parseInt(ownerId) < 1) throw new InvalidVideoIdException(ownerId);

        return videoRepository.findAllByOwnerId(ownerId).log().log()
                .map(videoEntity -> videoMapper.videoEntityToApi(videoEntity));
    }

    @Override
    public Flux<Video> getVideos() {
        return videoRepository.findAll().map(videoEntity -> videoMapper.videoEntityToApi(videoEntity));
    }

    @Override
    public void deleteVideo(String videoId) {
        if (Integer.parseInt(videoId) < 1) throw new InvalidVideoIdException(videoId);

        videoRepository.deleteById(videoId).log().block();
    }

    @Override
    public void updateVideo(String videoId, UpdateVideoPayload updateVideoPayload) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(videoId));
        Update update = new Update();
        if(!updateVideoPayload.getNewTitle().isEmpty())
            update.set("title", updateVideoPayload.getNewTitle());

        if(!updateVideoPayload.getDescription().isEmpty())
            update.set("description", updateVideoPayload.getDescription());

        reactiveMongoTemplate.updateFirst(query, update, VideoEntity.class).log().block();
    }

}
