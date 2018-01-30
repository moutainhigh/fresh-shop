package com.hafu365.fresh.service.goods;

import com.hafu365.fresh.core.entity.goods.ModifyAttrGoods;
import org.springframework.data.domain.Page;

import java.util.Map;

/**
 * 修改属性商品业务接口
 * Created by HuangWeizhen on 2017/8/9.
 */
public interface ModifyAttrGoodsService {

    /**
     * 保存属性商品
     * @param modifyAttrGoods
     * @return
     */
    ModifyAttrGoods save(ModifyAttrGoods modifyAttrGoods);

    /**
     * 根据id搜索属性商品
     * @param modifyAttrId
     * @return
     */
    ModifyAttrGoods findById(long modifyAttrId);

    /**
     * 根据商品id查询属性商品
     * @param goodsId
     * @return
     */
    ModifyAttrGoods findByGoodsId(String goodsId);

    /**
     * 逻辑删除属性商品
     * @param modifyAttrId
     */
    boolean deleteById(long modifyAttrId);

    /**
     * 物理删除属性商品
     * @param modifyAttrId
     */
    boolean physicalDeleteById(long modifyAttrId);

    /**
     * 分页查询修改属性商品
     * @param map_param
     * @return
     */
    Page<ModifyAttrGoods> findAttrGoods(Map<String,Object> map_param);
}
