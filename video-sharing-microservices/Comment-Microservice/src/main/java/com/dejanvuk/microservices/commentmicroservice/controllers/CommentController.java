package com.dejanvuk.microservices.commentmicroservice.controllers;

import com.dejanvuk.microservices.api.comment.Comment;
import com.dejanvuk.microservices.commentmicroservice.payload.CommentPayload;
import com.dejanvuk.microservices.commentmicroservice.payload.UpdateCommentPayload;
import com.dejanvuk.microservices.commentmicroservice.services.ICommentService;
import com.mongodb.reactivestreams.client.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
public class CommentController {
    @Autowired
    ICommentService commentService;

    @PostMapping(path = "/comments", consumes = "application/json", produces = "application/json")
    Mono<?> createComment(@RequestBody CommentPayload commentPayload) {
        Mono<ResponseEntity<Map<String, String>>> errors = validateComment(commentPayload);

        if (errors != null) return errors;

        commentService.create(commentPayload);

        return Mono.just(new ResponseEntity<>("Comment created successfully!", HttpStatus.CREATED));
    }

    @DeleteMapping(value = "/comments/{commentId}", produces = "application/json")
    void deleteComment(@PathVariable String commentId) {
        commentService.deleteComment(commentId);
    }

    @GetMapping(value = "/comments/video/{videoId}", produces = "application/json")
    Flux<Comment> getVideosComment(@PathVariable String videoId){
        return commentService.getVideoComments(videoId);
    }

    @GetMapping(value = "/comments/user/{userId}", produces = "application/json")
    Flux<Comment> getUsersComment(@PathVariable String userId){
        return commentService.getOwnersComments(userId);
    }

    @PutMapping(value = "/comments/{commentId}")
    void updateComment(@PathVariable String commentId, @RequestBody UpdateCommentPayload updateCommentPayload){

        commentService.updateComment(commentId, updateCommentPayload.getEditedContent());
    }


    private Mono<ResponseEntity<Map<String, String>>> validateComment(CommentPayload commentPayload) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<CommentPayload>> violations = validator.validate(commentPayload);

        if (violations.size() == 0) return null;

        Map<String, String> errorMap = new HashMap<>();

        for (ConstraintViolation<CommentPayload> violation : violations) {
            errorMap.put(violation.getInvalidValue().toString(), violation.getMessage());
        }

        return Mono.just(new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST));
    }
}
