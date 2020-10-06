package com.dejanvuk.microservices.videomicroservice.controller;

import com.dejanvuk.microservices.api.comment.Comment;
import com.dejanvuk.microservices.api.video.Video;
import com.dejanvuk.microservices.videomicroservice.payload.UpdateVideoPayload;
import com.dejanvuk.microservices.videomicroservice.payload.VideoPayload;
import com.dejanvuk.microservices.videomicroservice.services.VideoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@Tag(name = "REST API for videos")
public class VideoController {

    @Autowired
    VideoService videoService;

    private final String commentServiceUrl = "http://comments-service";

    @PostMapping(path = "/videos", consumes = "application/json", produces = "application/json")
    Mono<?> createVideo(@RequestBody VideoPayload videoPayload) {
        Mono<ResponseEntity<Map<String, String>>> errors = validateVideo(videoPayload);

        if (errors != null) return errors;

        videoService.create(videoPayload);

        return Mono.just(new ResponseEntity<>("Video created successfully!", HttpStatus.CREATED));
    }

    @DeleteMapping(value = "/videos/{videoId}", produces = "application/json")
    void deleteVideo(@PathVariable String videoId) {
        videoService.deleteVideo(videoId);
    }

    @PutMapping(value = "/videos/{videoId}")
    void updateVideo(@PathVariable String videoId, @RequestBody UpdateVideoPayload updateVideoPayload) {
        videoService.updateVideo(videoId, updateVideoPayload);
    }

    @GetMapping(value = "/videos/user/{userId}", produces = "application/json")
    Flux<Video> getUsersVideos(@PathVariable String userId) {
        return videoService.getOwnerVideos(userId);
    }

    @GetMapping(value = "/videos", produces = "application/json")
    Flux<Video> getVideos() {
        return videoService.getVideos();
    }

    @GetMapping(value = "/videos/{userId}/comments", produces = "application/json")
    Flux<Comment> getVideosComments(@PathVariable String videoId) {
        WebClient webClient = WebClient.create(commentServiceUrl);
        return webClient.get().uri("/comments/video/" + videoId).retrieve().bodyToFlux(Comment.class).log().onErrorResume(error -> Flux.empty());
    }

    private Mono<ResponseEntity<Map<String, String>>> validateVideo(VideoPayload videoPayload) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<VideoPayload>> violations = validator.validate(videoPayload);

        if (violations.size() == 0) return null;

        Map<String, String> errorMap = new HashMap<>();

        for (ConstraintViolation<VideoPayload> violation : violations) {
            errorMap.put(violation.getInvalidValue().toString(), violation.getMessage());
        }

        return Mono.just(new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST));
    }
}
