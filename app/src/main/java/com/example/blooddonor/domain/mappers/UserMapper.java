package com.example.blooddonor.domain.mappers;

import com.example.blooddonor.data.models.User;
import com.example.blooddonor.domain.models.UserDTO;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {
    public static UserDTO toDTO(User user) {
        return new UserDTO(user.getId(), user.getFullName(), user.getEmail(), user.getPassword(), user.getBloodType(), user.getProfilePicture(), user.getRoleId());
    }

    public static User toModel(UserDTO userDTO) {
        return new User(userDTO.getId(), userDTO.getFullName(), userDTO.getEmail(), userDTO.getPassword(), userDTO.getBloodType(), userDTO.getProfilePicture(), userDTO.getRoleId());
    }

    public static List<UserDTO> toDTO(List<User> users) {
        return users.stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }

    public static List<User> toModel(List<UserDTO> userDTOs) {
        return userDTOs.stream()
                .map(UserMapper::toModel)
                .collect(Collectors.toList());
    }
}