package com.example.users.service;

import com.example.users.model.User;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Component
public class UserService {

    // In-memory storage for demonstration purposes
    private List<User> users = new ArrayList<>();

    @Value("${user.min-age}")
    private int minAge;

    public LocalDate getMinBirthDate() {
        return LocalDate.now().minusYears(minAge);
    }

    public void saveUser(User user) {
        getUsers().add(user);
    }

    public List<User> searchUsersByBirthDateRange(List<User> users, LocalDate fromDate, LocalDate toDate) {
        return users.stream().filter(user -> {
            LocalDate userBirthDate = user.getBirthDate();
            return userBirthDate.isAfter(fromDate) && userBirthDate.isBefore(toDate);
        }).collect(Collectors.toList());
    }
}
