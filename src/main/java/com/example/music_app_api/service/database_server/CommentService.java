package com.example.music_app_api.service.database_server;

import com.example.music_app_api.entity.Comment;

import java.util.List;

public interface CommentService {
    Comment saveComment(Comment comment, String idUser, String idSong);

    Comment deleteComment(Long idComment);

    Comment getCommentById(Long idComment);

    Comment addLikeComment(Long idComment, String idUser);

    Comment removeLikeComment(Long idComment, String idUser);

    List<Comment> getLikeCommentsByUser(String idUser);

    List<Comment> getCommentsByUser(String idUser);

    List<Comment> getCommentsBySong(String idSong);

    List<Comment> getCommentsByUserAndSong(String idUser, String idSong);
}