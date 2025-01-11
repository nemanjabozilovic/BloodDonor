package com.example.blooddonor.domain.repositories;

import com.example.blooddonor.data.models.Role;

import java.util.List;

public interface RoleRepository {
    Role getRoleById(int roleId);
    List<Role> getAllRoles();
    boolean insertRole(Role role);
    boolean updateRole(Role role);
    boolean deleteRole(int roleId);
}