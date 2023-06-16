package com.example.music_app_api.controller.ui;

import com.example.music_app_api.entity.Song;
import com.example.music_app_api.entity.SourceSong;
import com.example.music_app_api.service.database_server.SongService;
import com.example.music_app_api.service.database_server.SourceSongService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@CrossOrigin(value = "*", maxAge = 3600)
@RequestMapping("/pmdv/ui/song/")
public class SongController {

    private final SongService songService;
    private final SourceSongService sourceSongService;

    @Autowired
    public SongController(
            SongService songService,
            SourceSongService sourceSongService) {
        this.songService = songService;
        this.sourceSongService = sourceSongService;
    }

    @GetMapping("info")
    public String song(@RequestParam String id, @NotNull Model model) {
        Song song = songService.getSong(id);
        SourceSong sourceSong = sourceSongService.getSourceSongByIdSong(id);
        song.setSourceSong(sourceSong);
        model.addAttribute("song", song);

        return "song";
    }
}