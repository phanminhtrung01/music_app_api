package com.example.music_app_api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Entity
@Table(name = "comment")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Comment {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int ID_LENGTH = 6;

    @Id
    @Column(name = "id_comment")
    private String idComment;
    @Column(nullable = false)
    private String value;
    @Column(nullable = false)
    private Long date;

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

        Date today = new Date();
        long timeInMilliseconds = today.getTime();
        String numberStr = String.valueOf(timeInMilliseconds);
        String firstTenDigits = numberStr.substring(0, 10);

        this.setDate(Long.valueOf(firstTenDigits));
        this.idComment = "CM" + sb;
    }

    @ManyToOne(
            cascade = CascadeType.ALL)
    @JoinColumn(name = "id_song")
    private Song song;

    @ManyToOne(
            cascade = CascadeType.ALL)
    @JoinColumn(name = "id_user")
    private User user;

    @ManyToMany(
            cascade = CascadeType.ALL)
    @JoinTable(
            name = "comment_like",
            joinColumns = @JoinColumn(name = "id_comment"),
            inverseJoinColumns = @JoinColumn(name = "id_user"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"id_comment", "id_user"}))
    @ToString.Exclude
    @JsonIgnore
    private List<User> usersLike = new ArrayList<>();

}
