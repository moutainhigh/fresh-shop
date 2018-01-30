package com.hafu365.fresh.service.order;

import com.hafu365.fresh.core.entity.order.MakeOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 预约订单的 服务层
 * Created by zhaihuilin on 2017/7/21  14:41.
 */
public interface MakeOrderService {

    /**
     * 新增预约订单
     * @param makeOrder
     * @return
     */
     public MakeOrder saveMakeOrder(MakeOrder makeOrder);

    /**
     * 编辑预约订单
     * @param makeOrder
     * @return
     */
     public MakeOrder updateMakeOrder(MakeOrder makeOrder);

    /**
     * 根据预约订单编号进行查询
     * @param makeOrderId   预约订单编号
     * @return
     */
     public MakeOrder findMakeOrderByMakeOrderId(String makeOrderId);

    /**
     *根据预约订单编号进行删除
     * @param makeOrderId
     * @return
     */
     public  boolean deleteMakeOrderByMakeOrderId(String makeOrderId);


     //public SimpleMakeOrder findSimpleMakeOrOrderByMember(String makeOrderId);

    /**
     * 根据 条件 动态 查询预约订单
     * @param makeOrder    预约订单
     * @param username       所属用户
     * @param CstartDate   创建开始时间
     * @param CendDate     创建结束时间
     * @param UstartDate   编辑开始时间
     * @param UendDate     编辑结束时间
     * @param pageable
     * @return
     */
     public Page<MakeOrder> findAllMakeOrder(
             MakeOrder makeOrder,
             String username,
             Long CstartDate,
             Long CendDate,
             Long UstartDate,
             Long UendDate,
             Pageable pageable
         );

     public MakeOrder findMakeOrderByMember(String username);

    /**
     * 根据 用户条件 动态 查询预约订单
     * @param username
     * @param pageable
     * @return
     */
     public Page<MakeOrder> findAllMakeOrderByMember(
             String username,
             Pageable pageable
     );

    /**
     * 获取所有的预订单列表信息
     * @return
     */
    public List<MakeOrder> getMakeOrderList();



}
