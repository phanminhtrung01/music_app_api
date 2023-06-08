package com.example.music_app_api.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class NotFoundController {

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<String> notFound() {
        return ResponseEntity.ok("NOT FOUND!");
    }

}