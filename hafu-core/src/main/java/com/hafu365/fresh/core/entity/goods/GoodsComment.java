package com.hafu365.fresh.core.entity.goods;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hafu365.fresh.core.entity.member.Member;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * 商品评论实体类
 * Created by HuangWeizhen on 2017/7/27.
 */
@Entity
@Data
@ToString
@NoArgsConstructor
@Table(name = "fresh_goods_comment")
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler","fieldHandler"})
public class GoodsComment implements Serializable {

    /**
     * 商品评论id
     */
    @Id
    @GeneratedValue
    private long commentId;

    /**
     * 评论的商品
     */
    @ManyToOne
    @JoinColumn(name = "goods_id")
    private Goods goods;

    /**
     * 引用的评论（父级评论）
     */
    @ManyToOne
    @JoinColumn(name = "oldComment_id")
    @JsonBackReference
    private GoodsComment oldComment;

    /**
     * 被引用的评论（子级评论）
     */
    @OneToMany(targetEntity = GoodsComment.class ,mappedBy = "oldComment")
    private List<GoodsComment> childComment;

    /**
     * 评论内容
     */
    @Lob
    private StringBuffer content;

    /**
     * 发布时间
     */
    private long createTime;

    /**
     * 发布用户
     */
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    /**
     * 评论状态
     */
    private String state;
}
