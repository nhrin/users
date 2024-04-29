package com.example.users.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestUpdatedFields {

    @Email(message = "Invalid email format")
    private String email;

    private String firstName;

    private String lastName;

    private String birthDate;

    private String address;
    private String phoneNumber;
}
