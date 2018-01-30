package com.hafu365.fresh.service.cart.impl;

import com.hafu365.fresh.core.entity.cart.Cart;
import com.hafu365.fresh.core.utils.StringUtils;
import com.hafu365.fresh.repository.cart.CartRepository;
import com.hafu365.fresh.service.cart.CartService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * 购物车 逻辑层
 * Created by zhaihuilin on 2017/7/21  14:15.
 */
@Service
@Log4j
@Transactional
public class CartServiceImpl implements CartService{

    @Autowired
    private CartRepository cartRepository;




    /**
     * 查询所有
     * @param pageable
     * @return
     */
    @Override
    public Page<Cart> findAll(Pageable pageable) {
        return cartRepository.findAll(pageable);
    }

    /**
     * 根据用户信息 分页查询
     * @param username
     * @param pageable
     * @return
     */
    @Override
    public Page<Cart> findCartByMember(String username, Pageable pageable) {
        return cartRepository.findAll(CartWhere(username),pageable);
    }

    /**
     * 根据用户查询 购物车信息 不分页
     * @param username
     * @return
     */
    @Override
    public List<Cart> findCartListByMember(String username) {
        return cartRepository.findCartByUsername(username);
    }

    /**
     * 保存购物车
     * @param cart
     * @return
     */
    @Override
    public Cart saveCart(Cart cart) {
        return cartRepository.save(cart);
    }
    /**
     * 编辑购物车
     * @param cart
     * @return
     */
    @Override
    public Cart updateCart(Cart cart) {

        return cartRepository.save(cart);
    }

    /**
     * 根据 购物车 编号进行删除
     * @param cartId
     * @return
     */
    @Override
    public boolean deleteCartByCartId(String cartId) {
        try {
            cartRepository.deleteCartByCartId(cartId);
            return true;
        }catch (Exception e){
            return false;
        }

    }
    /**
     * 根据  购物车编号查询 购物车信息
     * @param cartId
     * @return
     */
    @Override
    public Cart findCartByCartId(String cartId) {
        return cartRepository.findCartByCartId(cartId);
    }

    /**
     * 购物车 根据 用户 动态查询
     * @param username
     * @return
     */
    public static Specification<Cart> CartWhere(
            final String username
    ){
        return new Specification<Cart>(){

            @Override
            public Predicate toPredicate(Root<Cart> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                if(StringUtils.isNotEmpty(username)){
                    predicates.add(criteriaBuilder.equal(root.<String>get("username"),username));
                }
                return criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
            }
        };
    }
}
