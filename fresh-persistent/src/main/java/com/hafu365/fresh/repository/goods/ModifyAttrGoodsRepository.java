package com.hafu365.fresh.repository.goods;

import com.hafu365.fresh.core.entity.goods.ModifyAttrGoods;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * 修改属性商品持久层接口
 * Created by HuangWeizhen on 2017/8/9.
 */
@Repository
public interface ModifyAttrGoodsRepository extends JpaRepository<ModifyAttrGoods,Long>,JpaSpecificationExecutor<ModifyAttrGoods> {

    /**
     * 通过id查询属性商品
     * @param modifyAttrId
     * @return
     */
    ModifyAttrGoods findByModifyAttrIdAndDelFalse(long modifyAttrId);

    /**
     * 通过商品id查询属性商品
     * @param goodsId
     * @return
     */
    ModifyAttrGoods findByGoodsIdAndDelFalse(String goodsId);

    /**
     * 分页查询属性商品
     * @param modifyAttrSpecification
     * @param pageable
     * @return
     */
    Page<ModifyAttrGoods> findAll(Specification<ModifyAttrGoods> modifyAttrSpecification, Pageable pageable);

    /**
     * 逻辑删除属性商品
     * @param modifyAttrId
     */
    @Query(value = "update ModifyAttrGoods mag set mag.del = true where mag.id = ?1 ")
    @Modifying
    void deleteByModifyAttrId(long modifyAttrId);

    /**
     * 根据商品id 物理删除属性商品
     * @param goodsId
     */
    void deleteByGoodsId(String goodsId);

}
