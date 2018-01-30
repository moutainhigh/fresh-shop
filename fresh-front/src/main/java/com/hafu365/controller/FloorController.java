package com.hafu365.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hafu365.fresh.core.entity.common.Image;
import com.hafu365.fresh.core.entity.common.ReturnMessages;
import com.hafu365.fresh.core.entity.constant.RequestState;
import com.hafu365.fresh.core.entity.goods.Goods;
import com.hafu365.fresh.core.entity.home.Floor;
import com.hafu365.fresh.core.entity.home.FloorBody;
import com.hafu365.fresh.core.entity.home.bodyType.GoodsBody;
import com.hafu365.fresh.core.entity.home.bodyType.ImageBody;
import com.hafu365.fresh.core.utils.FloorFactory;
import com.hafu365.fresh.core.utils.StringUtils;
import com.hafu365.fresh.service.home.FloorService;
import com.sun.javafx.collections.ImmutableObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Type;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 楼层控制
 * Created by SunHaiyang on 2017/8/21.
 */
@RestController
public class FloorController {

    @Autowired
    private FloorService floorService;

    /**
     * 添加楼层
     * @param title 标题
     * @param style 演示
     * @param type 类别
     * @param body 内容
     * @param titleImage 标题图片 [可空]
     * @param advImage 广告图片 [可空]
     * @param sort 排序 [可空:默认255]
     * @param onlyShow 一直显示 [可空:默认false]
     * @param startTime 开始展示时间 [可空]
     * @param endTime 结束展示时间 [可空]
     * @return 访问反馈
     */
    @PostMapping(value = "/floor/save")
    public ReturnMessages saveFloor(
            @RequestParam(name = "title") String title,
            @RequestParam(name = "style") String style,
            @RequestParam(name = "type") String type,
            @RequestParam(name = "body") String body,
            @RequestParam(name = "titleImage" ,required = false)String titleImage,
            @RequestParam(name = "advImage",required = false)String advImage,
            @RequestParam(name = "sort",required = false,defaultValue = "255")int sort,
            @RequestParam(name = "onlyShow",required = false,defaultValue = "false")boolean onlyShow,
            @RequestParam(name = "startTime",required = false,defaultValue = "-1")long startTime,
            @RequestParam(name = "endTime",required = false,defaultValue = "-1")long endTime

    ) {
        Floor floor = new Floor(title,style,type);
        Gson gson = new Gson();
        Type advImageType = new TypeToken<ArrayList<Image>>(){}.getType();
        List<FloorBody> floorBody = FloorFactory.getFloorBody(body,type);
        floor.setBody(floorBody);
        if (StringUtils.isNotEmpty(advImage)){
            List<Image> advImages = gson.fromJson(advImage,advImageType);
            floor.setAdvImage(advImages);
        }
        if (StringUtils.isNotEmpty(titleImage)){
            Image titleImg = gson.fromJson(titleImage, Image.class);
            floor.setTitleImage(titleImg);
        }
        floor.setOnlyShow(onlyShow);
        if(sort != 255){
            floor.setSort(sort);
        }
        if(startTime > -1 && endTime > -1){
            floor.setStartTime(startTime);
            floor.setEndTime(endTime);
        }
        floor = floorService.saveFloor(floor);
        if(floor != null){
            return new ReturnMessages(RequestState.SUCCESS,"楼层添加成功。",floor);
        }
        return new ReturnMessages(RequestState.ERROR, "楼层添加失败。",null);
    }

    /**
     * 通过ID查询楼层
     * @param id 楼层ID
     * @return 消息反馈
     */
    @PostMapping(value = "/floor/find")
    public ReturnMessages findById(
            @RequestParam(name = "id",required = false,defaultValue = "-1")long id,
            @RequestParam(name = "page",required = false,defaultValue = "0")int page,
            @RequestParam(name = "pageSize",required = false,defaultValue = "20")int pageSize
    ){
        if(id != -1){
            Floor floor = floorService.findFloorByFloorId(id);
            if(floor != null){
                return new ReturnMessages(RequestState.SUCCESS,"楼层查询成功。",floor);
            }
            return new ReturnMessages(RequestState.ERROR, "楼层查询失败。",null);
        }else{
            Sort sort = new Sort(Sort.Direction.ASC,"sort");
            Pageable pageable = new PageRequest(page,pageSize,sort);
            Page<Floor>floors = floorService.findFloor(pageable);
            if(floors.getSize()>0){
                return new ReturnMessages(RequestState.SUCCESS,"楼层查询成功。",floors);
            }else {
                return new ReturnMessages(RequestState.ERROR, "楼层查询失败。",null);
            }
        }
    }

