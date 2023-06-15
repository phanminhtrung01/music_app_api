package com.example.music_app_api.service.database_server.iml_service;

import com.example.music_app_api.dto.CommentDto;
import com.example.music_app_api.entity.Comment;
import com.example.music_app_api.entity.Song;
import com.example.music_app_api.entity.User;
import com.example.music_app_api.exception.NotFoundException;
import com.example.music_app_api.mix_in.IgnoreSongMixIn;
import com.example.music_app_api.mix_in.IgnoreUserMixIn;
import com.example.music_app_api.repo.CommentRepository;
import com.example.music_app_api.service.database_server.CommentService;
import com.example.music_app_api.service.database_server.SongService;
import com.example.music_app_api.service.database_server.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper = new ObjectMapper();

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
    public CommentDto saveComment(
            Comment comment, String idUser, String idSong) {
        try {
            User user = userService.getUserById(idUser);
            Song song = songService.getSong(idSong);

            comment.setUser(user);
            comment.setSong(song);
            commentRepository.save(comment);

            return objectMapper.convertValue(comment, CommentDto.class);
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    public CommentDto deleteComment(String idComment) {
        try {
            Comment comment = getCommentById(idComment);
            comment.setUser(null);
            comment.setUsersLike(null);

            commentRepository.delete(comment);

            return objectMapper.convertValue(comment, CommentDto.class);
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
    public List<CommentDto> getCommentsByUser(String idUser) {
        try {
            userService.getUserById(idUser);
            objectMapper.addMixIn(CommentDto.class, IgnoreUserMixIn.class);
            List<Comment> comments = commentRepository.getCommentsByUser(idUser);
            List<CommentDto> commentsDto = objectMapper.convertValue(comments, new TypeReference<>() {
            });
            objectMapper.addMixIn(CommentDto.class, null);
            return commentsDto;
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
    public List<CommentDto> getCommentsBySong(String idSong) {
        try {
            Song song = songService.getSong(idSong);
            objectMapper.addMixIn(CommentDto.class, IgnoreSongMixIn.class);
            List<Comment> comments = commentRepository.getCommentsBySong(song.getIdSong());
            List<CommentDto> commentsDto = objectMapper.convertValue(comments, new TypeReference<>() {
            });
            objectMapper.addMixIn(CommentDto.class, null);
            return commentsDto;
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
    public List<CommentDto> getCommentsByUserAndSong(
            String idUser, String idSong) {
        try {
            userService.getUserById(idUser);
            Song song = songService.getSong(idSong);
            List<Comment> comments = commentRepository
                    .getCommentsByUserAndSong(idUser, song.getIdSong());
            return objectMapper.convertValue(comments, new TypeReference<>() {
            });
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    public CommentDto addLikeComment(String idComment, String idUser) {
        try {
            User user = userService.getUserById(idUser);
            Comment comment = getCommentById(idComment);
            user.getCommentsLike().add(comment);
            comment.getUsersLike().add(user);
            commentRepository.save(comment);

            return objectMapper.convertValue(comment, CommentDto.class);
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    public CommentDto removeLikeComment(String idComment, String idUser) {
        try {
            User user = userService.getUserById(idUser);
            Comment comment = getCommentById(idComment);
            comment.getUsersLike().remove(user);
            commentRepository.save(comment);

            return objectMapper.convertValue(comment, CommentDto.class);
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
    public List<CommentDto> getLikeCommentsByUser(String idUser) {
        try {
            userService.getUserById(idUser);
            List<Comment> comments = commentRepository.getLikeCommentsByUser(idUser);
            return objectMapper.convertValue(comments, new TypeReference<>() {
            });
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }
}
