package com.example.music_app_api.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "source_song")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class SourceSong {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_source")
    private String idSource;
    @Column(name = "source_m4a")
    private String sourceM4a;
    @Column(name = "source_128")
    private String source128;
    @Column(name = "source_320")
    private String source320;
    @Column(name = "source_lossless")
    private String sourceLossless;
}
