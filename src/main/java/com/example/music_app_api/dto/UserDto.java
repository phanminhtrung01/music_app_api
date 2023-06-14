package com.example.music_app_api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto implements Serializable {
    private String idUser;
    private String name;
    private String username;
    private String gender;
    private String email;
    private String phoneNumber;
    private String avatar;
    private String birthday;
}