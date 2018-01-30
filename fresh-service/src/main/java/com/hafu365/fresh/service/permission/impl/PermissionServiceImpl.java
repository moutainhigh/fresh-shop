package com.hafu365.fresh.service.permission.impl;

import com.hafu365.fresh.core.entity.member.Permission;
import com.hafu365.fresh.repository.permission.PermissionRepository;
import com.hafu365.fresh.service.permission.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by SunHaiyang on 2017/9/23.
 */
@Service
@Transactional
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    PermissionRepository permissionRepository;


    @Override
    public List<Permission> findAllByPermission() {
        return permissionRepository.findAllByOldIsNull();
    }

    @Override
    public Permission findById(long id) {
        return permissionRepository.findOne(id);
    }

    @Override
    public Permission savePermission(Permission permission) {
        return permissionRepository.save(permission);
    }

    @Override
    public Permission updatePermission(Permission permission) {
        return permissionRepository.save(permission);
    }

    @Override
    public boolean deletePermission(long id) {
        try {
            permissionRepository.delete(id);
            return true;
        }catch (Exception e){
            return false;
        }

    }
}
