package com.hafu365.fresh.repository.cart;


import com.hafu365.fresh.core.entity.cart.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 购物车 执久层
 * Created by zhaihuilin on 2017/7/21  14:12.
 */
@Repository
public interface CartRepository extends JpaRepository<Cart,Long>,JpaSpecificationExecutor<Cart>{
    /**
     * 根据 购物车编号进行删除   物理删除   主要实现购物车列表中的删除
     * @param cartId
     */
    public  void   deleteCartByCartId(String cartId);
    /**
     * 根据 购物车 进行 查询 购物车信息
     * @param cartId
     * @return
     */
    public  Cart findCartByCartId(String cartId);

    /**
     * 根据用户名获取购物车列表信息
     * @param username
     * @return
     */
    public List<Cart> findCartByUsername(String username);
}
