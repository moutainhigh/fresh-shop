package com.hafu365.fresh.goods.controller.goods;

import com.hafu365.fresh.core.entity.common.ReturnMessages;
import com.hafu365.fresh.core.entity.common.UtilPage;
import com.hafu365.fresh.core.entity.constant.RequestState;
import com.hafu365.fresh.core.entity.constant.StateConstant;
import com.hafu365.fresh.core.entity.goods.ModifyPriceGoods;
import com.hafu365.fresh.core.utils.StringUtils;
import com.hafu365.fresh.service.goods.ModifyPriceGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 修改价格商品
 * Created by HuangWeizhen on 2017/10/11.
 */
@RestController
@RequestMapping("/priceGoods")
public class PriceGoodsController {
    @Autowired
    private ModifyPriceGoodsService priceGoodsService;

    /**
     * 分页查询属性商品
     * @param pageNum       请求的页码[可空][默认初始页，值为0]
     * @param pageSize      分页大小[可空][默认为5]
     * @param pageSort      分页排序[可空][默认按"createTime"排序]
     * @param sortDirection 分页排序方向[可空]["desc","asc"][默认按"desc"倒序排序]
     * @return
     */
    @RequestMapping("/findPriceGoods")
    public ReturnMessages findPriceGoods(
                                         @RequestParam(name = "pageNum",required = false)String pageNum,
                                         @RequestParam(name = "pageSize",required = false)String pageSize,
                                         @RequestParam(name = "pageSort",required = false)String pageSort,
                                         @RequestParam(name = "sortDirection",required = false)String sortDirection){
        ReturnMessages rm = new ReturnMessages();

        Map<String,Object> mapParam = new HashMap<String,Object>();

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
        Page<ModifyPriceGoods> priceGoodsPage = null;
        try{
            priceGoodsPage = priceGoodsService.findPriceGoods(mapParam);
            if(priceGoodsPage != null && priceGoodsPage.getContent() != null && priceGoodsPage.getContent().size() > 0){

                rm = new ReturnMessages(RequestState.SUCCESS,"查询成功!",priceGoodsPage);
            }else{
                rm = new ReturnMessages(RequestState.SUCCESS,"暂无数据!",null);
            }

        }catch (Exception e){
            rm = new ReturnMessages(RequestState.ERROR,"暂无数据!",null);
        }

        return rm;
    }
}
