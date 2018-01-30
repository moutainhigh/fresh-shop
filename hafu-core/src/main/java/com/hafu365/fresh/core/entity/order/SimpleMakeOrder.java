package com.hafu365.fresh.core.entity.order;

import com.hafu365.fresh.core.entity.member.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhaihuilin on 2017/10/25  13:14.
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SimpleMakeOrder implements Serializable {

    private String makeOrderId;

    private List<SimpleDayOrder> dayOrderList;

    private Member member;
}
