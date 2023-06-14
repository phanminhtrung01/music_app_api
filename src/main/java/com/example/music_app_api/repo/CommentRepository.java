package com.example.music_app_api.repo;

import com.example.music_app_api.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {
    @Query(
            value = """
                    SELECT c FROM Comment c
                    JOIN FETCH c.user u
                    JOIN FETCH c.song s
                    WHERE u.idUser = ?1 AND s.idSong = ?2
                    """)
    @Transactional(readOnly = true)
    List<Comment> getCommentsByUserAndSong(String idUser, String idSong);

    @Query(
            value = """
                    SELECT c FROM Comment c
                    JOIN FETCH c.user u
                    WHERE u.idUser = ?1
                    """)
    @Transactional(readOnly = true)
    List<Comment> getCommentsByUser(String idUser);

    @Query(
            value = """
                    SELECT c FROM Comment c
                    JOIN FETCH c.song s
                    WHERE s.idSong = ?1
                    ORDER BY c.date DESC
                    """)
    @Transactional(readOnly = true)
    List<Comment> getCommentsBySong(String idSong);

    @Query(
            value = """
                    SELECT c FROM Comment c
                    JOIN FETCH c.usersLike u
                    WHERE u.idUser =?1
                    """
    )
    @Transactional(readOnly = true)
    List<Comment> getLikeCommentsByUser(String idUser);
}
