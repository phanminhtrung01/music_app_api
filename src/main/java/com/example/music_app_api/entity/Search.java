package com.example.music_app_api.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "history_search")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Search {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_search")
    private Long idSearch;

    @Column(name = "key_search")
    private String key;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_user")
    private User user;
}
