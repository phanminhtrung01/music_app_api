package com.example.music_app_api.service.database_server.iml_service;

import com.example.music_app_api.entity.User;
import com.example.music_app_api.entity.UserCredential;
import com.example.music_app_api.exception.NotFoundException;
import com.example.music_app_api.repo.UserRepository;
import com.example.music_app_api.service.database_server.UserService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
            isValidUser(user, false);
            Optional<User> userOptional = userRepository.findUserByEmail(user.getEmail());
            if (userOptional.isPresent()) {
                throw new RuntimeException("constraint");
            }

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

            String oldName = userDB.getName();
            String oldUsername = userDB.getUsername();
            String oldGender = userDB.getGender();
            String oldPhoneNumber = userDB.getPhoneNumber();
            String oldAvatar = userDB.getAvatar();
            String oldBirthday = userDB.getBirthday();

            String newName = user.getName();
            String newUsername = user.getUsername();
            String newGender = user.getGender();
            String newPhoneNumber = user.getPhoneNumber();
            String newAvatar = user.getAvatar();
            String newBirthday = user.getBirthday();

            if (oldName != null && !oldName.equals(newName)) {
                userDB.setName(newName);
            }

            if (oldUsername != null && !oldUsername.equals(newUsername)) {
                userDB.setUsername(newUsername);
            }

            if (oldGender != null && !oldGender.equals(newGender)) {
                userDB.setGender(newGender);
            }

            if (oldPhoneNumber != null && !oldPhoneNumber.equals(newPhoneNumber)) {
                userDB.setPhoneNumber(newPhoneNumber);
            }

            if (oldAvatar != null && !oldAvatar.equals(newAvatar)) {
                userDB.setAvatar(newAvatar);
            }

            if (oldBirthday != null && !oldBirthday.equals(newBirthday)) {
                userDB.setBirthday(newBirthday);
            }

            isValidUser(user, true);
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

    private void isValidUser(@NotNull User user, boolean constraint) {

        String newEmail = user.getEmail();
        String newPassword = user.getPassword();
        String newPhoneNumber = user.getPhoneNumber();
        String newBirthday = user.getBirthday();
        String newAvatar = user.getAvatar();
        String newUsername = user.getUsername();
        String newGender = user.getGender();


        if (constraint) {
            if (newPhoneNumber.isBlank() || !isValidPhoneNumber(newPhoneNumber)) {
                throw new RuntimeException("Invalid PhoneNumber User! Not blank, Correct format.");
            }
            if (newBirthday.isBlank() || !isValidDate(newBirthday)) {
                throw new RuntimeException("Invalid Birthday User! Not blank, Correct format.");
            }
            if (newAvatar.isBlank()) {
                throw new RuntimeException("Invalid Avatar User! Not blank.");
            }
            if (newUsername.isBlank() || isValidUsername(newUsername)) {
                throw new RuntimeException("Invalid Username User! Not blank, Not special characters.");
            }
        }

        if (newEmail == null || newEmail.isBlank()) {
            throw new RuntimeException("Invalid Email User!");
        }
        if (newPassword == null || newPassword.isBlank() || newPassword.length() < 8) {
            throw new RuntimeException("Invalid Password User!");
        }
        if (newGender == null || newGender.isBlank() || !isValidGenre(newGender)) {
            throw new RuntimeException("Invalid Genre User!");
        }


    }

    private boolean isValidDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(dateString);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private boolean isValidGenre(@NotNull String dateString) {
        return dateString.equalsIgnoreCase("male")
                || dateString.equalsIgnoreCase("female");
    }

    private boolean isValidUsername(String username) {
        String regex = "^[a-zA-Z]\\w*$";
        return username == null || !username.matches(regex);
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        String regex = "^\\d{10}$";
        return phoneNumber != null && phoneNumber.matches(regex);
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
