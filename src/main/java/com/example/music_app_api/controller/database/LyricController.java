package com.example.music_app_api.controller.database;

import com.example.music_app_api.service.database_server.LyricService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(value = "*", maxAge = 3600)
@RequestMapping("/pmdv/db/lyric/")
public class LyricController {
    private final LyricService lyricService;

    @Autowired
    public LyricController(LyricService lyricService) {
        this.lyricService = lyricService;
    }
}
