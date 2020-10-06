package com.dejanvuk.microservices.commentmicroservice.services;

import com.dejanvuk.microservices.api.comment.Comment;
import com.dejanvuk.microservices.commentmicroservice.exceptions.InvalidCommentIdException;
import com.dejanvuk.microservices.commentmicroservice.mappers.CommentMapper;
import com.dejanvuk.microservices.commentmicroservice.payload.CommentPayload;
import com.dejanvuk.microservices.commentmicroservice.persistence.CommentEntity;
import com.dejanvuk.microservices.commentmicroservice.persistence.CommentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ICommentService implements CommentService {

    private static final Logger log = LoggerFactory.getLogger(ICommentService.class);

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

    @Autowired
    CommentMapper commentMapper;

    @Override
    public void create(CommentPayload commentPayload) {
        CommentEntity commentEntity = new CommentEntity();
        commentEntity.setOwnerId(commentPayload.getOwnerId());
        commentEntity.setContent(commentPayload.getContent());
        commentEntity.setVideoId(commentPayload.getVideoId());
        commentEntity.setLikes(0);
        commentEntity.setDislikes(0);
        commentEntity.setNrOfReports(0);

        log.debug("Creating comment: {} " , commentEntity);

        commentRepository.save(commentEntity).log().block();
    }

    @Override
    public Flux<Comment> getOwnersComments(String ownerId) {
        return commentRepository.findAllByOwnerId(ownerId).log().map(commentEntity -> commentMapper.commentEntityToComment(commentEntity));
    }

    @Override
    public Flux<Comment> getVideoComments(String videoId) {
        return commentRepository.findAllByVideoId(videoId).log().map(commentEntity -> commentMapper.commentEntityToComment(commentEntity));
    }

    @Override
    public void deleteComment(String commentId) {
        if (Integer.parseInt(commentId) < 1) throw new InvalidCommentIdException(commentId);

        log.debug("Deleting comment with id: {} " , commentId);

        commentRepository.deleteById(commentId).log().block();
    }

    @Override
    public void updateComment(String commentId, String newContent) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(commentId));
        Update update = new Update();
        update.set("content", newContent);
        reactiveMongoTemplate.updateFirst(query, update, CommentEntity.class).log().block();
    }
}
