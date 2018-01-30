package com.hafu365.fresh.service.goods;

import com.hafu365.fresh.core.entity.goods.Brand;
import com.hafu365.fresh.core.entity.store.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

/**
 * 品牌业务接口
 * Created by HuangWeizhen on 2017/8/14.
 */
public interface BrandService {

    /**
     * 保存品牌
     * @param brand
     * @return
     */
    Brand save(Brand brand);

    /**
     * 更新品牌
     * @param brand
     * @return
     */
    Brand update(Brand brand);

    /**
     * 关闭品牌
     * @param brandId
     */
    boolean closeBrand(String brandId);

    /**
     * 逻辑删除品牌
     * @param brandId
     * @return
     */
    boolean delete(String brandId);
    /**
     * 物理删除品牌
     * @param brandId
     * @return
     */
    boolean physicalDelete(String brandId);

    /**
     * 根据品牌id查询品牌
     * @param brandId
     * @return
     */
    Brand findById(String brandId);

    /**
     * 根据品牌标题查询品牌
     * @param brandTitle
     * @return
     */
    Brand findByBrandTitle(String brandTitle);

    /**
     * 多条件分页查询品牌
     * @param map_param
     * @return
     */
    Page<Brand> findBrand(Map<String,Object> map_param);

    /**
     * 查询分类下关联的品牌 && 店铺下的品牌(发布商品用品牌查询)
     * @return
     */
    List<Brand> findByGcAndStore(String classId, String storeId);

    /**
     * 查询分类下关联的品牌id
     * @param gcId
     * @return
     */
    List<String> findBrandIdListByGc(String gcId);

    /**
     * 设定默认品牌
     * @param brandId
     * @return
     */
    public Brand setBrandDefault(String brandId);

    /**
     * 查询默认品牌
     * @return
     */
    public Brand findBrandByTheDefaultTrue();

    /**
     * 查询店铺品牌
     * @param store
     * @return
     */
    List<Brand> findByStoreAndDelFalse(Store store);

}
