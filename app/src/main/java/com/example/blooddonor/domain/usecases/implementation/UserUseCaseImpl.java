package com.example.blooddonor.domain.usecases.implementation;

import android.content.Context;
import android.net.Uri;

import com.example.blooddonor.R;
import com.example.blooddonor.data.models.User;
import com.example.blooddonor.domain.mappers.UserMapper;
import com.example.blooddonor.domain.models.UserDTO;
import com.example.blooddonor.domain.repositories.RoleRepository;
import com.example.blooddonor.domain.repositories.UserRepository;
import com.example.blooddonor.domain.usecases.interfaces.UserUseCase;
import com.example.blooddonor.utils.ImageUploadHelper;
import com.example.blooddonor.utils.UserAlreadyExistsException;

import java.util.ArrayList;
import java.util.List;

public class UserUseCaseImpl implements UserUseCase {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserUseCaseImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserDTO getUserById(int userId) {
        User user = userRepository.getUserById(userId);
        if (user != null) {
            UserDTO userDTO = UserMapper.toDTO(user);
            userDTO.setRoleName(roleRepository.getRoleById(user.getRoleId()).getRoleName());
            return userDTO;
        }
        return null;
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<UserDTO> userDTOs = new ArrayList<>();
        List<User> users = userRepository.getAllUsers();
        for (User user : users) {
            UserDTO userDTO = UserMapper.toDTO(user);
            userDTO.setRoleName(roleRepository.getRoleById(user.getRoleId()).getRoleName());
            userDTOs.add(userDTO);
        }
        return userDTOs;
    }

    @Override
    public UserDTO insertUser(UserDTO userDTO) {
        if (userRepository.userExists(userDTO.getEmail())) {
            throw new UserAlreadyExistsException("User with email " + userDTO.getEmail() + " already exists.");
        }

        User user = UserMapper.toModel(userDTO);
        return UserMapper.toDTO(userRepository.insertUser(user));
    }

    @Override
    public boolean userExists(String email) {
        return userRepository.userExists(email);
    }

    @Override
    public UserDTO updateUser(UserDTO userDTO) {
        User user = UserMapper.toModel(userDTO);
        return UserMapper.toDTO(userRepository.updateUser(user));
    }

    @Override
    public boolean deleteUser(int userId) {
        return userRepository.deleteUser(userId);
    }

    @Override
    public UserDTO login(String email, String password) {
        User user = userRepository.login(email, password);
        if (user == null) return null;

        UserDTO userDTO = UserMapper.toDTO(user);
        String roleName = roleRepository.getRoleById(user.getRoleId()).getRoleName();
        userDTO.setRoleName(roleName);

        return userDTO;
    }

    @Override
    public boolean updateVerificationCode(String email, int verificationCode){
        return userRepository.updateVerificationCode(email, verificationCode);
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.getUserByEmail(email);
        if (user != null) {
            UserDTO userDTO = UserMapper.toDTO(user);
            userDTO.setRoleName(roleRepository.getRoleById(user.getRoleId()).getRoleName());
            return userDTO;
        }
        return null;
    }

    @Override
    public boolean updatePassword(String email, String hashedPassword, String salt) {
        return userRepository.updatePassword(email, hashedPassword, salt);
    }
}