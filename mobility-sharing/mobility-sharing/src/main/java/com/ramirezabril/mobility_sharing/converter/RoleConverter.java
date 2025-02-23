package com.ramirezabril.mobility_sharing.converter;

import com.ramirezabril.mobility_sharing.entity.Role;
import com.ramirezabril.mobility_sharing.model.RoleModel;

public class RoleConverter {

    public static RoleModel toRoleModel(Role role) {
        return role == null ? null : new RoleModel(
                role.getId(),
                role.getName()
        );
    }

    public static Role toRoleEntity(RoleModel roleModel) {
        if (roleModel == null) {
            return null;
        }

        Role role = new Role();
        role.setId(roleModel.getId());
        role.setName(roleModel.getName());

        return role;
    }
}