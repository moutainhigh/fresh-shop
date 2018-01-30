package com.hafu365.fresh.service.goods;

import com.hafu365.fresh.core.entity.constant.StateConstant;
import com.hafu365.fresh.core.entity.goods.Goods;
import com.hafu365.fresh.core.entity.goods.GoodsVo;
import com.hafu365.fresh.core.entity.store.Store;

import java.util.Date;

/**
 * Created by zhaihuilin on 2017/11/1  9:22.
 */
public class GoodsUtils {

    /**
     *判断商品是否是失效
     * @param goods
     * @return
     */
    public static boolean theFailGoods(Goods goods){
        long time = new Date().getTime();
        if (goods !=null){
            if(time < goods.getSoldInTime() || time > goods.getSoldOutTime() || goods.isDel() ||
                    !goods.getState().equals(StateConstant.GOODS_STATE_CHECK_ON.toString())){
                //商品已经删除或者下架
                return false;
            }
        }else{
            return false;
        }
        //商品还在上架中
        return true;
    }

    /**
     *
     * @param goods
     * @return
     */
    public static GoodsVo toGoodsVo(Goods goods){
        if(goods != null){
            if(theFailGoods(goods)){
                GoodsVo goodsVo = new GoodsVo();
                goodsVo.setGoods(goods);
                goodsVo.setGoodsprice(goods.getPrice());
                goodsVo.setGoodsId(goods.getGoodsId());
                return goodsVo;
            }
        }
        return null;
    }

    /**
     *
     * @param goods
     * @param count
     * @return
     */
    public static GoodsVo toGoodsVo(Goods goods,int count){
        if(goods != null){
            if(theFailGoods(goods)){
                GoodsVo goodsVo = new GoodsVo();
                goodsVo.setCreateTiem(new Date().getTime());
                goodsVo.setNumber(count); //设置 订单商品购买的数量
                goodsVo.setGoods(goods);//设置 订单商品编号
                goodsVo.setGoodsprice(goods.getPrice());//设置 订单商品商品单价
                goodsVo.setGoodsId(goods.getGoodsId());//设置 订单商品编号
                if (goods.getStore() !=null){
                    goodsVo.setStore(goods.getStore()); //设置 订单商品所属店铺
                }
                return goodsVo;
            }
        }
        return null;
    }

    /**
     * 检查是否是自己店铺旗下的商品
      * @param store
     * @param goods
     * @return
     */
    public  static  boolean CheckwhetherMeGoods(Store store, Goods goods){
         if (store !=null){
                String MystoreId=store.getStoreId();//获取当前登录用户名的店铺编号
                Store goodsStore=goods.getStore();//获取商品所属店铺
                if (goodsStore !=null){
                     if (goodsStore.getStoreId().equals(MystoreId)){  //相等 是旗下商品
                         return  false;
                     }
                }
         }
         return  true;
    }
}
