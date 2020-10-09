package com.dejanvuk.microservices.commentmicroservice.message;

import com.dejanvuk.microservices.api.comment.Comment;
import com.dejanvuk.microservices.api.event.Event;
import com.dejanvuk.microservices.api.exceptions.InvalidEventException;
import com.dejanvuk.microservices.commentmicroservice.payload.CommentPayload;
import com.dejanvuk.microservices.commentmicroservice.services.ICommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;

@EnableBinding(Sink.class)
public class MessageProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageProcessor.class);

    @Autowired
    ICommentService commentService;

    @StreamListener(target = Sink.INPUT)
    public void process(Event<String, CommentPayload> event) {
        LOGGER.info("Received new event!\nEvent created at {} ", event.getCreationDate());

        switch (event.getEventType()) {
            case CREATE:
                commentService.create(event.getData());
                break;
            case DELETE:
                commentService.deleteComment(event.getKey());
                break;
            case UPDATE:
                commentService.updateComment(event.getKey(), event.getData().getContent());
                break;
            default:
                throw new InvalidEventException("Invalid event id: " + event.getEventType());
        }
    }
}
