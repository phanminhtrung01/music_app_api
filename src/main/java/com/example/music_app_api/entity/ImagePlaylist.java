package com.example.music_app_api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "image_playlist")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ImagePlaylist {
    @Id
    @Column(name = "img_playlist", nullable = false)
    String idImg;
    @Column(nullable = false)
    String thumbnail;
}
