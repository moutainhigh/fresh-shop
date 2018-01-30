package com.hafu365.fresh.core.entity.bills;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


/**
 * 交易信息
 * Created by SunHaiyang on 2017/10/12.
 */
@Entity
@Data
@ToString
@Table(name = "fresh_bills_pay_info")
public class PayInfo implements Serializable {

    /**
     * ID
     */
    @Id
    @GeneratedValue
    private long id;

    /**
     * 交易单号
     */
    @Column(unique = true)
    private String paySn;

    /**
     * 交易时间
     */
    private long payTime = new Date().getTime();

    /**
     * 账单信息
     */
    @OneToOne
    @JoinColumn(name = "billsId")
    private Bills bills;
}
