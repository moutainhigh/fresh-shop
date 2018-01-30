package com.hafu365.fresh.service.cart;


import com.hafu365.fresh.core.entity.cart.Cart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 购物车 服务层
 * Created by zhaihuilin on 2017/7/21  14:14.
 */
public interface CartService {
    /**
     * 保存购物车
     * @param cart
     * @return
     */
    public  Cart saveCart(Cart cart);

    /**
     * 修改购物车
     * @param cart
     * @return
     */
    public  Cart updateCart(Cart cart);

    /**
     * 根据 购物车编号进行 物理删除
     * @param cartId
     * @return
     */
    public  boolean deleteCartByCartId(String cartId);

    /**
     * 根据 购物车 编号进行 查询购物车信息
     * @param cartId
     * @return
     */
    public Cart findCartByCartId(String cartId);
    /**
     * 动态查询
     * @param pageable
     * @return
     */
    public Page<Cart> findAll(Pageable pageable);
    /**
     * 根据用户信息查询 购物车列表信息 分页
     * @param username
     * @param pageable
     * @return
     */
    public Page<Cart> findCartByMember(String username, Pageable pageable);

    /**
     * 根据用户信息查询 购物车列表信息 不分页
     * @param username
     * @return
     */
    public List<Cart> findCartListByMember(String username);

}
