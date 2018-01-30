package com.hafu365.fresh.service.member;

import com.hafu365.fresh.core.entity.constant.CommonConstant;
import com.hafu365.fresh.core.entity.member.Member;
import com.hafu365.fresh.core.entity.member.MemberInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

/**
 * 用户配置信息 服务层
 * Created by HuangXueheng on 2017/9/19.
 */
public interface MemberInfoService {


    /**
     * 保存用户配置信息
     * @param memberInfo
     */
    public MemberInfo saveMemberInfo(MemberInfo memberInfo);

    /**
     * 更改用户配置信息
     * @param memberInfo
     */
    public MemberInfo updateMemberInfo(MemberInfo memberInfo);

    /**
     * 通过用户查找用户配置信息
     * @param member
     * @return
     */
    public MemberInfo findMemberInfoByMember(Member member);

    /**
     * 查询用户信息
     * @param pageable
     * @return
     */
    public Page<MemberInfo> findMemberInfo(Pageable pageable);

    /**
     * 根据用户名查询用户信息
     * @param username
     * @return
     */
    public MemberInfo findMemberInfoByUsername(String username);

    /**
     * 根据状态查询用户
     * @param state
     * @return
     */
    public Page<MemberInfo> findMemberInfoByState(String state,Pageable pageable);
}
