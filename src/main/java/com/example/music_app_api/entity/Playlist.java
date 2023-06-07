package com.example.music_app_api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@Entity
@Table(name = "playlist")
@Getter
@Setter
@ToString
public class Playlist {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int ID_LENGTH = 6;

    @Id
    @Column(name = "id_playlist")
    private String idPlaylist;
    @Column(nullable = false)
    private String name;
    @Column(name = "date_create", nullable = false)
    private String dateCreate;
    private String thumbnail;

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
        this.idPlaylist = "PL" + sb;
    }

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "playlist_song",
            joinColumns = @JoinColumn(name = "id_playlist"),
            inverseJoinColumns = @JoinColumn(name = "id_song"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"id_playlist", "id_song"})
    )
    @ToString.Exclude
    @JsonIgnore
    private List<Song> songs = new ArrayList<>();

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_user")
    private User user;

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
