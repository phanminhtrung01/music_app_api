package com.example.music_app_api.service.database_server.iml_service;

import com.example.music_app_api.entity.User;
import com.example.music_app_api.exception.NotFoundException;
import com.example.music_app_api.repo.UserRepository;
import com.example.music_app_api.service.database_server.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    @Override
    public User getUserById(String id) {
        try {
            Optional<User> userOptional = userRepository.findById(id);
            if (userOptional.isEmpty()) {
                throw new NotFoundException("Not fount user with ID: " + id);
            }

            return userOptional.get();
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    public User save(User user) {
        try {
            userRepository.save(user);
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
        return user;
    }

    @Override
    public User delete(String idUser) {
        try {
            User user = getUserById(idUser);
            userRepository.delete(user);

            return user;
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    @Override
    public Map<String, Object> verifyUser(String email, String password) {
        Map<String, Object> map = new HashMap<>();
        try {
            Optional<User> userOptional = userRepository.findUserByEmail(email);
            if (userOptional.isEmpty()) {
                throw new NotFoundException("Not found user with email: " + email);
            }
            User user = userOptional.get();

            if (user.getPassword().equals(password)) {
                map.put("user", user);
                map.put("check", true);
                return map;
            } else {
                map.put("user", new User());
                map.put("check", false);
            }

            return map;
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
    }
}
