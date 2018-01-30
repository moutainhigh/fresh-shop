package com.hafu365.fresh.goods.controller.goods;

import com.hafu365.fresh.core.entity.common.ReturnMessages;
import com.hafu365.fresh.core.entity.common.UtilPage;
import com.hafu365.fresh.core.entity.constant.RequestState;
import com.hafu365.fresh.core.entity.constant.StateConstant;
import com.hafu365.fresh.core.entity.goods.Goods;
import com.hafu365.fresh.core.entity.goods.GoodsComment;
import com.hafu365.fresh.core.entity.member.Member;
import com.hafu365.fresh.core.utils.SecurityUtils;
import com.hafu365.fresh.core.utils.StringUtils;
import com.hafu365.fresh.service.goods.GoodsCommentService;
import com.hafu365.fresh.service.goods.GoodsService;
import com.hafu365.fresh.service.member.MemberService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品评论
 * Created by HuangWeizhen on 2017/8/16.
 */
@RestController
@RequestMapping("/comment")
public class GoodsCommentsController {

    @Autowired
    private GoodsCommentService commentService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private MemberService memberService;

    /**
     * 添加评论
     * @param goodsId       评论的商品id
     * @param oldCommentId  父级评论id[可空]
     * @param content       评论内容
     * @return
     */
    @RequestMapping("/save")
    public ReturnMessages saveComment(@RequestParam(name = "goodsId",required = true)String goodsId,
                                      @RequestParam(name = "oldCommentId",required = false)String oldCommentId,
                                      @RequestParam(name = "content",required = true)String content,
                                      HttpServletRequest request
    ){

        GoodsComment comment = new GoodsComment();
        //设置商品
        if(StringUtils.isNotEmpty(goodsId)){
            Goods goods = goodsService.findByGoodsId(goodsId);
            if(goods == null){
                return new ReturnMessages(RequestState.ERROR,"商品不存在!",null);
            }else{
                comment.setGoods(goods);
            }
        }
        //设置父评论
        if(StringUtils.isNotEmpty(oldCommentId)){
            long oldId = 0l;
            try{
                oldId = Long.valueOf(oldCommentId);

            }catch (Exception e){
                return new ReturnMessages(RequestState.ERROR,"父评论参数有误！",null);
            }
            GoodsComment oldComment = commentService.findById(oldId);
            if(oldComment != null){
                comment.setOldComment(oldComment);
            }

        }
        //设置评论内容
        if(StringUtils.isNotEmpty(content)){
            comment.setContent(new StringBuffer(content));
        }
        //设置创建时间
        comment.setCreateTime(System.currentTimeMillis());
        String userName = SecurityUtils.getUsername(request);
        Member member = memberService.findMemberByUsername(userName);
        comment.setMember(member);
        //设置评论状态（后台暂无审核功能，保存时直接通过审核）
        comment.setState(StateConstant.COMMENT_STATE_CHECK_ON.toString());

        GoodsComment commentRes = commentService.save(comment);
        if(commentRes != null){
            return new ReturnMessages(RequestState.SUCCESS,"保存成功！",commentRes);
        }else{
            return new ReturnMessages(RequestState.ERROR,"保存失败！",null);
        }

    }

    /**
     * 编辑评论
     * @param commentId 评论id
     * @param content   评论内容[可空]
     * @param state     评论审核状态[可空]["COMMENT_STATE_CHECK_ON","COMMENT_STATE_CHECK_OFF"]
     * @return
     */
    @RequestMapping("/update")
    public ReturnMessages updateComment(@RequestParam(name = "commentId",required = true)String commentId,
                                      @RequestParam(name = "content",required = false)String content,
                                      @RequestParam(name = "state",required = false)String state
    ){

        GoodsComment comment = new GoodsComment();
        if(StringUtils.isNotEmpty(commentId)){
            GoodsComment commentSearch = commentService.findById(Long.valueOf(commentId));
            if(commentSearch == null){
                return new ReturnMessages(RequestState.ERROR,"评论不存在！",null);
            }else{
                BeanUtils.copyProperties(commentSearch,comment);
            }
        }else{
            return new ReturnMessages(RequestState.ERROR,"评论参数有误！",null);
        }
        //设置评论内容
        if(StringUtils.isNotEmpty(content)){
            comment.setContent(new StringBuffer(content));
        }
        //设置评论状态
        if(StringUtils.isNotEmpty(state)){  //审核场景用
            if(StringUtils.isNotEmpty(state)){
                if(state.equals(StateConstant.COMMENT_STATE_CHECK_ON.toString()) || state.equals(StateConstant.COMMENT_STATE_CHECK_OFF.toString())){

                    comment.setState(state);
                }else{
                    return new ReturnMessages(RequestState.ERROR,"状态参数有误!",null);
                }
            }
        }else{//评论更新后，改变审核状态，因添加评论自动为审核通过，更新也设置为自动审核通过
            comment.setState(StateConstant.COMMENT_STATE_CHECK_ON.toString());
        }

        GoodsComment commentRes = commentService.save(comment);
        if(commentRes != null){
            return new ReturnMessages(RequestState.SUCCESS,"更新成功！",commentRes);
        }else{
            return new ReturnMessages(RequestState.ERROR,"更新失败！",null);
        }

    }

