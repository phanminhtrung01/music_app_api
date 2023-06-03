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
@Table(name = "genre")
@Getter
@Setter
@ToString
public class Genre {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int ID_LENGTH = 7;

    @Id
    @Column(name = "id_genre")
    private String idGenre;
    private String name;
    private String title;

    @ManyToMany(
            mappedBy = "genres",
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonIgnore
    private List<Song> songs = new ArrayList<>();

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
        this.idGenre = "G" + sb;
    }

}
