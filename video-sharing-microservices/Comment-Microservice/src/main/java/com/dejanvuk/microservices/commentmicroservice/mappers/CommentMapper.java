package com.dejanvuk.microservices.commentmicroservice.mappers;

import com.dejanvuk.microservices.api.comment.Comment;
import com.dejanvuk.microservices.commentmicroservice.persistence.CommentEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    Comment commentEntityToComment(CommentEntity commentEntity);
}
