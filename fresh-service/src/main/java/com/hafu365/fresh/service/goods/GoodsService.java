package com.hafu365.fresh.service.goods;

import com.hafu365.fresh.core.entity.goods.Brand;
import com.hafu365.fresh.core.entity.goods.Goods;
import com.hafu365.fresh.core.entity.goods.GoodsClass;
import com.hafu365.fresh.core.entity.goods.GoodsStock;
import com.hafu365.fresh.core.entity.store.Store;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * 商品业务类接口
 * Created by HuangWeizhen on 2017/7/26.
 */
public interface GoodsService {
    /**
     * 添加商品
     * @param goods
     * @return
     */
    Goods save(Goods goods,GoodsStock goodsStock);
    /**
     * 更新商品
     * @param goods
     * @return
     */
    Goods update(Goods goods,GoodsStock goodsStock);

    /**
     * 更新商品
     * @param goods
     * @return
     */
    Goods updateGoods(Goods goods);

    /**
     * 根据id搜索商品
     * @param goodsId
     * @return
     */
    Goods findByGoodsId(String goodsId);

    /**
     * 根据商品编号获取商品
     * @param goodsId  ZHL
     * @return
     */
    Goods findGoodsByGoodsId(String goodsId);

    /**
     * 商品是否存在
     * @param goodsId
     * @return 存在且未删除的商品返回true
     */
    boolean isExist(String goodsId);

    /**
     * 多条件分页查询商品
     * @param map_param
     * @return
     */
    Page<Goods> findByCondition(Map<String,Object> map_param);

    /**
     * 多条件分页查询简单商品(查询商品id，商品标题，价格，第一张图片)
     * @param map_param
     * @return
     */
    Page<Goods> findSimpleGoodsByCondition(Map<String,Object> map_param);

    /**
     * 逻辑删除商品
     * @param goodsId
     * @return
     */
    boolean deleteByGoodsId( String goodsId);

    /**
     * 物理删除商品
     * @param goodsId
     * @return
     */
    boolean deleteGoods(String goodsId);

    /**
     * 审核商品
     */
    boolean checkEditGoods(long modifyGoodsId,String result);

    /**
     * 查询分类关联的商品
     * @return
     */
    List<Goods> findByGoodsClass(GoodsClass goodsClass);

    /**
     * 查询品牌关联的商品
     * @param brand
     * @return
     */
    List<Goods> findByBrandAndDelFalse(Brand brand);

    /**
     * 获取某店铺下所有的商品
     * @param store   ZHL
     * @return
     */
    List<Goods> findByStoreAndDelFalse(Store store);

    /**
     * 获取所有失效的商品编号
     * @param time  时间
     * @return
     */
    List<String> findDelGoods(long time);

    /**
     * 新增获取失效商品的集合
     * @return
     */
    List<Goods> findAllFailGoods();

}
