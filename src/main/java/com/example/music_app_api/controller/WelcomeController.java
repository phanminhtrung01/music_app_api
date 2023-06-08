package com.example.music_app_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("")
@CrossOrigin(value = "*", maxAge = 3600)
public class WelcomeController {
    @GetMapping
    public ResponseEntity<String> hello() {

        return ResponseEntity.ok("MUSIC API");
    }
}
