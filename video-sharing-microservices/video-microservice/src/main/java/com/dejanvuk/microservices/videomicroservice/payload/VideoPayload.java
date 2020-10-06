package com.dejanvuk.microservices.videomicroservice.payload;

import com.dejanvuk.microservices.api.video.Tag;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Schema(description = "Class representing an uploaded video")
@Data
public class VideoPayload {
    @NotBlank
    @ApiModelProperty(notes = "The id of the user who uploaded the video", required = true, position = 0)
    private String ownerId;

    @NotBlank
    @ApiModelProperty(notes = "The name of the user who uploaded the video", required = true, position = 1)
    private String ownersName;

    @NotBlank
    @ApiModelProperty(notes = "The location where the video is hosted", required = true, position = 2)
    private String locationUrl;

    @Size(max = 64)
    @NotBlank
    @ApiModelProperty(notes = "Title of the video", required = true, position = 3)
    private String title;

    @Size(max = 256)
    @NotBlank
    @ApiModelProperty(notes = "Short video description of the video contents", required = true, position =4)
    private String description;

    @ApiModelProperty(notes = "A list of tags for the video", position = 5)
    private List<Tag> tags;
}
