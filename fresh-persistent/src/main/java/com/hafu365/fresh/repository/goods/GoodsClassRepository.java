package com.hafu365.fresh.repository.goods;

import ch.qos.logback.core.joran.action.IADataForComplexProperty;
import com.hafu365.fresh.core.entity.goods.GoodsClass;
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
 * 商品分类持久层接口
 * Created by HuangWeizhen on 2017/7/27.
 */
@Repository
public interface GoodsClassRepository extends JpaRepository<GoodsClass,String> ,JpaSpecificationExecutor<GoodsClass>{
    /**
     * 查询一级分类（包括显示与不显示）
     * @return
     */
    List<GoodsClass> findByOldClassIsNullAndDelFalseOrderByOrderNum();

    /**
     * 根据父类获取子类（包括显示与不显示）
     * @return
     */
    List<GoodsClass> findByOldClassAndDelFalseOrderByOrderNum(GoodsClass oldClass);

    /**
     * 分页查询分类
     * @param goodsClassSpecification
     * @param pageable
     * @return
     */
    Page<GoodsClass> findAll(Specification<GoodsClass> goodsClassSpecification, Pageable pageable);

    /**
     * 根据id搜索分类
     * @param classId
     * @return
     */
    GoodsClass findByClassIdAndDelFalse(String classId);

    /**
     * 逻辑删除分类
     * @param classId
     */
    @Query(value = "update GoodsClass gc set gc.del = true where gc.id = ?1")
    @Modifying
    void deleteByClassId(String classId);

    /**
     * 查询父类关联的子类
     * @param oldClass
     * @return
     */
    List<GoodsClass> findByOldClassAndDelFalse(GoodsClass oldClass);

    /**
     * 查询前端显示的一级分类
     * @return
     */
    @Query(value = "select new GoodsClass(gc.classId,gc.classTitle,gc.pics)from GoodsClass gc where gc.del = false and gc.gcShow = true and gc.oldClass is null order by orderNum")
    List<GoodsClass> findGoodsClassByShow();

    /**
     * 查询父类下所有前端显示的子分类
     * @return
     */
    @Query(value = "select new GoodsClass(gc.classId,gc.classTitle,gc.pics)from GoodsClass gc where gc.del = false and gc.gcShow = true and gc.oldClass = ?1 order by orderNum")
    List<GoodsClass> findSimpleGoodsClass(GoodsClass parentClass);

}
