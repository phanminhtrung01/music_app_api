package com.example.music_app_api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_credentials")
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
    private int code;
    @Column(name = "time_verify")
    private String timeVerify;
    @Column(name = "check_login")
    private boolean checkLogin;
}
