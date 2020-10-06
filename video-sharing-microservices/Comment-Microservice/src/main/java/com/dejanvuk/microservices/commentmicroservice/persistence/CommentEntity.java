package com.dejanvuk.microservices.commentmicroservice.persistence;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "comments")
public class CommentEntity {
    @Setter(AccessLevel.NONE)
    @Id
    private String id;

    private String videoId; // the id of the video where the comment was posted

    private String ownerId; // the id of the original poster of the comment

    private String content;

    private int likes;

    private int dislikes;

    private int nrOfReports;

    @Version
    private Integer version;

    @JsonFormat(pattern = "yyyy-mm-dd")
    private Date created_At;

    @CreatedDate
    private void onCreate() {
        created_At = new Date();
    }
}
