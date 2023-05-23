package com.example.music_app_api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "file")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class File {
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_file", nullable = false)
    private Long idFile;
    private String data;
    @Column(name = "name_file", nullable = false)
    private String nameFile;
    @Column(name = "size_file", nullable = false)
    private String sizeFile;
    @Column(name = "type_file")
    private String typeFile;
    @Column(name = "status_file")
    private String statusFile;

}
