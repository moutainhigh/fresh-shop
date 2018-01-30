package com.hafu365.fresh.service.goods;

import com.hafu365.fresh.core.entity.goods.GoodsClass;

import java.util.List;

/**
 * 商品分类业务接口
 * Created by HuangWeizhen on 2017/7/27.
 */
public interface GoodsClassService {

    /**
     * 添加分类
     * @param goodsClass
     * @return
     */
    GoodsClass save(GoodsClass goodsClass);

    /**
     * 更新分类
     * @param goodsClass
     * @return
     */
    GoodsClass update(GoodsClass goodsClass);

    /**
     * 获取显示的商品分类列表
     * @return
     */
    List<GoodsClass> findShowGcList();

    /**
     * 获取商品分类列表(包含显示与不显示)
     * @return
     */
    List<GoodsClass> findGcList();

    /**
     * 根据id搜索分类
     * @return
     */
    GoodsClass findById(String classId);

    /**
     * 分类是否存在
     * @param classId
     * @return
     */
    boolean isExist(String classId);

    /**
     * 逻辑删除分类
     * @param classId
     * @return
     */
    boolean deleteByClassId(String classId);

    /**
     * 物理删除分类
     * @param classId
     * @return
     */
    boolean physicalDelete(String classId);

    /**
     * 查询父类关联的子类
     * @param oldClass
     * @return
     */
    List<GoodsClass> findGcChildren(GoodsClass oldClass);


}
