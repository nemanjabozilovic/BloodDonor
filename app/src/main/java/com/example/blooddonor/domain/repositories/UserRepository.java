package com.example.blooddonor.domain.repositories;

import com.example.blooddonor.data.models.User;

import java.util.List;
import java.util.Map;

public interface UserRepository {
    User getUserById(int userId);

    boolean userExists(String email);

    List<User> getAllUsers();

    User insertUser(User user);

    User updateUser(User user);

    boolean deleteUser(int userId);

    User login(String email, String password);

    boolean updateVerificationCode(String email, int verificationCode);

    User getUserByEmail(String email);

    boolean updatePassword(String email, String hashedPassword, String salt);

    Map<String, String> getAllUserEmails();
}