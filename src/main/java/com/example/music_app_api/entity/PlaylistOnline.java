package com.example.music_app_api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Entity
@Table(name = "playlist_on")
@Getter
@Setter
@ToString
public class PlaylistOnline {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int ID_LENGTH = 5;

    @Id
    @Column(name = "id_playlist_on")
    private String encodeId;
    @Column(nullable = false)
    private String title;
    @Column(name = "sort_title", nullable = false)
    private String sortTitle;
    @Column(nullable = false)
    private String thumbnail;
    @Column(name = "thumbnail_m")
    private String thumbnailM;
    @Column(name = "date_create", nullable = false)
    private String dateCreate;

    @PrePersist
    public void generateId() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(ID_LENGTH);
        boolean hasDigit = false;
        for (int i = 0; i < ID_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            char c = CHARACTERS.charAt(index);
            sb.append(c);
            if (Character.isDigit(c)) {
                hasDigit = true;
            }
        }
        if (!hasDigit) {
            int index = random.nextInt(ID_LENGTH);
            int digitIndex = random.nextInt(10) + 26;
            sb.setCharAt(index, CHARACTERS.charAt(digitIndex));
        }
        this.encodeId = "PLO" + sb;
    }

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "playlist_song_on",
            joinColumns = @JoinColumn(name = "id_playlist_on"),
            inverseJoinColumns = @JoinColumn(name = "id_song"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"id_playlist_on", "id_song"})
    )
    @ToString.Exclude
    @JsonIgnore
    private List<Song> songs = new ArrayList<>();
}
