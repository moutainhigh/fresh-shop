package com.hafu365.fresh.repository.goods;

import com.hafu365.fresh.core.entity.goods.Goods;
import com.hafu365.fresh.core.entity.goods.GoodsComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 商品评论持久层接口
 * Created by HuangWeizhen on 2017/8/16.
 */
@Repository
public interface GoodsCommentRepository extends JpaRepository<GoodsComment,Long>,JpaSpecificationExecutor<GoodsComment>{

    /**
     * 获取商品引用评论（一级评论）
     * @param state
     * @return
     */
    List<GoodsComment> findByGoodsAndOldCommentIsNullAndStateOrderByCreateTimeDesc(Goods goods, String state);

    /**
     * 根据商品上级评论获取相应状态的子级评论
     * @param oldComment
     * @param state
     * @return
     */
    List<GoodsComment> findByGoodsAndOldCommentAndStateOrderByCreateTimeDesc(Goods goods, GoodsComment oldComment, String state);

    /**
     * 分页查询
     * @param commentSpecification
     * @param pageable
     * @return
     */
    Page<GoodsComment> findAll(Specification<GoodsComment> commentSpecification, Pageable pageable);

    /**
     * 根据父评论查询所有子评论
     * @param oldComment
     * @return
     */
    List<GoodsComment> findByOldCommentOrderByCreateTimeDesc(GoodsComment oldComment);

    /**
     * 根据商品删除评论
     * @param goods
     */
    void deleteByGoods(Goods goods);


}
