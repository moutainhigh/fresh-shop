package com.hafu365.fresh.service.goods;

import com.hafu365.fresh.core.entity.goods.ModifyPriceGoods;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Map;

/**
 * 修改价格商品业务接口
 * Created by HuangWeizhen on 2017/10/11.
 */
public interface ModifyPriceGoodsService {
    /**
     * 保存价格商品
     * @param modifyPriceGoods
     * @return
     */
    ModifyPriceGoods save(ModifyPriceGoods modifyPriceGoods);

    /**
     * 根据id搜索价格商品
     * @param modifyPriceId
     * @return
     */
    ModifyPriceGoods findById(long modifyPriceId);

    /**
     * 根据商品id查询价格商品
     * @param goodsId
     * @return
     */
    ModifyPriceGoods findByGoodsId(String goodsId);

    /**
     * 逻辑删除价格商品
     * @param modifyPriceId
     */
    boolean deleteById(long modifyPriceId);

    /**
     * 物理删除价格商品
     * @param modifyPriceId
     */
    boolean physicalDeleteById(long modifyPriceId);

    /**
     * 分页查询修改价格商品
     * @param map_param
     * @return
     */
    Page<ModifyPriceGoods> findPriceGoods(Map<String,Object> map_param);

    /**
     * 查询价格商品列表
     * @return
     */
    List<ModifyPriceGoods> findAll();
}
