package com.hafu365.fresh.repository.permission;

import com.hafu365.fresh.core.entity.member.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 权限持久层
 * Created by SunHaiyang on 2017/9/23.
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission,Long> {


        public List<Permission> findAllByOldIsNull();
}
