package com.example.blooddonor.domain.repositories;

import com.example.blooddonor.data.models.User;

import java.util.List;

public interface UserRepository {
    User getUserById(int userId);

    boolean userExists(String email);

    List<User> getAllUsers();
    User insertUser(User user);
    User updateUser(User user);
    boolean deleteUser(int userId);
    User login(String email, String password);
}