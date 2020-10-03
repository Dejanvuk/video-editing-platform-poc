package com.dejanvuk.microservices.videomicroservice.response;

import com.dejanvuk.microservices.videomicroservice.persistence.Tag;
import lombok.Data;

import java.util.List;

@Data
public class Video {
    private String id;

    private String locationUrl;

    private String ownerName;

    private String title;

    private String description;

    private List<Tag> tags;

    private int likes;

    private int dislikes;
}
