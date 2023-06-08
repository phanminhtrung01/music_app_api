package com.example.music_app_api.service.database_server.iml_service;

import com.example.music_app_api.entity.Comment;
import com.example.music_app_api.entity.Song;
import com.example.music_app_api.entity.User;
import com.example.music_app_api.exception.NotFoundException;
import com.example.music_app_api.repo.CommentRepository;
import com.example.music_app_api.service.database_server.CommentService;
import com.example.music_app_api.service.database_server.SongService;
import com.example.music_app_api.service.database_server.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserService userService;
    private final SongService songService;

    @Autowired
    public CommentServiceImpl(
            CommentRepository commentRepository,
            UserService userService,
            SongService songService) {
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.songService = songService;
    }

    @Override
    @Transactional
    public Comment saveComment(
            Comment comment, String idUser, String idSong) {
        try {
            User user = userService.getUserById(idUser);
            Song song = songService.getSong(idSong);

            comment.setUser(user);
            comment.setSong(song);
            commentRepository.save(comment);

            return comment;
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    public Comment deleteComment(String idComment) {
        try {
            Comment comment = getCommentById(idComment);
            comment.setUser(null);
            comment.setUsersLike(null);

            commentRepository.delete(comment);

            return comment;
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    public Comment getCommentById(String idComment) {
        try {
            Optional<Comment> commentOptional = commentRepository.findById(idComment);
            if (commentOptional.isPresent()) {
                return commentOptional.get();
            } else {
                throw new NotFoundException("Not fount comment with ID: " + idComment);
            }
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    public List<Comment> getCommentsByUser(String idUser) {
        try {
            userService.getUserById(idUser);
            return commentRepository.getCommentsByUser(idUser);
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    public List<Comment> getCommentsBySong(String idSong) {
        try {
            songService.getSong(idSong);
            return commentRepository.getCommentsBySong(idSong);
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    public List<Comment> getCommentsByUserAndSong(
            String idUser, String idSong) {
        try {
            userService.getUserById(idUser);
            songService.getSong(idSong);
            return commentRepository.getCommentsByUserAndSong(idUser, idSong);
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    public Comment addLikeComment(String idComment, String idUser) {
        try {
            User user = userService.getUserById(idUser);
            Comment comment = getCommentById(idComment);
            user.getCommentsLike().add(comment);
            comment.getUsersLike().add(user);
            commentRepository.save(comment);

            return comment;
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    public Comment removeLikeComment(String idComment, String idUser) {
        try {
            User user = userService.getUserById(idUser);
            Comment comment = getCommentById(idComment);
            comment.getUsersLike().remove(user);
            commentRepository.save(comment);

            return comment;
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    public List<Comment> getLikeCommentsByUser(String idUser) {
        try {
            userService.getUserById(idUser);

            return commentRepository.getLikeCommentsByUser(idUser);
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }
}
