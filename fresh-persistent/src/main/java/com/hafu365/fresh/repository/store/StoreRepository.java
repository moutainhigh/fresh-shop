package com.hafu365.fresh.repository.store;

import com.hafu365.fresh.core.entity.goods.Brand;
import com.hafu365.fresh.core.entity.member.Member;
import com.hafu365.fresh.core.entity.store.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 店铺持久层接口
 * Created by HuangWeizhen on 2017/8/8.
 */
@Repository
public interface StoreRepository extends JpaRepository<Store,String>,JpaSpecificationExecutor<Store> {

    /**
     * 根据店铺id进行查询
     * @param storeId
     * @return
     */
    Store findByStoreIdAndDelFalse(String storeId);

    /**
     * 根据店铺名称进行查询
     * @param storeName
     * @return
     */
    Store findByStoreNameAndDelFalse(String storeName);

    /**
     * 多条件分页查询店铺
     * @param storeSpecification
     * @param pageable
     * @return
     */
    Page<Store> findAll(Specification<Store> storeSpecification, Pageable pageable);

    /**
     * 逻辑删除店铺
     * @param storeId
     */
    @Query(value = "update Store s set s.del = true where s.id = ?1")
    @Modifying
    void deleteByStoreId(String storeId);

    /**
     * 关闭店铺
     * @param state
     * @param storeId
     */
    @Query(value = "update Store s set s.state = ?1 where s.id = ?2")
    @Modifying
    void closeStore(String state, String storeId);

    /**
     * 通过店员查询店铺
     * @param memberId
     * @return
     */
    @Query(value = "select s.* from fresh_store s inner join (select * from fresh_store_member sm where sm.member_id = ?1) sml on s.store_id = sml.store_id",nativeQuery = true)
    Store findBychilMember(String memberId);

    /**
     * 查询用户店铺
     * @param member
     * @return
     */
    Store findByMemberAndDelFalse(Member member);

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
    @Query(value = "update Store s set s.theDefault = true where s.storeId = ?1")
    public Store setStoreDefault(String storeId);

    /**
     * 查询默认店铺
     * @return
     */
    public Store findStoreByTheDefaultTrue();

}
