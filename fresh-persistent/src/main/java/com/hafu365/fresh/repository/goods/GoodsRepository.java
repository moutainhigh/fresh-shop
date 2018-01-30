package com.hafu365.fresh.repository.goods;

import com.hafu365.fresh.core.entity.goods.Brand;
import com.hafu365.fresh.core.entity.goods.Goods;
import com.hafu365.fresh.core.entity.goods.GoodsClass;
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
 * 商品持久层接口
 * Created by HuangWeizhen on 2017/7/26.
 */
@Repository
public interface GoodsRepository extends JpaRepository<Goods,String> , JpaSpecificationExecutor<Goods> {
    /**
     * 根据id查询商品
     * @param goodsId
     * @return
     */
    Goods findByGoodsIdAndDelFalse(String goodsId);

    /**
     * 多条件分页查询商品
     * @param goodsSpecification
     * @param pageable
     * @return
     */
    Page<Goods> findAll(Specification<Goods> goodsSpecification, Pageable pageable);

    /**
     * 逻辑删除商品
     * @param goodsId
     */
    @Query(value = "update Goods g set g.del = true where g.id = ?1")
    @Modifying
    void deleteByGoodsId(String goodsId);

    /**
     * 查询品牌关联的商品
     * @param brand
     * @return
     */
    List<Goods> findByBrand(Brand brand);

    /**
     * 查询分类关联的商品
     * @return
     */
    List<Goods> findByGoodsClassAndDelFalse(GoodsClass goodsClass);

    /**
     * 查询品牌关联的商品
     * @param brand
     * @return
     */
    List<Goods> findByBrandAndDelFalse(Brand brand);

    /**
     * 查询店铺关联的商品
     * @param store
     * @return
     */
    List<Goods> findByStore(Store store);

    /**
     * 获取所有失效的商品编号
     * @param time
     * @return
     */
    @Query("select g.goodsId from Goods g where g.del = true or g.soldInTime > ?1 or g.soldOutTime < ?1")
    List<String> findDelGoods(long time);

}
