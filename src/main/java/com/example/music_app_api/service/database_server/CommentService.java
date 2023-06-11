package com.example.music_app_api.service.database_server;

import com.example.music_app_api.dto.CommentDto;
import com.example.music_app_api.entity.Comment;

import java.util.List;

public interface CommentService {
    CommentDto saveComment(Comment comment, String idUser, String idSong);

    CommentDto deleteComment(String idComment);

    Comment getCommentById(String idComment);

    CommentDto addLikeComment(String idComment, String idUser);

    CommentDto removeLikeComment(String idComment, String idUser);

    List<CommentDto> getLikeCommentsByUser(String idUser);

    List<CommentDto> getCommentsByUser(String idUser);

    List<CommentDto> getCommentsBySong(String idSong);

    List<CommentDto> getCommentsByUserAndSong(String idUser, String idSong);
}
