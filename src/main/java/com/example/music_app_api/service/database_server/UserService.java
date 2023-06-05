package com.example.music_app_api.service.database_server;

import com.example.music_app_api.entity.User;

import java.util.Map;

public interface UserService {
    User getUserById(String id);

    User save(User user);

    User delete(String idUser);

    Map<String, Object> verifyUser(String email, String password);

    User logout(String email);

    User forgetPasswordUser(String email);

    Map<String, Object> verifyForgetPassword(String email, int code);
}
