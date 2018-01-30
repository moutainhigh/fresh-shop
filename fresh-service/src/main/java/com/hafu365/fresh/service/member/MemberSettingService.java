package com.hafu365.fresh.service.member;

import com.hafu365.fresh.core.entity.member.Member;
import com.hafu365.fresh.core.entity.member.MemberSetting;
import org.apache.jackrabbit.core.security.user.MembershipCache;

import java.util.List;

/**
 * 用户设置
 * Created by SunHaiyang on 2017/8/25.
 */
public interface MemberSettingService {

    /**
     * 通过用户获得用户设置
     * @param member
     * @return
     */
    public MemberSetting findMemberSettingByMember(Member member);

    /**
     * 保存用户设置
     * @param memberSetting
     * @return
     */
    public MemberSetting saveMemberSetting(MemberSetting memberSetting);

    /**
     * 更新用户设置
     * @param memberSetting
     * @return
     */
    public MemberSetting updateMemberSetting(MemberSetting memberSetting);

    /**
     * 根据用户和对账日期获取用户设置
     * @param member
     * @param day
     * @return
     */
    public MemberSetting findMemberSettingByMemberAndDay(Member member,int day);

    /**
     * 根据对账日期获取用户设置
     * @param day
     * @return
     */
    public List<MemberSetting> findMemberSettingBysettlementDate(int day);

    /**
     * 根据付款日期获取用户设置
     * @param day
     * @return
     */
    public List<MemberSetting> findMemberSettingBySettlementInterval(int day);


}
