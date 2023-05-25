package com.example.music_app_api.entity;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "status_file")
public class TStatusFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_status_file", nullable = false)
    private Long idStatusFile;
    @Column(name = "name_status_file", nullable = false)
    private String nameStatusFile;
    @Column(name = "date_completed", nullable = false)
    private String dateCompleted;
}