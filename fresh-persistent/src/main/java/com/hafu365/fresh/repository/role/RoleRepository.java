package com.hafu365.fresh.repository.role;

import com.hafu365.fresh.core.entity.member.Member;
import com.hafu365.fresh.core.entity.member.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 角色 持久层
 * Created by HuangXueheng on 2017/9/17.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {

    /**
     * 根据角色id查询角色
     * @param id
     * @return
     */
    public Role findRoleById(long id);

    /**
     * 根据用户名查询
     * @param name
     * @return
     */
    public List<Role> findByName(String name);


    /**
     * 设定默认角色
     * @param id
     * @return
     */
    @Query(value = "update Role r set r.theDefault = true where r.id = ?1")
    public Role setRoleDefault(long id);

    /**
     * 查询默认角色
     * @return
     */
    public Role findRoleByTheDefaultTrue();


    /**
     * 通过用户查询角色
     * @return
     */
    public List<Role> findRoleByMemberList(List<Member> members);

}
