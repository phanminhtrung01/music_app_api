package com.example.music_app_api.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Entity
@Table(name = "artist")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Artist {

    public Artist(
            String name, String birthday,
            String thumbnail, String sortBiography) {
        this.name = name;
        this.birthday = birthday;
        this.thumbnail = thumbnail;
        this.sortBiography = sortBiography;
    }

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int ID_LENGTH = 7;
    @Id
    @Column(name = "id_artist")
    private String idArtist;
    @Column(nullable = false)
    private String name;
    @Column(name = "real_name")
    private String realName;
    @Column(nullable = false)
    private String birthday;
    @Column(nullable = false)
    private String thumbnail;
    @Column(name = "thumbnail_m")
    private String thumbnailM;
    @Column(name = "sort_biography", nullable = false)
    private String sortBiography;
    private String biography;
    private String national;
    @Column(name = "total_follow")
    private String totalFollow;

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
        this.idArtist = "A" + sb;
    }

    @ManyToMany(
            mappedBy = "artistsSing",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    private List<Song> songs = new ArrayList<>();

    @ManyToMany(
            mappedBy = "favoriteArtists",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    private List<User> users = new ArrayList<>();
}
