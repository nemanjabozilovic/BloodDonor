package com.example.blooddonor.domain.repositories;

import com.example.blooddonor.data.models.User;

import java.util.List;

public interface UserRepository {
    User getUserById(int userId);
    List<User> getAllUsers();
    boolean insertUser(User user);
    boolean updateUser(User user);
    boolean deleteUser(int userId);
    boolean assignRoleToUser(int userId, int roleId);
}