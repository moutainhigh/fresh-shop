package com.hafu365.fresh.core.entity.voucher;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hafu365.fresh.core.entity.member.Member;
import com.hafu365.fresh.core.entity.order.Orders;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 代金券操作记录实体类
 * Created by HuangWeizhen on 2017/8/28.
 */
@Entity
@Data
@ToString
@NoArgsConstructor
@Table(name = "fresh_voucher_log")
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler","fieldHandler"})
public class VoucherLog implements Serializable{

    /**
     * 代金券操作记录id
     */
    @Id
    @GeneratedValue
    private long logId;

    /**
     * 关联的代金券
     */
    @ManyToOne
    @JoinColumn(name = "voucher_id")
    private Voucher voucher;

    /**
     * 操作类型
     */
    private String operation;

    /**
     * 关联的订单
     */
    @OneToOne
    @JoinColumn(name = "orders_id")
    private Orders orders;

    /**
     * 操作时间
     */
    private long operationTime;

    /**
     * 操作用户
     */
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    /**
     * 操作描述
     */
    @Lob
    private String description;
}
