package com.example.music_app_api.controller.resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pmdv/src")
@CrossOrigin(value = "*", maxAge = 3600)
public class SRCController {
    @GetMapping
    public ResponseEntity<String> src() {

        return ResponseEntity.ok("MUSIC API RESOURCE");
    }
}
