package com.example.music_app_api.entity;


import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "lyric")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Lyric {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_lyric", nullable = false)
    private String id;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_file")
    private File file;
    private String sentences;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_song")
    private Song song;

}
