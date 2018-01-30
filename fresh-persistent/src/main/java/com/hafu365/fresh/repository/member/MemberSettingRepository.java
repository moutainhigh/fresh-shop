package com.hafu365.fresh.repository.member;

import com.hafu365.fresh.core.entity.member.Member;
import com.hafu365.fresh.core.entity.member.MemberSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户设置
 * Created by SunHaiyang on 2017/8/24.
 */
@Repository
public interface MemberSettingRepository extends JpaRepository<MemberSetting,Long> {

    /**
     * 通过Member 获取用户设置
     * @param member
     * @return
     */
    public MemberSetting findByMember(Member member);

    /**
     * 根据member 和 当天日期获取用户设置
     * @param member
     * @param day
     * @return
     */
    @Query(value = "select m from MemberSetting m where m.member = ?1 and m.settlementDate = ?2")
    public MemberSetting findByMemberAndSettlementDate(Member member,int day);

    /**
     * 根据出账日期获取设置
     * @param day
     * @return
     */
    public List<MemberSetting> findMemberSettingBysettlementDate(int day);

    /**
     * 根据付款日期获取设置
     * @param day
     * @return
     */
    public List<MemberSetting> findMemberSettingBySettlementInterval(int day);

}
