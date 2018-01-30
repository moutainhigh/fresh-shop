package com.hafu365.fresh.core.entity.order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 预约订单 实体类
 * Created by zhaihuilin on 2017/7/21  11:50.
 */
@Data
@Entity
@ToString(exclude = "member")
@NoArgsConstructor
@JsonIgnoreProperties({"member"})
@Table(name = "fresh_order_make_order")
public class MakeOrder implements Serializable {

    /**
     * 预约订单编号
     */
    @Id
    @GenericGenerator(name = "sys-uid",strategy = "com.hafu365.fresh.core.utils.KeyGeneratorUtils",parameters = {
            @Parameter(name = "k",value = "MO")
    })
    @GeneratedValue(generator = "sys-uid")
    private String makeOrderId;

    /**
     * 创建时间
     */
    private  long createTime = new Date().getTime();

    /**
     * 更新时间
     */
    private  long updateTime = new Date().getTime();

    public MakeOrder(String username) {
        this.username = username;
    }

    @NotNull
    private String username;

//    @PostLoad
//    private void laod(){
//        this.username = this.member.getUsername();
//    }

    /**
     * 天订单列表信息
     */
//    @NonNull
//    @OneToMany(cascade = {},fetch = FetchType.LAZY,mappedBy = "makeOrder")
//    @JsonBackReference
//    private List<DayOrder> dayOrderList;

//    /**
//     * 所属用户
//     */
//    @JsonInclude(JsonInclude.Include.NON_NULL)
//    @ManyToOne(cascade = {CascadeType.MERGE},fetch = FetchType.EAGER)
//    @JoinColumn(name = "member_id")
//    @NonNull
//    private Member member;


}
