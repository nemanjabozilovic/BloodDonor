package com.example.blooddonor.domain.usecases.interfaces;

import com.example.blooddonor.domain.models.UserDTO;

import java.util.List;
import java.util.Map;

public interface UserUseCase {
    List<UserDTO> getAllUsers();

    UserDTO insertUser(UserDTO userDTO);

    UserDTO updateUser(UserDTO userDTO);

    boolean deleteUser(int userId);

    UserDTO login(String email, String password);

    boolean updateVerificationCode(String email, int verificationCode);

    UserDTO getUserByEmail(String email);

    boolean updatePassword(String email, String hashedPassword, String salt);

    Map<String, String> getAllUserEmails();
}