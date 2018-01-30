package com.hafu365.fresh.core.entity.voucher;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hafu365.fresh.core.entity.member.Member;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 代金券实体类
 * Created by HuangWeizhen on 2017/8/28.
 */
@Entity
@Data
@ToString
@NoArgsConstructor
@Table(name = "fresh_voucher")
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler","fieldHandler"})
public class Voucher implements Serializable{

    /**
     * 代金券id
     */
    @Id
    @GenericGenerator(name = "sys-uid", strategy = "com.hafu365.fresh.core.utils.KeyGeneratorUtils", parameters = {
            @Parameter(name = "k", value = "V")
    })
    @GeneratedValue(generator = "sys-uid")
    private String voucherId;

    /**
     * 代金券金额
     */
    private double money;

    /**
     * 代金券状态
     */
    private String state;

    /**
     * 代金券所属用户
     */
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    /**
     * 代金券描述
     */
    private String description;

    /**
     * 生效时间
     */
    private long effectiveTime;

    /**
     * 过期时间
     */
    private long indate;
}