    /**
     * 查询所有展示的楼层
     * @return 消息反馈
     */
    @PostMapping(value = "/floor/findAll")
    public ReturnMessages findByShow(){
        List<Floor> floors = floorService.findAllByOrderBySort();
        List<Floor> floorList = new ArrayList<Floor>();
        for (Floor floor : floors){
            if (isShow(floor)){
                floorList.add(floor);
            }
        }
        if(floorList != null){
            return new ReturnMessages(RequestState.SUCCESS,"楼层查询成功。",floorList);
        }
        return new ReturnMessages(RequestState.ERROR, "楼层查询失败。",null);
    }

    /**
     * 编辑楼层
     * @param id 商品iD
     * @param title 标题
     * @param style 演示
     * @param type 类别
     * @param body 内容
     * @param titleImage 标题图片 [可空]
     * @param advImage 广告图片 [可空]
     * @param sort 排序 [可空:默认255]
     * @param onlyShow 一直显示 [可空:默认false]
     * @param startTime 开始展示时间 [可空]
     * @param endTime 结束展示时间 [可空]
     * @return 访问反馈
     */
    @PostMapping(value = "/floor/update")
    public ReturnMessages updateFloor(
            @RequestParam(name = "id") long id,
            @RequestParam(name = "title",required = false) String title,
            @RequestParam(name = "style",required = false) String style,
            @RequestParam(name = "type",required = false) String type,
            @RequestParam(name = "body",required = false) String body,
            @RequestParam(name = "titleImage" ,required = false)String titleImage,
            @RequestParam(name = "advImage",required = false)String advImage,
            @RequestParam(name = "sort",required = false,defaultValue = "255")int sort,
            @RequestParam(name = "onlyShow",required = false,defaultValue = "false")boolean onlyShow,
            @RequestParam(name = "startTime",required = false,defaultValue = "-1")long startTime,
            @RequestParam(name = "endTime",required = false,defaultValue = "-1")long endTime
    ){
        Floor floor = floorService.findFloorByFloorId(id);
        Gson gson = new Gson();
        Type imagesType = new TypeToken<ArrayList<Image>>(){}.getType();
        if(StringUtils.isNotEmpty(title)){
            floor.setTitle(title);
        }
        if(StringUtils.isNotEmpty(style)){
            floor.setStyle(style);
        }
        if(StringUtils.isNotEmpty(type)){
            floor.setType(type);
        }
        if(StringUtils.isNotEmpty(body)){
            floor.setBody(null);
            floor.setBodyStr(null);
            floor.setBody(FloorFactory.getFloorBody(body,floor.getType()));
        }
        if(StringUtils.isNotEmpty(titleImage)){
            floor.setTitleImage(gson.fromJson(titleImage,Image.class));
        }
        if(StringUtils.isNotEmpty(advImage)){
            List<Image> images = gson.fromJson(advImage,imagesType);
            floor.setAdvImage(images);
        }
        if(sort != 255){
            floor.setSort(sort);
        }
        if(onlyShow){
            floor.setOnlyShow(onlyShow);
        }else{
            if(startTime > -1 && endTime > -1){
                floor.setOnlyShow(false);
                floor.setStartTime(startTime);
                floor.setEndTime(endTime);
            }
        }
        Floor newFloor = floorService.updateFloor(floor);
        if(newFloor != null){
            return new ReturnMessages(RequestState.SUCCESS,"楼层编辑成功。",newFloor);
        }else {
            return new ReturnMessages(RequestState.SUCCESS,"楼层编辑失败。",null);
        }

    }

    /**
     * 删除楼层
     * @param id 楼层id
     * @return 消息反馈
     */
    @PostMapping(value = "/floor/delete")
    public ReturnMessages deleteFloor(
            @RequestParam(name = "id")long id
    ){
        if (floorService.deleteFloorById(id)){
            return new ReturnMessages(RequestState.SUCCESS,"楼层删除成功。",null);
        } else {
            return new ReturnMessages(RequestState.SUCCESS,"楼层删除成功。",null);
        }
    }

    /**
     * 查询楼层是否显示
     * @param floor
     * @return
     */
    private boolean isShow(Floor floor){
        long time = new Date().getTime();
        long startTime = floor.getStartTime();
        long endTime = floor.getEndTime();
        if(floor.isOnlyShow()){
            return true;
        }
        if(time > startTime && time < endTime){
            return true;
        }
        return false;
    }

}
