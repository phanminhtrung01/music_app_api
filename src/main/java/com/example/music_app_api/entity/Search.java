package com.example.music_app_api.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.Random;

@Entity
@Table(name = "history_search")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Search {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int ID_LENGTH = 5;

    @Id
    @Column(name = "id_search", nullable = false)
    private String idSearch;

    @Column(name = "key_search", nullable = false)
    private String key;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_user")
    private User user;

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
        this.idSearch = "SEA" + sb;
    }
}
