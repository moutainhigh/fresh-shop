package com.hafu365.fresh.service.goods;

import com.hafu365.fresh.core.entity.goods.Goods;
import com.hafu365.fresh.core.entity.goods.GoodsComment;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * 商品评论业务接口
 * Created by HuangWeizhen on 2017/8/16.
 */
public interface GoodsCommentService {
    /**
     * 保存or更新商品评论
     * @param goodsComment
     * @return
     */
    GoodsComment save(GoodsComment goodsComment);

    /**
     * 根据id删除评论
     * @param commentId
     * @return
     */
    boolean delete(long commentId);

    /**
     * 根据id查询评论
     * @param commentId
     * @return
     */
    GoodsComment findById(long commentId);

    /**
     * 获取商品的评论
     * @param goods
     * @param state
     * @return
     */
    List<GoodsComment> findGoodsComment(Goods goods, String state);

    /**
     * 分页查询评论
     * @param map_param
     * @return
     */
    Page<GoodsComment> findCommentByCondition(Map<String,Object> map_param);

    /**
     * 根据父评论查询所有子评论
     * @param oldComment
     * @return
     */
    List<GoodsComment> findByOldComment(GoodsComment oldComment);
}
