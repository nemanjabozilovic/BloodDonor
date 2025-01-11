package com.example.blooddonor.domain.usecases.implementation;

import com.example.blooddonor.data.models.Role;
import com.example.blooddonor.domain.mappers.RoleMapper;
import com.example.blooddonor.domain.models.RoleDTO;
import com.example.blooddonor.domain.repositories.RoleRepository;
import com.example.blooddonor.domain.usecases.interfaces.RoleUseCase;

import java.util.ArrayList;
import java.util.List;

public class RoleUseCaseImpl implements RoleUseCase {
    private final RoleRepository roleRepository;

    public RoleUseCaseImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public RoleDTO getRoleById(int roleId) {
        Role role = roleRepository.getRoleById(roleId);
        return role != null ? RoleMapper.toDTO(role) : null;
    }

    @Override
    public List<RoleDTO> getAllRoles() {
        List<RoleDTO> roleDTOs = new ArrayList<>();
        List<Role> roles = roleRepository.getAllRoles();
        for (Role role : roles) {
            roleDTOs.add(RoleMapper.toDTO(role));
        }
        return roleDTOs;
    }

    @Override
    public boolean insertRole(RoleDTO roleDTO) {
        Role role = RoleMapper.toModel(roleDTO);
        return roleRepository.insertRole(role);
    }

    @Override
    public boolean updateRole(RoleDTO roleDTO) {
        Role role = RoleMapper.toModel(roleDTO);
        return roleRepository.updateRole(role);
    }

    @Override
    public boolean deleteRole(int roleId) {
        return roleRepository.deleteRole(roleId);
    }

    @Override
    public int getRoleIdByName(String roleName) {
        List<Role> roles = roleRepository.getAllRoles();
        for (Role role : roles) {
            if (role.getRoleName().equalsIgnoreCase(roleName)) {
                return role.getId();
            }
        }
        return -1;
    }
}