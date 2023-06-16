package com.example.music_app_api.controller.database;

import com.example.music_app_api.entity.User;
import com.example.music_app_api.exception.NotFoundException;
import com.example.music_app_api.model.ResponseObject;
import com.example.music_app_api.service.database_server.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/pmdv/db/user/")
@CrossOrigin(value = "*", maxAge = 3600)
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {

        this.userService = userService;
    }

    @PostMapping("add")
    public ResponseEntity<ResponseObject> addUser(
            @RequestBody User user) {

        try {
            String avatar = user.getAvatar();
            String gender = user.getGender();

            if (avatar == null || avatar.isEmpty()) {
                if (gender.equals("Male") || gender.equals("male")) {
                    user.setAvatar("https://cdn1.iconfinder.com/" +
                            "data/icons/avatars-55/100/" +
                            "avatar_profile_user_music_headphones_shirt_cool-512.png");
                } else if (gender.equals("Female") || gender.equals("female")) {
                    user.setAvatar("https://cdn1.iconfinder.com/" +
                            "data/icons/ordinary-people/512/music-512.png");
                } else {
                    user.setAvatar("https://cdn1.iconfinder.com/" +
                            "data/icons/ordinary-people/512/music-512.png");
                }
            }
            User userPar = userService.save(user);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new ResponseObject(
                            HttpStatus.CREATED.value(),
                            "Query add user successful!",
                            userPar));
        } catch (NotFoundException notFoundException) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject(
                            HttpStatus.NOT_FOUND.value(),
                            notFoundException.getMessage(),
                            null));
        } catch (RuntimeException e) {

            if (e.getMessage().contains("constraint")) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(new ResponseObject(
                                HttpStatus.CONFLICT.value(),
                                "The email is already!",
                                null));
            }

            return e.getMessage().contains("Invalid") ?
                    ResponseEntity
                            .status(HttpStatus.CONFLICT)
                            .body(new ResponseObject(
                                    HttpStatus.CONFLICT.value(),
                                    e.getMessage(),
                                    null)) :
                    ResponseEntity
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(new ResponseObject(
                                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                    e.getMessage(),
                                    null));
        }
    }

    @DeleteMapping("delete")
    public ResponseEntity<ResponseObject> delUser(
            @RequestParam("idUser") String idUser) {

        try {
            User user = userService.delete(idUser);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "Query remove user successful!",
                            user));

        } catch (NotFoundException notFoundException) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject(
                            HttpStatus.NOT_FOUND.value(),
                            notFoundException.getMessage(),
                            null));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            e.getMessage(),
                            null));
        }

    }

    @PutMapping("update")
    public ResponseEntity<ResponseObject> updateUser(
            @RequestBody User user) {

        try {
            User userPar = userService.update(user);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "Query update user successful!",
                            userPar));
        } catch (NotFoundException notFoundException) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject(
                            HttpStatus.NOT_FOUND.value(),
                            notFoundException.getMessage(),
                            null));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            e.getMessage(),
                            null));
        }
    }

    @GetMapping("get")
    public ResponseEntity<ResponseObject> getUser(
            @RequestParam("idUser") String idUser) {

        try {
            User user = userService.getUserById(idUser);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "Query get user successful!",
                            user));

        } catch (NotFoundException notFoundException) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject(
                            HttpStatus.NOT_FOUND.value(),
                            notFoundException.getMessage(),
                            null));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            e.getMessage(),
                            null));
        }

    }

    @PostMapping("verify")
    public ResponseEntity<ResponseObject> verifyUser(
            @RequestBody User userPar) {

        try {
            if (userPar.getEmail() == null
                    || userPar.getPassword() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseObject(
                                HttpStatus.BAD_REQUEST.value(),
                                "Email Or Password NULL. " +
                                        "User authentication failed!",
                                null));
            }

            String regex = "^(.+)@(\\S+)$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(userPar.getEmail());
            boolean isValid = matcher.matches();

            if (!isValid) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseObject(
                                HttpStatus.BAD_REQUEST.value(),
                                "Email isValid. " +
                                        "User authentication failed!",
                                null));
            }

            Map<String, Object> mapVerify = userService
                    .verifyUser(userPar.getEmail(), userPar.getPassword());
            boolean check = (boolean) mapVerify.get("check");
            User user = (User) mapVerify.get("user");

            return check ?
                    ResponseEntity.status(HttpStatus.OK)
                            .body(new ResponseObject(
                                    HttpStatus.OK.value(),
                                    "User authentication successful!",
                                    user)) :
                    ResponseEntity
                            .status(HttpStatus.CONFLICT)
                            .body(new ResponseObject(
                                    HttpStatus.CONFLICT.value(),
                                    "User authentication failed!",
                                    null));
        } catch (NotFoundException notFoundException) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject(
                            HttpStatus.NOT_FOUND.value(),
                            notFoundException.getMessage(),
                            null));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            e.getMessage(),
                            null));
        }
    }

    @PostMapping("logout")
    public ResponseEntity<ResponseObject> logout(
            @RequestParam("email") String email) {

        try {
            String regex = "^(.+)@(\\S+)$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(email);
            boolean isValid = matcher.matches();

            if (!isValid) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ResponseObject(
                                HttpStatus.BAD_REQUEST.value(),
                                "Email isValid. " +
                                        "User authentication failed!",
                                null));
            }

            User user = userService.logout(email);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "Logout successful!",
                            user));
        } catch (NotFoundException notFoundException) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject(
                            HttpStatus.NOT_FOUND.value(),
                            notFoundException.getMessage(),
                            null));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            e.getMessage(),
                            null));
        }
    }

    @PostMapping("forget_password")
    public ResponseEntity<ResponseObject> forgetPasswordUser(
            @RequestParam("email") String email) {

        try {
            User userVerify = userService.forgetPasswordUser(email);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject(
                            HttpStatus.OK.value(),
                            "User authentication successful!",
                            userVerify));
        } catch (NotFoundException notFoundException) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject(
                            HttpStatus.NOT_FOUND.value(),
                            notFoundException.getMessage(),
                            null));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            e.getMessage(),
                            null));
        }
    }

    @PostMapping("verify/forget_password")
    public ResponseEntity<ResponseObject> verifyForgetPasswordUser(
            @RequestParam("email") String email,
            @RequestParam("code") Integer code) {

        try {
            if (code == null) {
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(new ResponseObject(
                                HttpStatus.OK.value(),
                                "Authentication failed!",
                                null));
            }

            Map<String, Object> mapVerify = userService
                    .verifyForgetPassword(email, code);
            User userVerify = (User) mapVerify.get("user");
            boolean check = (boolean) mapVerify.get("check");

            return check ?
                    ResponseEntity.status(HttpStatus.OK)
                            .body(new ResponseObject(
                                    HttpStatus.OK.value(),
                                    "Authentication successful!",
                                    userVerify)) :
                    ResponseEntity
                            .status(HttpStatus.OK)
                            .body(new ResponseObject(
                                    HttpStatus.OK.value(),
                                    "Authentication failed!",
                                    null));
        } catch (NotFoundException notFoundException) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject(
                            HttpStatus.NOT_FOUND.value(),
                            notFoundException.getMessage(),
                            null));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            e.getMessage(),
                            null));
        }
    }
}
