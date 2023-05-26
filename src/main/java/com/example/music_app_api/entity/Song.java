package com.example.music_app_api.entity;


import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Entity
@Table(name = "song")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Song {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int ID_LENGTH = 7;

    @Id
    @Column(name = "id_song")
    @JsonAlias({"encodeId", "id"})
    private String idSong;
    private String title;
    @Column(name = "artists_name")
    private String artistsNames;
    private String thumbnail;
    @Column(name = "thumbnail_m")
    private String thumbnailM;
    private int duration;
    @Column(name = "release_date")
    private long releaseDate;

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
        this.idSong = "S" + sb;
    }

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_source", referencedColumnName = "id_source")
    private SourceSong sourceSong;

    @ManyToMany(
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    @JoinTable(
            name = "sing_song",
            joinColumns = @JoinColumn(name = "id_song"),
            inverseJoinColumns = @JoinColumn(name = "id_artist"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"id_song", "id_artist"})
    )
    @ToString.Exclude
    @JsonIgnore
    private List<Artist> artistsSing = new ArrayList<>();

    @ManyToMany(
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    @JoinTable(
            name = "genre_song",
            joinColumns = @JoinColumn(name = "id_song"),
            inverseJoinColumns = @JoinColumn(name = "id_genre"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"id_song", "id_genre"})
    )
    @ToString.Exclude
    @JsonIgnore
    private List<Genre> genres = new ArrayList<>();

    @ManyToMany(mappedBy = "songs")
    @ToString.Exclude
    @JsonIgnore
    private List<Playlist> playlistsOfSong = new ArrayList<>();

    @ManyToMany(mappedBy = "favoriteSongs")
    @ToString.Exclude
    @JsonIgnore
    private List<User> usersFavorite = new ArrayList<>();

    @ManyToMany(mappedBy = "historyListen")
    @ToString.Exclude
    @JsonIgnore
    private List<User> usersListen = new ArrayList<>();

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_chart")
    private Charts chart;

    @OneToMany
    @JoinColumn(name = "id_song")
    @ToString.Exclude
    @JsonIgnore
    private List<Comment> comments = new ArrayList<>();

}
