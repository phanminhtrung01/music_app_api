package com.example.music_app_api.controller.database;

import com.example.music_app_api.service.database_server.GenreService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(value = "*", maxAge = 3600)
@RequestMapping("/pmdv/db/genre/")
public class GenreController {
    private final GenreService genreService;

    public GenreController(GenreService genreService) {

        this.genreService = genreService;
    }
}