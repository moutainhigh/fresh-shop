package com.hafu365.fresh.core.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hafu365.fresh.core.entity.constant.FloorTypeConstant;
import com.hafu365.fresh.core.entity.home.FloorBody;
import com.hafu365.fresh.core.entity.home.bodyType.GoodsBody;
import com.hafu365.fresh.core.entity.home.bodyType.ImageBody;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * floorBody工厂
 * Created by SunHaiyang on 2017/8/21.
 */
public class FloorFactory {

    public static List<FloorBody> getFloorBody (String floorBody, String type){
        Gson gson = new Gson();
        if (type.equalsIgnoreCase(FloorTypeConstant.FLOOR_IMAGES.toString())){
            Type imageBodyType = new TypeToken<ArrayList< ImageBody>>(){}.getType();
            return gson.fromJson(floorBody,imageBodyType);
        }
        if(type.equalsIgnoreCase(FloorTypeConstant.FLOOR_GOODS.toString())){
            Type goodsBodyType = new TypeToken<ArrayList<GoodsBody>>(){}.getType();
            return gson.fromJson(floorBody,goodsBodyType);
        }
        return null;
    }
}
