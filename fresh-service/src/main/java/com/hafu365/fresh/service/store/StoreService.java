package com.hafu365.fresh.service.store;


import com.hafu365.fresh.core.entity.member.Member;
import com.hafu365.fresh.core.entity.store.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

/**
 * 店铺 服务层
 * Created by zhaihuilin on 2017/7/21  14:51.
 */
public interface StoreService {

    /**
     * 保存店铺信息
     * @param store
     * @return
     */
    Store save(Store store);
    /**
     * 保存店铺信息
     * @param store
     * @return
     */
    Store update(Store store);
    /**
     * 关闭店铺
     * @param storeId
     */
    boolean closeStore(String storeId);

    /**
     * 逻辑删除店铺
     * @param storeId
     * @return
     */
    boolean deleteByStoreId(String storeId);

    /**
     * 物理删除店铺
     * @param storeId
     * @return
     */
    boolean deleteStore(String storeId);

    /**
     * 通过店铺id查询 店铺信息
     * @param storeId
     * @return
     */
    Store findByStoreId(String storeId);

    /**
     * 根据店铺名称进行查询
     * @param storeName
     * @return
     */
    Store findByStoreName(String storeName);

    /**
     * 多条件分页查询店铺
     * @param map_param
     * @return
     */
    Page<Store> findByCondition(Map<String,Object> map_param);

    /**
     * 是否存在店铺
     * @param storeId
     * @return
     */
    boolean isExist(String storeId);

    /**
     * 通过店员id查询店铺
     * @param memberId
     * @return
     */
    Store findBychildMember(String memberId);

    /**
     * 查询用户店铺
     * @param member
     * @return
     */
    Store findByMember(Member member);

    /**
     * 查询审核通过的店铺
     * @param state
     * @return
     */
    List<Store> findByDelFalseAndState(String state);

    /**
     * 设定默认店铺
     * @param storeId
     * @return
     */
    public Store setStoreDefault(String storeId);

    /**
     * 查询默认店铺
     * @return
     */
    public Store findStoreByTheDefaultTrue();


}
