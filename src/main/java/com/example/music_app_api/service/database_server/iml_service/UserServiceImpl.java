package com.example.music_app_api.service.database_server.iml_service;

import com.example.music_app_api.entity.User;
import com.example.music_app_api.entity.UserCredential;
import com.example.music_app_api.exception.NotFoundException;
import com.example.music_app_api.repo.UserRepository;
import com.example.music_app_api.service.database_server.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
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
    public User update(User user) {
        try {
            User userDB = getUserById(user.getIdUser());
            UserCredential userCredential = userDB.getUserCredential();
            userDB.setUserCredential(userCredential);

            userRepository.save(userDB);

            return userDB;
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                throw new NotFoundException(e.getMessage());
            } else {
                throw new RuntimeException(e.getMessage());
            }
        }
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
    public Map<String, Object> verifyUser(
            String email, String password) {
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

    @Override
    public User logout(String email) {
        try {
            Optional<User> userOptional = userRepository.findUserByEmail(email);
            if (userOptional.isEmpty()) {
                throw new NotFoundException("Not found user with email: " + email);
            }
            User user = userOptional.get();
            UserCredential userCredential = user.getUserCredential();
            userCredential.setCheckLogin(false);
            user.setUserCredential(userCredential);

            userRepository.save(user);
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
    public User forgetPasswordUser(String email) {
        try {
            Optional<User> userOptional = userRepository.findUserByEmail(email);
            if (userOptional.isEmpty()) {
                throw new NotFoundException("Not found user with email: " + email);
            }
            User user = userOptional.get();
            UserCredential userCredential = user.getUserCredential();
            long currentTime = System.currentTimeMillis();

            String input = email + currentTime;
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));

            BigInteger number = new BigInteger(1, hash);
            int randomNumber = number.mod(new BigInteger("10000")).intValue();
            userCredential.setTimeVerify(currentTime);
            userCredential.setCode(randomNumber);
            user.setUserCredential(userCredential);

            userRepository.save(user);

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
    public Map<String, Object> verifyForgetPassword(String email, int code) {
        Map<String, Object> map = new HashMap<>();
        try {
            Optional<User> userOptional = userRepository.findUserByEmail(email);
            if (userOptional.isEmpty()) {
                throw new NotFoundException("Not found user with email: " + email);
            }
            User user = userOptional.get();
            UserCredential userCredential = user.getUserCredential();
            long currentTime = System.currentTimeMillis();

            if (currentTime - userCredential.getTimeVerify() < 900000
                    && userCredential.getCode() == code) {
                userCredential.setTimeVerify(null);
                userCredential.setCode(null);
                user.setUserCredential(userCredential);

                map.put("user", user);
                map.put("check", true);

                userRepository.save(user);
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
