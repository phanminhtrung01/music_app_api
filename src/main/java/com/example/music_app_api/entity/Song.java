package com.example.music_app_api.entity;


import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "song")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Song {
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
