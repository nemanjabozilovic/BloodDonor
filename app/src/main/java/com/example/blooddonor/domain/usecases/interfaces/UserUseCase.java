package com.example.blooddonor.domain.usecases.interfaces;

import com.example.blooddonor.domain.models.UserDTO;

import java.util.List;

public interface UserUseCase {
    UserDTO getUserById(int userId);

    List<UserDTO> getAllUsers();

    UserDTO insertUser(UserDTO userDTO);

    UserDTO updateUser(UserDTO userDTO);

    boolean deleteUser(int userId);

    UserDTO login(String email, String password);
}