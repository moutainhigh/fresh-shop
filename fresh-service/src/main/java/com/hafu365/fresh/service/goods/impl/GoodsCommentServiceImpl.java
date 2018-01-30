package com.hafu365.fresh.service.goods.impl;

import com.hafu365.fresh.core.entity.common.UtilPage;
import com.hafu365.fresh.core.entity.goods.Goods;
import com.hafu365.fresh.core.entity.goods.GoodsComment;
import com.hafu365.fresh.core.utils.StringUtils;
import com.hafu365.fresh.repository.goods.GoodsCommentRepository;
import com.hafu365.fresh.service.goods.GoodsCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 商品评论业务实现类
 * Created by HuangWeizhen on 2017/8/16.
 */
@Transactional
@Service
public class GoodsCommentServiceImpl implements GoodsCommentService {

    @Autowired
    private GoodsCommentRepository commentRepository;

    @Override
    public GoodsComment save(GoodsComment goodsComment) {
        return commentRepository.save(goodsComment);
    }

    @Override
    public boolean delete(long commentId) {
        try{
            GoodsComment comment = commentRepository.getOne(commentId);
            //物理删除评论与用户的关联
            comment.setMember(null);
            commentRepository.save(comment);
            commentRepository.delete(commentId);
            return true;
        }catch (Exception e){
            return false;
        }

    }

    @Override
    public GoodsComment findById(long commentId) {
        return commentRepository.getOne(commentId);
    }

    @Override
    public List<GoodsComment> findGoodsComment(Goods goods, String state) {
        //获取商品一级评论
        List<GoodsComment> oldCommentList = commentRepository.findByGoodsAndOldCommentIsNullAndStateOrderByCreateTimeDesc(goods,state);
        if(oldCommentList != null && oldCommentList.size() > 0){
            for(GoodsComment commentParent : oldCommentList){
                //获取自身评论树
                commentParent = getComment(goods,commentParent,state);
            }
            return oldCommentList;
        }else{
            return null;
        }
    }

    @Override
    public Page<GoodsComment> findCommentByCondition(Map<String, Object> map_param) {
        final List<GoodsComment> commentList = (ArrayList<GoodsComment>) map_param.get("commentList");
        final GoodsComment comment = (GoodsComment) map_param.get("comment");
        final long startTime = (Long) map_param.get("startTime");
        final long endTime = (Long) map_param.get("endTime");
        final UtilPage page = (UtilPage)map_param.get("page");
        final Sort sort = new Sort(page.getDirection(),page.getPageSort());
        Pageable pageable = new PageRequest(page.getPageNum(), page.getPageSize(), sort);
        return commentRepository.findAll(new Specification<GoodsComment>() {
            @Override
            public Predicate toPredicate(Root<GoodsComment> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
               List<Predicate> predicates = new ArrayList<Predicate>();
                //拼接评论id查询
                if(comment.getCommentId() != 0){
                    predicates.add(cb.equal(root.<Long>get("commentId"),comment.getCommentId()));
                }
                //拼接商品查询
                if(comment.getGoods() != null){
                    predicates.add(cb.equal(root.<String>get("goods"),comment.getGoods()));
                }
                //拼接父评论查询

                if(commentList != null && commentList.size() > 0){
                    List<Predicate> or_predicate_old_comment = new ArrayList<Predicate>();
                    for(GoodsComment commentParent : commentList){
                        or_predicate_old_comment.add(cb.equal(root.<Long>get("oldComment"),commentParent));
                    }
                    if(or_predicate_old_comment.size() > 0){
                        Predicate old_comment_predicate_or = cb.or(or_predicate_old_comment.toArray(new Predicate[or_predicate_old_comment.size()]));
                        predicates.add(old_comment_predicate_or);
                    }

                }

                //拼接用户查询
                if(comment.getMember() != null){
                    predicates.add(cb.equal(root.<String>get("member"),comment.getMember()));
                }
                //拼接状态查询
                if(StringUtils.isNotEmpty(comment.getState())){
                    predicates.add(cb.equal(root.<String>get("state"),comment.getState()));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, pageable);
    }

    @Override
    public List<GoodsComment> findByOldComment(GoodsComment oldComment) {
        List<GoodsComment> commentList = new ArrayList<GoodsComment>();
        commentList.add(oldComment);
        List<GoodsComment> childList = commentRepository.findByOldCommentOrderByCreateTimeDesc(oldComment);
        if(childList != null && childList.size() > 0){
            for(GoodsComment comment : childList){
                List<GoodsComment> child_child_list = findByOldComment(comment);
                commentList.addAll(child_child_list);
            }
            return commentList;
        }else{
            return commentList;
        }
    }

    /**
     * 根据父评论获取评论树
     * @param goods
     * @param commentParent
     * @param state
     * @return
     */
    public GoodsComment getComment(Goods goods, GoodsComment commentParent, String state){

        List<GoodsComment> childList = commentRepository.findByGoodsAndOldCommentAndStateOrderByCreateTimeDesc(goods,commentParent,state);
        if(childList != null && childList.size() > 0){  //有子集遍历
            for(GoodsComment childComment : childList){
                childComment = getComment(goods,childComment,state);//返回自身
            }
            commentParent.setChildComment(childList);//更新父评论
            return commentParent;

        }else{  //无子集返回本身
            return commentParent;
        }
    }
}
