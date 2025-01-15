package com.example.blooddonor.domain.usecases.interfaces;

import android.content.Context;
import android.net.Uri;

import com.example.blooddonor.domain.models.UserDTO;

import java.util.List;

public interface UserUseCase {
    UserDTO getUserById(int userId);
    List<UserDTO> getAllUsers();
    UserDTO insertUser(UserDTO userDTO);
    boolean userExists(String email);
    UserDTO updateUser(UserDTO userDTO);
    boolean deleteUser(int userId);
    UserDTO login(String email, String password);
    boolean updateVerificationCode(String email, int verificationCode);
    UserDTO getUserByEmail(String email);
    boolean updatePassword(String email, String hashedPassword, String salt);
    String saveProfilePicture(int userId, Context context, Uri imageUri, String existingFilePath);
}