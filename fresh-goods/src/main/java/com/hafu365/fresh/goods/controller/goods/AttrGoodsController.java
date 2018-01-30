package com.hafu365.fresh.goods.controller.goods;

import com.hafu365.fresh.core.entity.common.ReturnMessages;
import com.hafu365.fresh.core.entity.common.UtilPage;
import com.hafu365.fresh.core.entity.constant.RequestState;
import com.hafu365.fresh.core.entity.constant.StateConstant;
import com.hafu365.fresh.core.entity.goods.ModifyAttrGoods;
import com.hafu365.fresh.core.utils.StringUtils;
import com.hafu365.fresh.service.goods.ModifyAttrGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 修改属性商品
 * Created by HuangWeizhen on 2017/8/15.
 */
@RestController
@RequestMapping("/attrGoods")
public class AttrGoodsController {

    @Autowired
    private ModifyAttrGoodsService attrGoodsService;

    /**
     * 分页查询属性商品
     * @param state         修改属性商品审核状态[可空]["MODIFY_GOODS_STATE_ON_CHECKING","MODIFY_GOODS_STATE_CHECK_ON","MODIFY_GOODS_STATE_CHECK_OFF"]
     * @param pageNum       请求的页码[可空][默认初始页，值为0]
     * @param pageSize      分页大小[可空][默认为5]
     * @param pageSort      分页排序[可空][默认按"createTime"排序]
     * @param sortDirection 分页排序方向[可空]["desc","asc"][默认按"desc"倒序排序]
     * @return
     */
    @RequestMapping("/findAttrGoods")
    public ReturnMessages findPriceGoods(@RequestParam(name = "state",required = false)String state,
                                         @RequestParam(name = "pageNum",required = false)String pageNum,
                                         @RequestParam(name = "pageSize",required = false)String pageSize,
                                         @RequestParam(name = "pageSort",required = false)String pageSort,
                                         @RequestParam(name = "sortDirection",required = false)String sortDirection){
        ReturnMessages rm = new ReturnMessages();

        ModifyAttrGoods attrGoods = new ModifyAttrGoods();
        //设置审核状态
        if(StringUtils.isNotEmpty(state)){
            if(state.equals(StateConstant.MODIFY_GOODS_STATE_ON_CHECKING.toString()) || state.equals(StateConstant.MODIFY_GOODS_STATE_CHECK_ON.toString()) || state.equals(StateConstant.MODIFY_GOODS_STATE_CHECK_OFF.toString())){
                attrGoods.setState(state);
            }else{
                return new ReturnMessages(RequestState.ERROR,"状态参数有误!",null);
            }

        }else{
            attrGoods.setState(StateConstant.MODIFY_GOODS_STATE_ON_CHECKING.toString());
        }

        Map<String,Object> mapParam = new HashMap<String,Object>();

        //设置价格商品
        mapParam.put("attrGoods",attrGoods);
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
        Page<ModifyAttrGoods> attrGoodsPage = null;
        try{
            attrGoodsPage = attrGoodsService.findAttrGoods(mapParam);
            if(attrGoodsPage != null && attrGoodsPage.getContent() != null && attrGoodsPage.getContent().size() > 0){

                rm = new ReturnMessages(RequestState.SUCCESS,"查询成功!",attrGoodsPage);
            }else{
                rm = new ReturnMessages(RequestState.SUCCESS,"暂无数据!",null);
            }

        }catch (Exception e){
            rm = new ReturnMessages(RequestState.ERROR,"暂无数据!",null);
        }

        return rm;
    }
}
