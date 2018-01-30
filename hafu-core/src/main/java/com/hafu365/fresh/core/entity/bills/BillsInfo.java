package com.hafu365.fresh.core.entity.bills;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hafu365.fresh.core.entity.constant.BillsInfoConstant;
import com.hafu365.fresh.core.entity.order.Orders;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 账单信息
 * Created by SunHaiyang on 2017/8/24.
 */
@Data
@ToString
@Entity
@Table(name = "fresh_bills_info")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class BillsInfo implements Serializable {

    /**
     * ID
     */
    @Id
    @GenericGenerator(name = "sys-uid",strategy = "com.hafu365.fresh.core.utils.KeyGeneratorUtils",parameters = {
            @Parameter(name = "k",value = "BO")
    })
    @GeneratedValue(generator = "sys-uid")
    private String id;

    /**
     * 金额
     */
    @NonNull
    @Column(scale = 2)
    private double money;

    /**
     * 创建时间
     */
    private long createTime = new Date().getTime();

    /**
     * 账单类型
     */
    @NonNull
    private int type;

    /**
     * 操作来源
     */
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private Orders order;

    /**
     * 账单描述
     */
    private String description;


    @Transient
    private String orderId;

    @PostLoad
    private void load(){
        if(order != null){
            this.orderId = this.order.getOrdersId();
        }
    }


    @ManyToOne
    @JoinTable(name = "fresh_bills_bills_info",joinColumns = {
            @JoinColumn(name = "bills_id")
    },inverseJoinColumns = {
            @JoinColumn(name = "bills_info_id")
    })
    @JsonBackReference
    private Bills bills;

    public BillsInfo(double money, BillsInfoConstant type, String description) {
        this.money = money;
        this.type = type.getState();
        this.description = description;
    }

    public void setType(BillsInfoConstant billsInfoConstant) {
        this.type = billsInfoConstant.getState();
    }
}
