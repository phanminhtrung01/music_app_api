package com.example.music_app_api.controller.database;

import com.example.music_app_api.entity.Comment;
import com.example.music_app_api.exception.NotFoundException;
import com.example.music_app_api.model.ResponseObject;
import com.example.music_app_api.service.database_server.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("http://localhost:8080")
@RequestMapping("/pmdv/db/comment/")
public class CommentController {
    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {

        this.commentService = commentService;
    }


    @PostMapping("add")
    public ResponseEntity<ResponseObject> addComment(
            @RequestParam("idUser") String idUser,
            @RequestParam("idSong") String idSong,
            @RequestParam("value") String value) {
        try {
            Comment comment = new Comment();
            comment.setValue(value);
            Comment commentPar = commentService.saveComment(comment, idUser, idSong);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new ResponseObject(
                            HttpStatus.CREATED.value(),
                            "Query add comment successful!",
                            commentPar));
        } catch (NotFoundException notFoundException) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject(
                            HttpStatus.NOT_FOUND.value(),
                            notFoundException.getMessage(),
                            null));
        } catch (RuntimeException runtimeException) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            runtimeException.getMessage(),
                            null));
        }
    }

    @DeleteMapping("delete")
    public ResponseEntity<ResponseObject> deleteComment(
            @RequestParam("idComment") String idComment) {
        try {
            Comment comment = commentService.deleteComment(idComment);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "Query remove comment successful!",
                            comment));
        } catch (NotFoundException notFoundException) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject(
                            HttpStatus.NOT_FOUND.value(),
                            notFoundException.getMessage(),
                            null));
        } catch (RuntimeException runtimeException) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            runtimeException.getMessage(),
                            null));
        }
    }

    @GetMapping("get/comments_by_user")
    public ResponseEntity<ResponseObject> getCommentsByUser(
            @RequestParam("idUser") String idUser) {

        try {
            List<Comment> comments = commentService.getCommentsByUser(idUser);

            if (!comments.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(new ResponseObject(
                                HttpStatus.OK.value(),
                                "Query get comments by user successful!",
                                comments));
            }

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "List empty!",
                            comments));
        } catch (NotFoundException notFoundException) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject(
                            HttpStatus.NOT_FOUND.value(),
                            notFoundException.getMessage(),
                            null));
        } catch (RuntimeException runtimeException) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            runtimeException.getMessage(),
                            null));
        }

    }

    @GetMapping("get/comments_by_song")
    public ResponseEntity<ResponseObject> getCommentsBySong(
            @RequestParam("idSong") String idSong) {

        try {
            List<Comment> comments = commentService.getCommentsBySong(idSong);

            if (!comments.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(new ResponseObject(
                                HttpStatus.OK.value(),
                                "Query get comments by song successful!",
                                comments));
            }

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "List empty!",
                            comments));
        } catch (NotFoundException notFoundException) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject(
                            HttpStatus.NOT_FOUND.value(),
                            notFoundException.getMessage(),
                            null));
        } catch (RuntimeException runtimeException) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            runtimeException.getMessage(),
                            null));
        }

    }

    @GetMapping("get/comments_by_song_user")
    public ResponseEntity<ResponseObject> getCommentsBySongAndUser(
            @RequestParam("idSong") String idSong,
            @RequestParam("idUser") String idUser) {

        try {
            List<Comment> comments = commentService
                    .getCommentsByUserAndSong(idUser, idSong);

            if (!comments.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(new ResponseObject(
                                HttpStatus.OK.value(),
                                "Query get comments by song and user successful!",
                                comments));
            }

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "List empty",
                            comments));
        } catch (NotFoundException notFoundException) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject(
                            HttpStatus.NOT_FOUND.value(),
                            notFoundException.getMessage(),
                            null));
        } catch (RuntimeException runtimeException) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            runtimeException.getMessage(),
                            null));
        }

    }


    @PostMapping("add/like_comment")
    public ResponseEntity<ResponseObject> addLikeComment(
            @RequestParam("idUser") String idUser,
            @RequestParam("idComment") String idComment) {
        try {
            Comment comment = commentService
                    .addLikeComment(idComment, idUser);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "Query like comment successful!",
                            comment)
                    );
        } catch (NotFoundException notFoundException) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject(
                            HttpStatus.NOT_FOUND.value(),
                            notFoundException.getMessage(),
                            null));
        } catch (RuntimeException runtimeException) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            runtimeException.getMessage(),
                            null));
        }
    }

    @DeleteMapping("delete/like_comment")
    public ResponseEntity<ResponseObject> removeLikeComment(
            @RequestParam("idUser") String idUser,
            @RequestParam("idComment") String idComment) {
        try {
            Comment comment = commentService
                    .removeLikeComment(idComment, idUser);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "Query unlike comment successful!",
                            comment)
                    );
        } catch (NotFoundException notFoundException) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject(
                            HttpStatus.NOT_FOUND.value(),
                            notFoundException.getMessage(),
                            null));
        } catch (RuntimeException runtimeException) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            runtimeException.getMessage(),
                            null));
        }
    }

    @GetMapping("get/like_comment_by_user")
    public ResponseEntity<ResponseObject> getLikeCommentByUser(
            @RequestParam("idUser") String idUser) {
        try {
            List<Comment> comments = commentService
                    .getLikeCommentsByUser(idUser);

            return !comments.isEmpty() ?
                    ResponseEntity
                            .status(HttpStatus.OK)
                            .body(new ResponseObject(
                                    HttpStatus.OK.value(),
                                    "Query get like comments by user successful!",
                                    comments)
                            ) :
                    ResponseEntity
                            .status(HttpStatus.OK)
                            .body(new ResponseObject(
                                    HttpStatus.OK.value(),
                                    "List empty!",
                                    comments)
                            );
        } catch (NotFoundException notFoundException) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject(
                            HttpStatus.NOT_FOUND.value(),
                            notFoundException.getMessage(),
                            null));
        } catch (RuntimeException runtimeException) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            runtimeException.getMessage(),
                            null));
        }
    }
}
