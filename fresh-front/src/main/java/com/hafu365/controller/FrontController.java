package com.hafu365.controller;

import com.hafu365.fresh.core.entity.common.ReturnMessages;
import com.hafu365.fresh.core.entity.constant.RequestState;
import com.hafu365.fresh.core.entity.home.Adv;
import com.hafu365.fresh.core.entity.home.Floor;
import com.hafu365.fresh.service.home.AdvService;
import com.hafu365.fresh.service.home.FloorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 首页
 * Created by SunHaiyang on 2017/8/22.
 */
@RestController
public class FrontController {

    @Autowired
    FloorService floorService;

    @Autowired
    AdvService advService;

    /**
     * 查询首页信息
     * @return 消息反馈
     */
    @PostMapping(value = "/front")
    public ReturnMessages getHome(){
        Map<String,Object> map = new HashMap<String, Object>();
        List<Adv> advs = advService.findAllByOrderBySort();
        List<Floor> floors = floorService.findAllByOrderBySort();
        if(advs.size() > 0){
            map.put("adv",advs);
        }
        if(floors.size() > 0){
            map.put("floor",floors);
        }
        if (map.size() > 0){
            return new ReturnMessages(RequestState.SUCCESS,"查询成功。",map);
        }else{
            return new ReturnMessages(RequestState.SUCCESS,"查询失败。",null);
        }

    }
}
