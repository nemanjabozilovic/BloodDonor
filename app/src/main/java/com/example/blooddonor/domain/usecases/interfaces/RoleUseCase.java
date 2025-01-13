package com.example.blooddonor.domain.usecases.interfaces;

import com.example.blooddonor.domain.models.RoleDTO;

import java.util.List;

public interface RoleUseCase {
    RoleDTO getRoleById(int roleId);
    List<RoleDTO> getAllRoles();
    boolean insertRole(RoleDTO roleDTO);
    boolean updateRole(RoleDTO roleDTO);
    boolean deleteRole(int roleId);
    int getRoleIdByName(String roleName);
}
