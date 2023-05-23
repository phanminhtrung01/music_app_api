package com.example.music_app_api.repo;

import com.example.music_app_api.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query(
            value = "select * from comment " +
                    "where id_user=?1 and id_song=?2",
            nativeQuery = true)
    List<Comment> getCommentsByUserAndSong(String idUser, String idSong);

    @Query(
            value = "select * from comment " +
                    "where id_user=?1", nativeQuery = true)
    List<Comment> getCommentsByUser(String idUser);

    @Query(
            value = "select * from comment " +
                    "where id_song=?1",
            nativeQuery = true)
    List<Comment> getCommentsBySong(String idSong);

    @Query(
            value = """
                    select * from comment
                    where exists(select id_comment from comment_like
                    where id_user = ?1
                    and comment.id_comment = comment_like.id_comment)
                    """, nativeQuery = true
    )
    List<Comment> getLikeCommentsByUser(String idUser);
}
