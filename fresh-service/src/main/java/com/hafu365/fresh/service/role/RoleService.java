package com.hafu365.fresh.service.role;

import com.hafu365.fresh.core.entity.member.Member;
import com.hafu365.fresh.core.entity.member.Role;

import java.util.List;

/**
 *角色 服务层
 * Created by HuangXueheng on 2017/9/17.
 */
public interface RoleService {

    /**
     * 根据id查询角色
     * @param id
     * @return
     */
    public Role findRoleById(long id);

    public List<Role> findRoleAll();

    /**
     * 根据角色名查询
     * @param name 角色名
     * @return
     */
    public List<Role> findRoleByName(String name);

    /**
     * 保存角色
     * @param role
     * @return
     */
    public Role saveRole (Role role);

    /**
     * 更新角色
     * @param role
     * @return
     */
    public Role updateRole(Role role);

    /**
     * 删除角色
     * @param id
     * @return
     */
    public boolean deleteRole(long id);

    /**
     * 设定为默认角色
     * @param id
     * @return
     */
    public Role setRoleDefault(long id);

    /**
     * 获取默认角色
     * @return
     */
    public Role getRoleByDefaule();


    /**
     * 通过用户查询角色
     * @param member
     * @return
     */
    public List<Role> findRoleByMember(Member member);

    /**
     * 赋予用户权限
     * @param username
     * @param roles
     * @return
     */
    public boolean setMemberRole(String username,List<Role> roles);

}
