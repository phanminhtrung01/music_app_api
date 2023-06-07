package com.example.music_app_api.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Entity
@Table(name = "charts")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Charts {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int ID_LENGTH = 6;

    @Id
    @Column(name = "id_chart")
    private String idChart;

    @OneToMany(
            cascade = CascadeType.ALL,
            mappedBy = "chart"
    )
    @ToString.Exclude
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
        this.idChart = "CH" + sb;
    }

}
