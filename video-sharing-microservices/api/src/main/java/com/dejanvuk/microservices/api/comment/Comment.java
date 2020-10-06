package com.dejanvuk.microservices.api.comment;

import lombok.Data;

@Data
public class Comment {
    private String videoId; // the id of the video where the comment was posted

    private String ownerId; // the id of the original poster of the comment

    private String content;
}