    /**
     * 删除评论
     * @param commentId 评论id
     * @return
     */
    @RequestMapping("/delete")
    public ReturnMessages deleteComment(@RequestParam(name = "commentId",required = true)String commentId
    ){

        GoodsComment comment = null;
        if(StringUtils.isNotEmpty(commentId)){
            try{
                comment = commentService.findById(Long.valueOf(commentId));
                if(comment == null){
                    return new ReturnMessages(RequestState.ERROR,"评论不存在！",null);
                }else{
                    List<GoodsComment> childComment = comment.getChildComment();
                    if(childComment != null && childComment.size() > 0){
                        return new ReturnMessages(RequestState.ERROR,"有子评论，请删除子节点后再删除！",null);
                    }
                }
            }catch (Exception e){
                return new ReturnMessages(RequestState.ERROR,"评论参数有误！",null);
            }

        }

        if(commentService.delete(Long.valueOf(commentId))){
            return new ReturnMessages(RequestState.SUCCESS,"删除成功！",null);
        }else{
            return new ReturnMessages(RequestState.ERROR,"删除失败！",null);
        }

    }



    /**
     * 获取商品的评论
     * @param goodsId   商品id
     * @param state     评论状态[可空]["COMMENT_STATE_ON_CHECKING","COMMENT_STATE_CHECK_ON","COMMENT_STATE_CHECK_OFF"][默认审核通过的评论]
     * @return
     */
    @RequestMapping("/getGoodsComment")
    public ReturnMessages findGoodsComment(@RequestParam(name = "goodsId",required = true)String goodsId,
                                           @RequestParam(name = "state",required = false)String state
    ){

        Goods goods = null;
        if(StringUtils.isNotEmpty(goodsId)){
            goods = goodsService.findByGoodsId(goodsId);
            if(goods == null){
                return new ReturnMessages(RequestState.ERROR,"商品不存在!",null);
            }
        }

        if(StringUtils.isNotEmpty(state)){
            if(state.equals(StateConstant.COMMENT_STATE_ON_CHECKING.toString()) || state.equals(StateConstant.COMMENT_STATE_CHECK_ON.toString()) || state.equals(StateConstant.COMMENT_STATE_CHECK_OFF.toString())){

            }else{
                if(goods == null){
                    return new ReturnMessages(RequestState.ERROR,"状态参数有误!",null);
                }
            }
        }else{
            state = StateConstant.COMMENT_STATE_CHECK_ON.toString();//默认查询审核通过的评论
        }
        List<GoodsComment> commentList = commentService.findGoodsComment(goods,state);
        if(commentList != null && commentList.size() > 0){
            return new ReturnMessages(RequestState.SUCCESS,"查询成功！",commentList);

        }else{
            return new ReturnMessages(RequestState.SUCCESS,"暂无数据！",null);
        }
    }

