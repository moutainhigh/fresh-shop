package com.hafu365.fresh.service.goods;

import com.hafu365.fresh.core.entity.goods.GoodsStock;
import org.springframework.data.domain.Page;

import java.util.Map;

/**
 * 商品库存业务接口
 * Created by HuangWeizhen on 2017/8/22.
 */
public interface GoodsStockService {

    /**
     * 新增或更新库存
     * @param goodsStock
     * @return
     */
    GoodsStock saveOrUpdate(GoodsStock goodsStock);

    /**
     * 根据id查询库存
     * @param stockId
     * @return
     */
    GoodsStock findByStockId(String stockId);

    /**
     * 根据条件分页查询库存
     * @param paramMap
     * @return
     */
    Page<GoodsStock> findByCondition(Map<String, Object> paramMap);
}
