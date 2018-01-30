package com.hafu365.fresh.core.entity.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 用户设置
 * Created by SunHaiyang on 2017/8/24.
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "fresh_member_setting")
public class MemberSetting implements Serializable {

    /**
     * ID
     */
    @Id
    @GeneratedValue
    private long id;

    /**
     * 付款日期
     */
    private int settlementInterval = 5;

    /**
     * 出账日期
     */
    private int settlementDate = 15;

    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;



}
