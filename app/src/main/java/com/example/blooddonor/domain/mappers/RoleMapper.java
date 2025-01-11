package com.example.blooddonor.domain.mappers;

import com.example.blooddonor.data.models.Role;
import com.example.blooddonor.domain.models.RoleDTO;

import java.util.List;
import java.util.stream.Collectors;

public class RoleMapper {
    public static RoleDTO toDTO(Role role) {
        return new RoleDTO(role.getId(), role.getRoleName());
    }

    public static Role toModel(RoleDTO roleDTO) {
        return new Role(roleDTO.getId(), roleDTO.getRoleName());
    }

    public static List<RoleDTO> toDTO(List<Role> roles) {
        return roles.stream()
                .map(RoleMapper::toDTO)
                .collect(Collectors.toList());
    }

    public static List<Role> toModel(List<RoleDTO> roleDTOs) {
        return roleDTOs.stream()
                .map(RoleMapper::toModel)
                .collect(Collectors.toList());
    }
}