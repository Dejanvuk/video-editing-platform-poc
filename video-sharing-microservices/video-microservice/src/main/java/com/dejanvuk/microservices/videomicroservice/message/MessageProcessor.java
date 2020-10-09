package com.dejanvuk.microservices.videomicroservice.message;

import com.dejanvuk.microservices.api.event.Event;
import com.dejanvuk.microservices.api.exceptions.InvalidEventException;
import com.dejanvuk.microservices.videomicroservice.payload.VideoPayload;
import com.dejanvuk.microservices.videomicroservice.services.VideoService;
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
    VideoService videoService;

    @StreamListener(target = Sink.INPUT)
    public void process(Event<String, VideoPayload> event) {
        LOGGER.info("Received new event!\nEvent created at {} ", event.getCreationDate());

        switch (event.getEventType()) {
            case CREATE:
                videoService.create(event.getData());
                break;
            case DELETE:
                videoService.deleteVideo(event.getKey());
                break;
            case UPDATE:
                videoService.updateVideo(event.getKey(), event.getData());
                break;
            default:
                throw new InvalidEventException("Invalid event id: " + event.getEventType());
        }
    }


}
