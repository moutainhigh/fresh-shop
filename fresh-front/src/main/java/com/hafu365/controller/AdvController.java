package com.hafu365.controller;

import com.google.gson.Gson;
import com.hafu365.fresh.core.entity.common.Image;
import com.hafu365.fresh.core.entity.common.ReturnMessages;
import com.hafu365.fresh.core.entity.constant.RequestState;
import com.hafu365.fresh.core.entity.home.Adv;
import com.hafu365.fresh.core.utils.StringUtils;
import com.hafu365.fresh.service.home.AdvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

/**
 * 广告控制层
 * Created by SunHaiyang on 2017/8/18.
 */
@RestController
public class AdvController {

    @Autowired
    AdvService advService;

    /**
     * 新建广告
     * @param title 标题
     * @param image 图片
     * @param startTime 展示开始时间
     * @param endTime 展示结束时间
     * @param onlyShow 一直显示
     * @param sort 排序
     * @return 操作反馈
     */
    @PostMapping(value = "/adv/save")
    public ReturnMessages saveAdv(
            @RequestParam(name = "title", required = true) String title,
            @RequestParam(name = "image", required = true) String image,
            @RequestParam(name = "startTime", required = false, defaultValue = "-1") long startTime,
            @RequestParam(name = "endTime", required = false, defaultValue = "-1") long endTime,
            @RequestParam(name = "onlyShow", required = false, defaultValue = "false") boolean onlyShow,
            @RequestParam(name = "sort", required = false, defaultValue = "255") int sort
    ) {
        Gson gson = new Gson();
         Image img = gson.fromJson(image, Image.class);
        Adv adv = new Adv(title, img, sort);
        if (onlyShow) {
            adv.setOnlyShow(onlyShow);
        } else {
            if (startTime != -1 && endTime != -1){
                adv.setStartTime(startTime);
                adv.setEndTime(endTime);
            }else{
                return new ReturnMessages(RequestState.ERROR,"参数错误。",null);
            }
        }
        adv = advService.saveAdv(adv);
        return new ReturnMessages(RequestState.SUCCESS,"新建广告成功。",adv);
    }

    /**
     * 查询所有显示广告
     * @return 消息反馈
     */
    @PostMapping(value = "/adv/findAll")
    public ReturnMessages findAllByShow(){
        List<Adv> advs = advService.findAllByOrderBySort();
        List<Adv> advList = new ArrayList<Adv>();
        for (Adv adv : advs){
            if (isShow(adv)){
                advList.add(adv);
            }
        }
        if(advList.size() > 0){
            return new ReturnMessages(RequestState.SUCCESS,"查询成功。",advList);
        }else{
            return new ReturnMessages(RequestState.ERROR,"查询失败。",null);
        }
    }

    /**
     * 查询广告
     * @param page 页码
     * @param pageSize 页面大小
     * @return 操作反馈
     */
    @PostMapping(value = "/adv/find")
    public ReturnMessages findAdv(
            @RequestParam(name = "page",required = false,defaultValue = "0")int page,
            @RequestParam(name = "pageSize",required = false,defaultValue = "20")int pageSize
    ){
        Pageable pageable = new PageRequest(page,pageSize);
        Page<Adv> advs = advService.findAdv(pageable);
        if(advs.getSize() > 0){
            return new ReturnMessages(RequestState.SUCCESS,"查询成功。",advs);
        }else{
            return new ReturnMessages(RequestState.ERROR,"查询失败",null);
        }

    }

    /**
     * 查询一个广告
     * @param id
     * @return
     */
    @PostMapping("/adv/findOne")
    public ReturnMessages findAdv(
            @RequestParam(name = "id")long id
    ){
        Adv adv = advService.findAdvByAdvId(id);
        if(adv != null){
            return new ReturnMessages(RequestState.SUCCESS,"查询成功。",adv);
        }else{
            return new ReturnMessages(RequestState.ERROR,"查询失败。",null);
        }
    }


    /**
     * 删除广告
     * @param id 广告ID
     * @return 操作反馈
     */
    @PostMapping(value = "/adv/delete")
    public ReturnMessages deleteAdv(
            @RequestParam(name = "id")long id
    ){
        if(advService.deleteAdv(id)){
            return new ReturnMessages(RequestState.SUCCESS,"删除成功。",null);
        }else{
            return new ReturnMessages(RequestState.SUCCESS,"删除失败。",null);
        }
    }

    /**
     * 编辑广告
     * @param id 编号
     * @param title 标题
     * @param image 图片json
     * @param onlyShow  一直显示
     * @param startTime 开始显示时间
     * @param endTime 结束显示时间
     * @param sort 排序
     * @return 操作反馈
     */
    @PostMapping(value = "/adv/update")
    public ReturnMessages updateAdv(
            @RequestParam(name = "id")long id,
            @RequestParam(name = "title" ,required = false)String title,
            @RequestParam(name = "image",required = false)String image,
            @RequestParam(name = "onlyShow",required = false)boolean onlyShow,
            @RequestParam(name = "startTime",required = false,defaultValue = "-1")long startTime,
            @RequestParam(name = "endTime",required = false,defaultValue = "-1")long endTime,
            @RequestParam(name = "sort",required = false,defaultValue = "-1")int sort
    ){
        Adv adv = advService.findAdvByAdvId(id);
        if(StringUtils.isNotEmpty(title)){
            adv.setAdvTitle(title);
        }
        if(StringUtils.isNotEmpty(image)){
            Gson gson = new Gson();
            Image img = gson.fromJson(image,Image.class);
            adv.setAdvImage(img);
            adv.setImageStr(null);
        }
        if(onlyShow){
            adv.setOnlyShow(onlyShow);
        }else{
            if(startTime != -1 && endTime != -1){
                adv.setOnlyShow(false);
                adv.setStartTime(startTime);
                adv.setEndTime(endTime);
            }
        }
        if(sort != -1){
            adv.setSort(sort);
        }
        try {
            adv = advService.updateAdv(adv);
            return new ReturnMessages(RequestState.SUCCESS,"编辑成功。",adv);
        }catch (Exception e){
            return new ReturnMessages(RequestState.ERROR,"编辑失败。",adv);
        }
    }

    /**
     * 查询是否显示
     * @param adv
     * @return
     */
    private boolean isShow(Adv adv){
        long time = new Date().getTime();
        long startTime = adv.getStartTime();
        long endTime = adv.getEndTime();
        if(adv.isOnlyShow()){
            return true;
        }
        if(time > startTime && time < endTime){
            return true;
        }
        return false;
    }
}
