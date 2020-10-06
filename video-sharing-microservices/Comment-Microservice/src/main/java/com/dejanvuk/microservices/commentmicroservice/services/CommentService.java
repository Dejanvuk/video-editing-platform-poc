package com.dejanvuk.microservices.commentmicroservice.services;

import com.dejanvuk.microservices.api.comment.Comment;
import com.dejanvuk.microservices.commentmicroservice.payload.CommentPayload;
import reactor.core.publisher.Flux;

public interface CommentService {
    void create(CommentPayload commentPayload);

    Flux<Comment> getOwnersComments(String ownerId);

    Flux<Comment> getVideoComments(String videoId);

    void deleteComment(String commentId);

    void updateComment(String commentId, String newContent);
}
