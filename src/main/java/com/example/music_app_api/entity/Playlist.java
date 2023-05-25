package com.example.music_app_api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "playlist")
@Getter
@Setter
@ToString
public class Playlist {
    @Id
    @Column(name = "id_playlist")
    private String idPlaylist;
    private String name;
    @Column(name = "date_create")
    private String dateCreate;

    @ManyToMany(
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    @JoinTable(
            name = "playlist_song",
            joinColumns = @JoinColumn(name = "id_playlist"),
            inverseJoinColumns = @JoinColumn(name = "id_song"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"id_playlist", "id_song"})
    )
    @ToString.Exclude
    @JsonIgnore
    private List<Song> songs = new ArrayList<>();

    @ManyToMany(
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    @JoinTable(
            name = "playlist_user",
            joinColumns = @JoinColumn(name = "id_playlist"),
            inverseJoinColumns = @JoinColumn(name = "id_user"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"id_playlist", "id_user"})
    )
    @ToString.Exclude
    @JsonIgnore
    private List<User> users = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Playlist playlist)) return false;
        return getIdPlaylist().equals(playlist.getIdPlaylist());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdPlaylist());
    }
}
