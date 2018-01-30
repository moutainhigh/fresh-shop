package com.hafu365.fresh.service.role.impl;

import com.hafu365.fresh.core.entity.member.Member;
import com.hafu365.fresh.core.entity.member.Role;
import com.hafu365.fresh.repository.member.MemberRepository;
import com.hafu365.fresh.repository.role.RoleRepository;
import com.hafu365.fresh.service.role.RoleService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色 逻辑层
 * Created by HuangXueheng on 2017/9/17.
 */
@Service
@Transactional
@Log4j
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Override
    public Role findRoleById(long id) {
        return roleRepository.findRoleById(id);
    }

    @Override
    public List<Role> findRoleAll() {
        return roleRepository.findAll();
    }

    @Override
    public List<Role> findRoleByName(String name) {
        return roleRepository.findByName(name);
    }

    @Override
    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public Role updateRole(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public boolean deleteRole(long id) {
        try {
            roleRepository.delete(id);
            return true;
        }catch (Exception e){
            return false;
        }

    }

    @Override
    public Role setRoleDefault(long id) {
        Role role = roleRepository.findRoleByTheDefaultTrue();
        if(role != null){
            role.setTheDefault(false);
            roleRepository.save(role);
        }
        role = roleRepository.findRoleById(id);
        role.setTheDefault(true);
        return roleRepository.save(role);
    }

    @Override
    public Role getRoleByDefaule() {
        return roleRepository.findRoleByTheDefaultTrue();
    }

    @Override
    public List<Role> findRoleByMember(Member member) {
        List<Member> members = new ArrayList<Member>();
        members.add(member);
        return roleRepository.findRoleByMemberList(members);
    }

    @Override
    public boolean setMemberRole(String username, List<Role> roles) {
        Member member = memberRepository.findMemberByUsername(username);
        if(member != null){
            member.setRoleList(roles);
            member = memberRepository.save(member);
            if(member != null){
                return true;
            }
        }
        return false;
    }
}
