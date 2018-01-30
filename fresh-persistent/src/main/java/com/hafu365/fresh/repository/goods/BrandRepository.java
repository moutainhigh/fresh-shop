package com.hafu365.fresh.repository.goods;

import com.hafu365.fresh.core.entity.goods.Brand;
import com.hafu365.fresh.core.entity.member.Role;
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
 * 品牌持久层接口
 * Created by HuangWeizhen on 2017/8/14.
 */
@Repository
public interface BrandRepository extends JpaRepository<Brand,String>,JpaSpecificationExecutor<Brand> {
    /**
     * 根据品牌id查询品牌
     * @return
     */
    Brand findByBrandIdAndDelFalse(String brandId);

    /**
     * 根据品牌标题查询品牌
     * @param brandTitle
     * @return
     */
    Brand findByBrandTitle(String brandTitle);

    /**
     * 多条件分页查询品牌
     * @param brandSpecification
     * @param pageable
     * @return
     */
    Page<Brand> findAll(Specification<Brand> brandSpecification, Pageable pageable);

    /**
     * 逻辑删除品牌
     * @param brandId
     */
    @Query(value = "update Brand b set b.del = true where b.id = ?1")
    @Modifying
    void deleteByBrandId(String brandId);

    /**
     * 关闭品牌
     * @param state
     * @param brandId
     */
    @Query(value = "update Brand b set b.state = ?1 where b.id = ?2")
    @Modifying
    void closeBrand(String state,String brandId);

    /**
     * 查询店铺品牌
     * @param store
     * @return
     */
    List<Brand> findByStoreAndDelFalse(Store store);

    /**
     * 查询分类下关联的品牌 && 店铺下的品牌(发布商品用品牌查询)
     * @return
     */
    @Query(value = "SELECT b.* from ((select b.* from fresh_goods_brand b inner join (select * from fresh_brand_goods_class bgc where bgc.goods_class_id = ?1) fbgc on b.brand_id = fbgc.brand_id ) union (SELECT b.* from fresh_goods_brand b WHERE b.store_id = ?2)) b WHERE b.state = 'BRAND_STATE_CHECK_ON' and b.del = false",nativeQuery = true)
    List<Brand> findByGcAndStore(String classId,String storeId);

    /**
     * 查询分类下关联的品牌id
     * @param gcId
     * @return
     */
    @Query(value = "select b.* from fresh_goods_brand b INNER JOIN (SELECT * from fresh_brand_goods_class where goods_class_id = ?1) bgc on b.brand_id = bgc.brand_id ",nativeQuery = true)
    List<Brand> findBrandIdListByGc(String gcId);

    /**
     * 设定默认品牌
     * @param brandId
     * @return
     */
    @Query(value = "update Brand b set b.theDefault = true where b.brandId = ?1")
    public Brand setBrandDefault(String brandId);

    /**
     * 查询默认品牌
     * @return
     */
    public Brand findBrandByTheDefaultTrue();

}
