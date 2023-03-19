package com.example.music_app_api.model;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class ResponseObject {
    private Integer status;
    private String message;
    private Object data;
}
