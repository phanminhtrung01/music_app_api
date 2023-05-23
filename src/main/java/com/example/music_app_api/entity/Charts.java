package com.example.music_app_api.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "charts")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Charts {
    @Id
    @Column(name = "id_chart")
    private String idChart;

    @OneToMany(
            cascade = CascadeType.ALL,
            mappedBy = "chart"
    )
    @ToString.Exclude
    private List<Song> songs = new ArrayList<>();

}
