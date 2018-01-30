package com.hafu365.fresh.service.member;


import com.hafu365.fresh.core.entity.member.Member;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 用户 服务层
 * Created by zhaihuilin on 2017/7/21  14:44.
 */
public interface MemberService {


    /**
     * 通过用户名查询
     * @param username
     * @return
     */
    public Member findMemberByUsername(String username);

    /**
     * 根据 用户编号 查询 用户信息
     * @param memberId
     * @return
     */
    public Member findMemberByMemberId(String memberId);

    /**
     *  保存用户信息
     * @param member
     * @return
     */
    public Member  saveMember(Member member);

    /**
     * 根据条件查询
     * @param member 条件查询
     * @param pageable 翻页
     * @return
     */
    public Page<Member> findAllByMember(Member member, Pageable pageable);

    /**
     * 逻辑删除
     * @param memberId
     * @return
     */
    public boolean deleteByMemberId(String memberId);

    /**
     * 物理删除
     * @param memberId
     * @return
     */
    public boolean physicallyDeleteByMemberId(String memberId);

    /**
     * 查询用户是否存在
     * @param username
     * @return
     */
    public boolean existMemberbyUsername(String username);


    /**
     * 获取所有用户
     * @return
     */
    public List<Member> findAll();

    /**
     * 获取所有用户名
     * @return
     */
    public List<String> findUsername();

    /**
     * 根据用户名获取简易的用户信息
     * @param username  用户名   ZHL
     * @return
     */
      Member findSimpleMemberByUsername(String username);

    /**
     * 根据用户名编号获取简易的用户信息
     * @param memberId  用户编号  ZHL
     * @return
     */
    public  Member findSimpleMemberByMemberId(String memberId);

}
