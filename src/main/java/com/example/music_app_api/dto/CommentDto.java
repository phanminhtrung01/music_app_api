package com.example.music_app_api.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class CommentDto implements Serializable {
    private String idComment;
    private String value;
}