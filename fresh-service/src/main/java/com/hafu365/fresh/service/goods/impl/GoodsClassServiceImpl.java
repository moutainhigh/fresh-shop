package com.hafu365.fresh.service.goods.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hafu365.fresh.core.entity.common.Image;
import com.hafu365.fresh.core.entity.goods.Brand;
import com.hafu365.fresh.core.entity.goods.GoodsClass;
import com.hafu365.fresh.core.utils.StringUtils;
import com.hafu365.fresh.repository.goods.BrandRepository;
import com.hafu365.fresh.repository.goods.GoodsClassRepository;
import com.hafu365.fresh.service.goods.BrandService;
import com.hafu365.fresh.service.goods.GoodsClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 商品分类业务实现类
 * Created by HuangWeizhen on 2017/7/27.
 */
@Transactional
@Service
public class GoodsClassServiceImpl implements GoodsClassService {

    @Autowired
    private GoodsClassRepository gcRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private BrandService brandService;

    @Override
    @CacheEvict(value = "GoodsClass",beforeInvocation = true,allEntries = true)
    public GoodsClass save(GoodsClass goodsClass) {
        return gcRepository.save(goodsClass);
    }

    @Override
    @CacheEvict(value = "GoodsClass",beforeInvocation = true,allEntries = true)
    public GoodsClass update(GoodsClass goodsClass) {
        GoodsClass gcSearch = gcRepository.findByClassIdAndDelFalse(goodsClass.getClassId());
        gcSearch.setClassPic(goodsClass.getClassPic());
        return gcRepository.save(goodsClass);
    }


    @Override
    @Cacheable(value = "GoodsClass",unless = "#result == null" )
    public List<GoodsClass> findShowGcList() {

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Image>>() {}.getType();
        List<GoodsClass> goodsClasses = gcRepository.findGoodsClassByShow();
        for (int i = 0 ; i < goodsClasses.size();i++){
            GoodsClass goodsClass = goodsClasses.get(i);
            //获取分类的品牌id列表
            List<String> brandIdList = brandService.findBrandIdListByGc(goodsClass.getClassId());
            goodsClass.setBrandIdList(brandIdList);
            goodsClass.setGcShow(true);
            List<GoodsClass> childGoodsClass = gcRepository.findSimpleGoodsClass(goodsClass);
            if(StringUtils.isNotEmpty(goodsClass.getPics())){
                List<Image> pics = gson.fromJson(goodsClass.getPics(),type);
                goodsClass.setClassPic(pics);
                goodsClass.setPics(null);
            }
            if(childGoodsClass != null && childGoodsClass.size() >0){
                //获取子类的品牌id列表
                for(GoodsClass gc : childGoodsClass){
                    if(gc != null){
                        List<String> childBrandIdList = brandService.findBrandIdListByGc(gc.getClassId());
                        gc.setBrandIdList(childBrandIdList);
                    }

                }
                goodsClass.setChildClass(childGoodsClass);
            }
            goodsClasses.set(i,goodsClass);
        }

        return goodsClasses;
    }

    @Override
    public List<GoodsClass> findGcList() {
        List<GoodsClass> parentList = gcRepository.findByOldClassIsNullAndDelFalseOrderByOrderNum();
        if(parentList != null && parentList.size() > 0){
            for(GoodsClass parent : parentList){
                //获取分类的品牌id列表
                List<String> brandIdList = brandService.findBrandIdListByGc(parent.getClassId());
                parent.setBrandIdList(brandIdList);
                List<GoodsClass> children = gcRepository.findByOldClassAndDelFalseOrderByOrderNum(parent);
                if(children != null && children.size() > 0){
                    for(GoodsClass child : children){
                        if(child != null){
                            List<String> childBrandIdList = brandService.findBrandIdListByGc(child.getClassId());
                            child.setBrandIdList(childBrandIdList);
                        }
                    }
                    parent.setChildClass(children);
                }
            }
        }
        return parentList;
    }


    @Override
    public GoodsClass findById(String classId) {

        return gcRepository.findByClassIdAndDelFalse(classId);
    }

    @Override
    public boolean isExist(String classId) {
        if(gcRepository.exists(classId)){
           GoodsClass gc = gcRepository.findByClassIdAndDelFalse(classId);
           if(gc != null){
               return true;

           }

        }
        return true;
    }

    @Override
    @CacheEvict(value = "GoodsClass",beforeInvocation = true,allEntries = true)
    public boolean deleteByClassId(String classId) {
        try{
            GoodsClass gc = gcRepository.findByClassIdAndDelFalse(classId);

            //物理删除分类与品牌的关联
            List<Brand> brandList = gc.getBrandList();
            if(brandList != null && brandList.size() > 0){
                for(Brand brand : brandList){
                    if(brand != null){
                        List<GoodsClass> gcList = brand.getGoodsClassList();
                        if(gcList != null && gcList.contains(gc)){
                            gcList.remove(gc);
                        }
                        brand.setGoodsClassList(gcList);
                        brandRepository.save(brand);
                    }

                }
            }
            //逻辑删除分类
            gcRepository.deleteByClassId(classId);

            return true;
        }catch (Exception e){
            return false;
        }

    }

    @Override
    @CacheEvict(value = "GoodsClass",beforeInvocation = true,allEntries = true)
    public boolean physicalDelete(String classId) {
        try{
            GoodsClass gc = gcRepository.findByClassIdAndDelFalse(classId);

            //物理删除分类与品牌的关联
            List<Brand> brandList = gc.getBrandList();
            if(brandList != null && brandList.size() > 0){
                for(Brand brand : brandList){
                    if(brand != null){
                        List<GoodsClass> gcList = brand.getGoodsClassList();
                        if(gcList != null && gcList.contains(gc)){
                            gcList.remove(gc);
                        }
                        brand.setGoodsClassList(gcList);
                        brandRepository.save(brand);
                    }
                }
            }
            //物理删除分类
            gcRepository.delete(classId);

            return true;
        }catch (Exception e){
            return false;
        }

    }

    @Override
    public List<GoodsClass> findGcChildren(GoodsClass oldClass) {
        return gcRepository.findByOldClassAndDelFalse(oldClass);
    }

}
