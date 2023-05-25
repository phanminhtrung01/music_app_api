package com.example.music_app_api.entity;


import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User {
    @Id
    @Column(name = "id_user")
    @JsonAlias({"id_user", "id"})
    private String idUser;
    private String name;
    private String username;
    private String password;
    private String gender;
    private String email;
    @Column(name = "phone_number")
    private String phoneNumber;
    private String avatar;
    private String birthday;
    @Column(name = "is_vip")
    private boolean isVip;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_credential", referencedColumnName = "id_credential")
    private UserCredential userCredential;
    @ManyToMany(
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    @JoinTable(
            name = "favorite_song",
            joinColumns = @JoinColumn(name = "id_user"),
            inverseJoinColumns = @JoinColumn(name = "id_song"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"id_user", "id_song"})
    )
    @ToString.Exclude
    @JsonIgnore
    private List<Song> favoriteSongs = new ArrayList<>();

    @ManyToMany(
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    @JoinTable(
            name = "favorite_artist",
            joinColumns = @JoinColumn(name = "id_user"),
            inverseJoinColumns = @JoinColumn(name = "id_artist"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"id_user", "id_artist"})
    )
    @ToString.Exclude
    @JsonIgnore
    private List<Artist> favoriteArtists = new ArrayList<>();

    @ManyToMany(
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    @JoinTable(
            name = "listen_song",
            joinColumns = @JoinColumn(name = "id_user"),
            inverseJoinColumns = @JoinColumn(name = "id_song"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"id_user", "id_song"})
    )
    @ToString.Exclude
    @JsonIgnore
    private List<Song> historyListen = new ArrayList<>();

    @OneToMany
    @JoinColumn(name = "id_user")
    @ToString.Exclude
    @JsonIgnore
    private List<Search> searches = new ArrayList<>();

    @OneToMany
    @JoinColumn(name = "id_user")
    @ToString.Exclude
    @JsonIgnore
    private List<Comment> comments = new ArrayList<>();

    @ManyToMany(
            mappedBy = "users",
            cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonIgnore
    private List<Playlist> playlistsOfUser = new ArrayList<>();

    @ManyToMany(
            mappedBy = "usersLike",
            cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonIgnore
    private List<Comment> commentsLike = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return getIdUser().equals(user.getIdUser());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdUser());
    }

}
