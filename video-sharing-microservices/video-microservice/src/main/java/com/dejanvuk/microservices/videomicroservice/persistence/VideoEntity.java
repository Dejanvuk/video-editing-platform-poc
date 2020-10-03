package com.dejanvuk.microservices.videomicroservice.persistence;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@Data
@Document(collection="videos")
public class VideoEntity {
    @Setter(AccessLevel.NONE)
    @Id
    private String id;

    private String locationUrl;

    private String ownerId; // id of the user who uploaded the video

    @Size(max = 64)
    @NotBlank
    private String title;

    @Size(max = 256)
    @NotBlank
    private String description;

    private List<Tag> tags;

    private int likes;

    private int dislikes;

    @Version
    private Integer version;

    @JsonFormat(pattern = "yyyy-mm-dd")
    private Date created_At;

    @CreatedDate
    private void onCreate() {
        created_At = new Date();
    }

}
