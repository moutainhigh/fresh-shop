package com.hafu365.fresh.repository.goods;

import com.hafu365.fresh.core.entity.goods.ModifyPriceGoods;
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
 * 修改价格商品持久层接口
 * Created by HuangWeizhen on 2017/10/11.
 */
@Repository
public interface ModifyPriceGoodsRepository  extends JpaRepository<ModifyPriceGoods,Long>,JpaSpecificationExecutor<ModifyPriceGoods> {
    /**
     * 通过id查询价格商品
     * @param modifyPriceId
     * @return
     */
    ModifyPriceGoods findByModifyPriceIdAndDelFalse(long modifyPriceId);

    /**
     * 通过商品id查询价格商品
     * @param goodsId
     * @return
     */
    ModifyPriceGoods findByGoodsIdAndDelFalse(String goodsId);

    /**
     * 分页查询价格商品
     * @param modifyPriceSpecification
     * @param pageable
     * @return
     */
    Page<ModifyPriceGoods> findAll(Specification<ModifyPriceGoods> modifyPriceSpecification, Pageable pageable);

    /**
     * 查询价格商品列表
     * @param modifyPriceSpecification
     * @return
     */
    List<ModifyPriceGoods> findAll(Specification<ModifyPriceGoods> modifyPriceSpecification);

    /**
     * 逻辑删除价格商品
     * @param modifyPriceId
     */
    @Query(value = "update ModifyPriceGoods mag set mag.del = true where mag.id = ?1 ")
    @Modifying
    void deleteByModifyPriceId(long modifyPriceId);

    /**
     * 根据商品id 物理删除价格商品
     * @param goodsId
     */
    void deleteByGoodsId(String goodsId);
}
