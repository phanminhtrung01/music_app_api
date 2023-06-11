package com.example.music_app_api.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.Random;

@Entity
@Table(name = "source_song")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class SourceSong {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int ID_LENGTH = 6;

    @Id
    @Column(name = "id_source")
    private String idSource;
    @Column(name = "source_m4a")
    @JsonAlias({"uriM4a"})
    private String sourceM4a;
    @Column(name = "source_128")
    @JsonAlias({"uri128"})
    private String source128;
    @Column(name = "source_320")
    @JsonAlias({"uri320"})
    private String source320;
    @Column(name = "source_lossless")
    @JsonAlias({"uriLossless"})
    private String sourceLossless;

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
        this.idSource = "SS" + sb;
    }
}
