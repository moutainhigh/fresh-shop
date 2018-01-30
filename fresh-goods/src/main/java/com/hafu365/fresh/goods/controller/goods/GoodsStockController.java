package com.hafu365.fresh.goods.controller.goods;

import com.hafu365.fresh.core.entity.common.ReturnMessages;
import com.hafu365.fresh.core.entity.common.UtilPage;
import com.hafu365.fresh.core.entity.constant.RequestState;
import com.hafu365.fresh.core.entity.goods.GoodsStock;
import com.hafu365.fresh.core.utils.StringUtils;
import com.hafu365.fresh.service.goods.GoodsService;
import com.hafu365.fresh.service.goods.GoodsStockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 商品库存
 * Created by HuangWeizhen on 2017/8/28.
 */
@Slf4j
@RestController
@RequestMapping("/goodsStock")
public class GoodsStockController {

    @Autowired
    private GoodsStockService stockService;

    @Autowired
    private GoodsService goodsService;

    /**
     * 修改商品库存
     * @param stockId   库存id
     * @param stockNum  商品库存数量[可空]
     * @param sku       商品库存单元[可空]
     * @return
     */
    @RequestMapping("/update")
    public ReturnMessages updateGoodsStock(@RequestParam(name = "stockId",required = true)String stockId,
                                           @RequestParam(name = "stockNum",required = false)String stockNum,
                                           @RequestParam(name = "sku",required = false)String sku
                                           ){
        GoodsStock goodsStock = new GoodsStock();

        if(StringUtils.isNotEmpty(stockId)){
            GoodsStock stockSearch = stockService.findByStockId(stockId);
            if(stockSearch != null){
                BeanUtils.copyProperties(stockSearch, goodsStock);

            }else{
                return new ReturnMessages(RequestState.ERROR,"库存不存在！",null);
            }

        }else{
            return new ReturnMessages(RequestState.ERROR,"库存参数有误！",null);
        }

        //设置库存数量
        if(StringUtils.isNotEmpty(stockNum)){
            long goodsStockNum = 0l;
            try{
                goodsStockNum = Long.valueOf(stockNum);
                if(goodsStockNum < 0){
                    return new ReturnMessages(RequestState.ERROR,"库存不能为负数！",null);
                }else{
                    goodsStock.setStockNum(goodsStockNum);
                }

            }catch (Exception e){
                return new ReturnMessages(RequestState.ERROR,"商品库存数量格式有误！",null);
            }

        }
        //设置库存单位
        if(StringUtils.isNotEmpty(sku)){
            goodsStock.setSku(sku);
        }

        GoodsStock goodsStockRes = stockService.saveOrUpdate(goodsStock);
        if(goodsStockRes != null){
            return new ReturnMessages(RequestState.SUCCESS,"更新成功！",goodsStockRes);
        }else{
            return new ReturnMessages(RequestState.ERROR,"更新失败！",null);
        }

    }

    /**
     * 根据id查询库存
     * @param stockId   库存id
     * @return
     */
    @RequestMapping("/findById")
    public ReturnMessages findById(@RequestParam(name = "stockId",required = true)String stockId
    ){
        if(StringUtils.isNotEmpty(stockId)){
            GoodsStock stockSearch = stockService.findByStockId(stockId);
            if(stockSearch != null){
                return new ReturnMessages(RequestState.SUCCESS,"查询成功！",stockSearch);

            }else{
                return new ReturnMessages(RequestState.ERROR,"库存不存在！",null);
            }

        }else{
            return new ReturnMessages(RequestState.ERROR,"库存参数有误！",null);
        }

    }

    /**
     * 根据条件分页查询商品库存
     * @param stockId       库存id[可空]
     * @param stockNum      库存数量[可空]
     * @param numCondition  库存数量条件[可空]["gt","gtAndEq","eq","lt","ltAndEq",其他任意值][默认值"doNothing"，不能单独使用，需与库存数量同时使用]
     * @param sku           库存单元[可空]
     * @param pageNum     请求的页码[可空][默认初始页，值为0]
     * @param pageSize      分页大小[可空][默认为5]
     * @param pageSort      分页排序[可空][默认按"stockNum"排序]
     * @param sortDirection 分页排序方向[可空]["desc","asc"][默认按"asc"倒序排序]
     * @return
     */
    @RequestMapping("/findByCondition")
    public ReturnMessages findByCondition(@RequestParam(name = "stockId",required = false)String stockId,
                                          @RequestParam(name = "stockNum",required = false)String stockNum,
                                          @RequestParam(name = "numCondition",required = false)String numCondition,
                                          @RequestParam(name = "sku",required = false)String sku,
                                          @RequestParam(name = "pageNum",required = false)String pageNum,
                                          @RequestParam(name = "pageSize",required = false)String pageSize,
                                          @RequestParam(name = "pageSort",required = false)String pageSort,
                                          @RequestParam(name = "sortDirection",required = false)String sortDirection
    ){
        ReturnMessages rm = new ReturnMessages();

        Map<String,Object> paramMap = new HashMap<String,Object>();
        GoodsStock stock = new GoodsStock();
        //设置库存id
        if(StringUtils.isNotEmpty(stockId)){
            GoodsStock stockSearch = stockService.findByStockId(stockId);
            if(stockSearch == null){
                return new ReturnMessages(RequestState.ERROR,"库存不存在！",null);
            }else{
                stock.setStockId(stockId);

            }
        }
        //设置库存数量
        if(StringUtils.isNotEmpty(stockNum)){
            try{
                stock.setStockNum(Long.valueOf(stockNum));
            }catch (Exception e){
                return new ReturnMessages(RequestState.ERROR,"库存数量参数格式有误！",null);
            }
        }else{
            stock.setStockNum(-1l);
        }
        paramMap.put("stock",stock);
        //设置库存数据条件
        if(StringUtils.isNotEmpty(numCondition) && (numCondition.equals("gt") || numCondition.equals("gtAndEq") || numCondition.equals("eq") || numCondition.equals("lt") || numCondition.equals("ltAndEq"))){

        }else{
            numCondition = "doNothing";
        }
        paramMap.put("numCondition",numCondition);

        //设置查询分页
        UtilPage page = new UtilPage(0,5,"stockNum", Sort.Direction.ASC);
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

        paramMap.put("page",page);

        Page<GoodsStock> stockPage = stockService.findByCondition(paramMap);
        if(stockPage.getContent() != null && stockPage.getContent().size() > 0){
            rm.setMessages("查询成功!");
        }else{
            rm.setMessages("暂无数据!");
        }
        rm.setContent(stockPage);
        rm.setState(RequestState.SUCCESS);
        return rm;

    }

}
