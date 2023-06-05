package com.example.music_app_api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_credential")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserCredential {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_credential")
    private Long idCredential;
    @Column(unique = true)
    private Integer code;
    @Column(name = "time_verify")
    private Long timeVerify;
    @Column(name = "check_login")
    private boolean checkLogin;
}
