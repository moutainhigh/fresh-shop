package com.hafu365.fresh.service.permission;

import com.hafu365.fresh.core.entity.member.Permission;

import java.util.List;

/**
 * 权限逻辑类
 * Created by SunHaiyang on 2017/9/23.
 */
public interface PermissionService {

    public List<Permission> findAllByPermission();

    public Permission findById(long id);

    public Permission savePermission(Permission permission);

    public Permission updatePermission(Permission permission);

    public boolean deletePermission(long id);

}
