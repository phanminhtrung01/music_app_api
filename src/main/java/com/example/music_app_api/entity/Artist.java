package com.example.music_app_api.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "artist")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Artist {
    @Id
    @Column(name = "id_artist")
    private String idArtist;
    private String name;
    @Column(name = "real_name")
    private String realName;
    private Date birthday;
    private String thumbnail;
    @Column(name = "thumbnail_m")
    private String thumbnailM;
    @Column(name = "sort_biography")
    private String sortBiography;
    private String biography;
    private String national;
    @Column(name = "total_follow")
    private String totalFollow;

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