    /**
     * 分页查询评论
     * @param commentId     评论id[可空]
     * @param goodsId       商品id[可空]
     * @param oldCommentId  父级评论id[可空]
     * @param memberId      评论的用户id[可空]
     * @param state         评论审核状态[可空]["COMMENT_STATE_ON_CHECKING","COMMENT_STATE_CHECK_ON","COMMENT_STATE_CHECK_OFF"]
     * @param startTime     查询起始时间[可空][搜索评论创建时间所在区间]
     * @param endTime       查询结束时间[可空][搜索评论创建时间所在区间]
     * @param pageNum       请求的页码[可空][默认初始页，值为0]
     * @param pageSize      分页大小[可空][默认为5]
     * @param pageSort      分页排序[可空][默认按"createTime"排序]
     * @param sortDirection 分页排序方向[可空]["desc","asc"][默认按"desc"倒序排序]
     * @return
     */
    @RequestMapping("/findByCondition")
    public ReturnMessages findByCondition(@RequestParam(name = "commentId",required = false)String commentId,
                                          @RequestParam(name = "goodsId",required = false)String goodsId,
                                          @RequestParam(name = "oldCommentId",required = false)String oldCommentId,
                                          @RequestParam(name = "memberId",required = false)String memberId,
                                          @RequestParam(name = "state",required = false)String state,
                                          @RequestParam(name = "startTime",required = false)String startTime,
                                          @RequestParam(name = "endTime",required = false)String endTime,
                                          @RequestParam(name = "pageNum",required = false)String pageNum,
                                          @RequestParam(name = "pageSize",required = false)String pageSize,
                                          @RequestParam(name = "pageSort",required = false)String pageSort,
                                          @RequestParam(name = "sortDirection",required = false)String sortDirection
    ){
        Map<String,Object> mapParam = new HashMap<String,Object>();
        GoodsComment comment = new GoodsComment();

        //设置评论id
        if(StringUtils.isNotEmpty(commentId)){
            GoodsComment commentSearch = commentService.findById(Long.valueOf(commentId));
            if(comment == null){
                return new ReturnMessages(RequestState.ERROR,"评论不存在,参数有误！",null);
            }else{
                comment.setCommentId(Long.valueOf(commentId));
            }
        }
        //设置商品
        if(StringUtils.isNotEmpty(goodsId)){
            Goods goods = goodsService.findByGoodsId(goodsId);
            if(goods == null){
                return new ReturnMessages(RequestState.ERROR,"商品不存在,参数有误！",null);
            }else{
                comment.setGoods(goods);
            }
        }

        //设置父评论
        List<GoodsComment> commentList = new ArrayList<GoodsComment>();
        if(StringUtils.isNotEmpty(oldCommentId)){
            GoodsComment oldComment = commentService.findById(Long.valueOf(oldCommentId));
            if(oldComment == null ){
                return new ReturnMessages(RequestState.ERROR,"父评论不存在,参数有误！",null);
            }else{
                //获取评论及所有子评论
                commentList = commentService.findByOldComment(oldComment);

            }

        }
        mapParam.put("commentList",commentList);

        //设置发布用户
        if(StringUtils.isNotEmpty(memberId)){
            Member member = memberService.findMemberByMemberId(memberId);
            if(member == null){
                return new ReturnMessages(RequestState.ERROR,"用户不存在,参数有误！",null);
            }else{
                comment.setMember(member);
            }
        }
        //设置评论状态
        if(StringUtils.isNotEmpty(state)){
            if(state.equals(StateConstant.COMMENT_STATE_ON_CHECKING.toString()) || state.equals(StateConstant.COMMENT_STATE_CHECK_ON.toString()) || state.equals(StateConstant.COMMENT_STATE_CHECK_OFF.toString())){
                comment.setState(state);
            }else{
                return new ReturnMessages(RequestState.ERROR,"状态参数有误！",null);
            }
        }

        //设置评论
        mapParam.put("comment",comment);

        //设置创建时间条件查询
        long sTime = 0l;
        if(StringUtils.isNotEmpty(startTime)){
            try{
                sTime = Long.valueOf(startTime);
            }catch (Exception e){
                return new ReturnMessages(RequestState.ERROR,"起始时间参数格式有误!",null);
            }
        }
        mapParam.put("startTime",sTime);
        long eTime = 0l;
        if(StringUtils.isNotEmpty(endTime)){
            try{
                eTime = Long.valueOf(endTime);
            }catch (Exception e){
                return new ReturnMessages(RequestState.ERROR,"结束时间参数格式有误!",null);
            }
        }
        mapParam.put("endTime",eTime);

        //设置查询分页
        UtilPage page = new UtilPage(0,5,"createTime", Sort.Direction.DESC);
        if(StringUtils.isNotEmpty(pageNum)){
            try{
                page.setPageNum(Integer.valueOf(pageNum));
            }catch (Exception e){
                return new ReturnMessages(RequestState.ERROR,"分页页数格式有误!",null);
            }
        }

        if(StringUtils.isNotEmpty(pageSize)){
            try{
                page.setPageSize(Integer.valueOf(pageSize));
            }catch (Exception e){
                return new ReturnMessages(RequestState.ERROR,"分页大小格式有误!",null);
            }
        }

        if(StringUtils.isNotEmpty(pageSort)){
            page.setPageSort(pageSort);
        }

        if(StringUtils.isNotEmpty(sortDirection)){
            if(sortDirection.equals("asc")){
                page.setDirection(Sort.Direction.ASC);
            }else if(sortDirection.equals("desc")){
                page.setDirection(Sort.Direction.DESC);
            }else{
                return new ReturnMessages(RequestState.ERROR,"分页排序参数有误!",null);
            }

        }

        mapParam.put("page",page);

        Page<GoodsComment> commentPage = commentService.findCommentByCondition(mapParam);
        if(commentPage != null && commentPage.getContent() != null && commentPage.getContent().size() > 0){

            return new ReturnMessages(RequestState.SUCCESS,"查询成功！",commentPage);
        }else{

            return new ReturnMessages(RequestState.SUCCESS,"暂无数据！",commentPage);
        }
    }
}
