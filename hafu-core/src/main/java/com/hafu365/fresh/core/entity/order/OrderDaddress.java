package com.hafu365.fresh.core.entity.order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *收件地址
 * Created by zhaihuilin on 2017/7/31  16:49.
 */
@Entity
@Data
@ToString
@NoArgsConstructor
@RequiredArgsConstructor
@JsonIgnoreProperties({"member"})
@Table(name = "fresh_order_daddress")
public class OrderDaddress {


    /**
     * 收货地址编号
     */
    @Id
    @GeneratedValue
    private long orderDaddressId;

    /**
     * 联系人姓名
     */
    @NonNull
    private String name;

    /**
     * 配送地址
     */

    private String address;

    /**
     * 配送电话
     */
    private String phone;
//    /**
//     * 所属用户
//     */
//    @ManyToOne(cascade = {},fetch =FetchType.EAGER)
//    @JoinColumn(name = "member_id")
//    @JsonBackReference
//    private Member member;

    /**
     * 所属用户
     */
    @NonNull
    private String username;

    /**
     * 是否默认
     */
    private  boolean isdefault=Boolean.FALSE;

    public OrderDaddress(OrderDaddress orderDaddress) {
        this.orderDaddressId = orderDaddress.getOrderDaddressId();
        this.name = orderDaddress.getName();
        this.address = orderDaddress.getAddress();
        this.phone = orderDaddress.getPhone();
        this.isdefault = orderDaddress.isIsdefault();
    }
}
