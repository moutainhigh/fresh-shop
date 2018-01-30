package com.hafu365.fresh.goods.task;

import com.hafu365.fresh.core.entity.constant.StateConstant;
import com.hafu365.fresh.core.entity.constant.VoucherConstant;
import com.hafu365.fresh.core.entity.goods.Goods;
import com.hafu365.fresh.core.entity.goods.ModifyPriceGoods;
import com.hafu365.fresh.core.entity.voucher.Voucher;
import com.hafu365.fresh.core.entity.voucher.VoucherLog;
import com.hafu365.fresh.service.goods.GoodsService;
import com.hafu365.fresh.service.goods.ModifyPriceGoodsService;
import com.hafu365.fresh.service.voucher.VoucherLogService;
import com.hafu365.fresh.service.voucher.VoucherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品价格修改次日自动生效定时任务
 * Created by HuangWeizhen on 2017/8/30.
 */
@Configuration
@Component
@Slf4j
@EnableScheduling
@Transactional
public class ScheduledTasks {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private ModifyPriceGoodsService priceGoodsService;

    public void priceGoodsTasks(){
        log.info("****进入价格商品定时任务");
        List<ModifyPriceGoods> priceGoodsList = priceGoodsService.findAll();
        if(priceGoodsList != null && priceGoodsList.size() > 0){
            for(ModifyPriceGoods priceGoods : priceGoodsList){
                if(priceGoods != null){
                    Goods goods = priceGoods.getGoods();
                    Goods goodsSearch = goodsService.findByGoodsId(goods.getGoodsId());
                    if(goodsSearch != null && goods.getPrice() != null){
                        goodsSearch.setPrice(goods.getPrice()); //设置价格
                        goodsService.updateGoods(goodsSearch);//更新
                        priceGoods.setState(StateConstant.MODIFY_GOODS_STATE_CHECK_ON.toString());
                        priceGoodsService.save(priceGoods);
                        priceGoodsService.deleteById(priceGoods.getModifyPriceId());
                    }


                }
            }
        }

    }
}
