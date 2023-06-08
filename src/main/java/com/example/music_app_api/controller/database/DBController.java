package com.example.music_app_api.controller.database;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pmdv/db")
@CrossOrigin(value = "*", maxAge = 3600)
public class DBController {
    @GetMapping
    public ResponseEntity<String> db() {

        return ResponseEntity.ok("MUSIC API DATABASE");
    }

}
