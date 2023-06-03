package com.example.music_app_api.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.Random;


@Entity
@Table(name = "lyric")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Lyric {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int ID_LENGTH = 6;

    @Id
    @Column(name = "id_lyric", nullable = false)
    private String id;
    private String content;

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
        this.id = "LR" + sb;
    }

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_file", referencedColumnName = "id_file")
    private File file;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_song")
    private Song song;

}
