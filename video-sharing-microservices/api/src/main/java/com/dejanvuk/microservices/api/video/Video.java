package com.dejanvuk.microservices.api.video;

import lombok.Data;

import java.util.List;

@Data
public class Video {
    private String id;

    private String locationUrl;

    private String title;

    private String description;

    private List<Tag> tags;

    private int likes;

    private int dislikes;
}
