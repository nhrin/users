package com.example.users.util;

import com.example.users.model.User;
import com.example.users.model.UserRequest;
import com.example.users.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class Util {

    @Autowired
    private static UserService userService;

    public static User convertToUser(UserRequest userRequest) {
        User user = new User();
        user.setEmail(userRequest.getEmail());
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setAddress(userRequest.getAddress());
        user.setPhoneNumber(userRequest.getPhoneNumber());
        user.setBirthDate(convertStringToLocalDate(userRequest.getBirthDate()));
        return user;
    }

    public static LocalDate convertStringToLocalDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(date, formatter);
    }

    public static String validateAge(LocalDate birthDate,
                                     LocalDate minBirthDate, int minAge) {
        LocalDate currentDate = LocalDate.now();
        if (birthDate.isAfter(currentDate)) {
            return "Birth date must be earlier than the current date.";
        }
        if (birthDate.isAfter(minBirthDate)) {
            return "User must be at least " + minAge + " " +
                    "years old.";
        }

        return null;
    }
}
