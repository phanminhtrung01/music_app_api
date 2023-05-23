package com.example.music_app_api.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "genre")
@Getter
@Setter
@ToString
public class Genre {
    @Id
    @Column(name = "id_genre")
    private String idGenre;
    private String name;

    @ManyToMany(
            mappedBy = "genres",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonIgnore
    private List<Song> songs = new ArrayList<>();

}
