package com.hafu365.fresh.core.entity.bills;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hafu365.fresh.core.entity.constant.BillsConstant;
import com.hafu365.fresh.core.entity.member.Member;
import com.hafu365.fresh.core.entity.member.MemberInfo;
import lombok.*;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.List;

/**
 * 账单
 * Created by SunHaiyang on 2017/8/24.
 */
@Data
@ToString
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Entity
@JsonIgnoreProperties({"member"})
@Table(name = "fresh_bills")
public class Bills implements Serializable {

    /**
     * ID
     */
    @Id
    @GenericGenerator(name = "sys-uid",strategy = "com.hafu365.fresh.core.utils.KeyGeneratorUtils",parameters = {
            @Parameter(name = "k",value = "BS")
    })
    @GeneratedValue(generator = "sys-uid")
    private String id;

    /**
     * 所属用户
     */
    @NonNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;


    /**
     * 账单金额
     */
    @Column(scale = 2)
    private double money;

    /**
     * 付款金额
     */
    @Column(scale = 2)
    private double paymentMoney;

    /**
     * 优惠金额
     */
    @Column(scale = 2)
    private double coupon;

    /**
     * 付款时间
     */
    private long paymentTime;

    /**
     * 创建账单时间
     */
    private long generatedBillsTime;

    /**
     * 期号
     */
    private String issue;

    /**
     * 账单描述
     */
    @Lob
    private String description;

    /**
     * 方便查看用户名
     */
    @Transient
    private String username;


    /**
     * 账单状态
     */
    @NonNull
    private int state;

    public void setState(BillsConstant billsConstant) {
        this.state = billsConstant.getState();
    }

    public Bills(String id,double money,double paymentMoney) {
        this.money = money;
        this.id = id;
        this.paymentMoney = paymentMoney;
    }

    public void setState(int state) {
        this.state = state;
    }

    @OneToMany(mappedBy = "bills",fetch = FetchType.LAZY,cascade = {CascadeType.ALL})
    @JsonManagedReference
    private List<BillsInfo> billsInfos;

    @PostLoad
    public void load(){
        if(this.member != null){
            this.username = this.member.getUsername();
        }
    }


}
