package com.example.music_app_api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Random;

@Entity
@Table(name = "file")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class File {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int ID_LENGTH = 7;

    @Id
    @Column(name = "id_file")
    private String idFile;
    private String data;
    private String nameFile;
    private String sizeFile;
    private String typeFile;
    @Column(name = "status_file")
    private String statusFile;

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
        this.idFile = "F" + sb;
    }
}
