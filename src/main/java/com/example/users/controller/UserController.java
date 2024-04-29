package com.example.users.controller;

import com.example.users.model.User;
import com.example.users.model.UserRequest;
import com.example.users.model.UserRequestUpdatedFields;
import com.example.users.service.UserService;
import com.example.users.util.Util;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Validated
    @PostMapping("/create")
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserRequest userRequest) {

        User user = Util.convertToUser(userRequest);
        String messageValidatedBirthDate =
                Util.validateAge(user.getBirthDate(),
                        userService.getMinBirthDate(), userService.getMinAge());
        if (messageValidatedBirthDate != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(messageValidatedBirthDate);
        }
        userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("User created successfully.");
    }

    @PatchMapping("/update/{userId}")
    public ResponseEntity<Object> updateUserFields(@PathVariable int userId,
                                                   @Valid @RequestBody UserRequestUpdatedFields updatedUserRequest) {

        User existingUser = getUserById(userId);
        if (existingUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
        // Update fields if provided
        if (updatedUserRequest.getEmail() != null) {
            existingUser.setEmail(updatedUserRequest.getEmail());
        }
        if (updatedUserRequest.getFirstName() != null) {
            existingUser.setFirstName(updatedUserRequest.getFirstName());
        }
        if (updatedUserRequest.getLastName() != null) {
            existingUser.setLastName(updatedUserRequest.getLastName());
        }
        if (updatedUserRequest.getBirthDate() != null) {
            existingUser.setBirthDate(Util.convertStringToLocalDate(updatedUserRequest.getBirthDate()));
        }
        if (updatedUserRequest.getAddress() != null) {
            existingUser.setAddress(updatedUserRequest.getAddress());
        }
        if (updatedUserRequest.getPhoneNumber() != null) {
            existingUser.setPhoneNumber(updatedUserRequest.getPhoneNumber());
        }
        return ResponseEntity.status(HttpStatus.OK).body("User fields updated successfully.");
    }

    @PutMapping("/update-all/{userId}")
    public ResponseEntity<Object> updateAllUserFields(@PathVariable int userId,
                                                      @Valid @RequestBody UserRequest updatedUserRequest) {
        User updatedUser = Util.convertToUser(updatedUserRequest);
        User existingUser = getUserById(userId);
        if (existingUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
        // Update all fields
        userService.getUsers().set(userId, updatedUser);
        return ResponseEntity.status(HttpStatus.OK).body("All user fields updated successfully.");
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable int userId) {
        User existingUser = getUserById(userId);
        if (existingUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
        userService.getUsers().remove(existingUser);
        return ResponseEntity.status(HttpStatus.OK).body("User deleted successfully.");
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchUsersByBirthDateRange(
            @RequestParam("from") @Validated LocalDate fromDate,
            @RequestParam("to") @Validated LocalDate toDate) {

        if (fromDate.isAfter(toDate)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("'From' date must be before 'To' date.");
        }
        List<User> usersInRange = userService
                .searchUsersByBirthDateRange(userService.getUsers(), fromDate, toDate);

        return ResponseEntity.status(HttpStatus.OK).body(usersInRange);
    }

    // Helper method to get a user by ID
    private User getUserById(int userId) {
        if (userId >= 0 && userId < userService.getUsers().size()) {
            return userService.getUsers().get(userId);
        }
        return null;
    }

    // Exception handler for handling validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        List<String> errors = new ArrayList<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errors.add(fieldError.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    // Exception handler for handling IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    // Exception handler for handling any other unhandled exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }
}
