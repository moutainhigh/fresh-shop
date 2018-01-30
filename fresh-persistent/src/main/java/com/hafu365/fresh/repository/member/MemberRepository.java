package com.hafu365.fresh.repository.member;


import com.hafu365.fresh.core.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户 执久层
 * Created by zhaihuilin on 2017/7/21  14:43.
 */
@Repository
public interface MemberRepository extends JpaRepository<Member,String> ,JpaSpecificationExecutor<Member> {

    /**
     * 根据 用户编号 查询 用户信息
     * @param memberId
     * @return
     */
    public Member findMemberByMemberIdAndDelFalse(String memberId);

    /**
     * 通过用户名查询用户
     * @param username
     * @return
     */
    public Member findMemberByUsernameAndDelFalse(String username);

    /**
     * 逻辑删除用户
     * @param memberId
     */
    @Query(value = "update Member m set m.del = true where m.memberId=?1")
    @Modifying
    public void deleteByMemberId(String memberId);

    /**
     * 通过用户名查询member
     * @param username
     * @return
     */
    public Member findMemberByUsername(String username);

    /**
     * 获取所有未删除的用户
     * @return
     */
    public List<Member> findAllByDelFalse();

    /**
     * 获取所有用户名
     * @return
     */
    @Query(value = "select m.username from Member m where m.del = false")
    public List<String> findUsernames();



}
