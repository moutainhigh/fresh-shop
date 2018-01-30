package com.hafu365.fresh.service.goods.impl;

import com.hafu365.fresh.core.entity.common.UtilPage;
import com.hafu365.fresh.core.entity.constant.StateConstant;
import com.hafu365.fresh.core.entity.goods.Brand;
import com.hafu365.fresh.core.entity.goods.Goods;
import com.hafu365.fresh.core.entity.store.Store;
import com.hafu365.fresh.core.utils.StringUtils;
import com.hafu365.fresh.repository.goods.BrandRepository;
import com.hafu365.fresh.repository.goods.GoodsRepository;
import com.hafu365.fresh.service.goods.BrandService;
import com.hafu365.fresh.service.goods.GoodsService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 品牌业务实现类
 * Created by HuangWeizhen on 2017/8/14.
 */
@Transactional
@Service
@Log4j
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private GoodsService goodsService;

    @Override
    @CacheEvict(value = "GoodsClass",beforeInvocation = true,allEntries = true)
    public Brand save(Brand brand) {
        brand.setCreateTime(System.currentTimeMillis());
        brand.setState(StateConstant.BRAND_STATE_ON_CHECKING.toString());
        return brandRepository.save(brand);
    }

    @Override
    @CacheEvict(value = "GoodsClass",beforeInvocation = true,allEntries = true)
    public Brand update(Brand brand) {
        Brand brandSearch = brandRepository.findByBrandIdAndDelFalse(brand.getBrandId());
        if(brand.getBrandPic() != null){
            brandSearch.setBrandPic(brand.getBrandPic());
        }
        return brandRepository.save(brand);
    }

    @Override
    @CacheEvict(value = "GoodsClass",beforeInvocation = true,allEntries = true)
    public boolean closeBrand(String brandId) {
        try{
            Brand brand = brandRepository.findByBrandIdAndDelFalse(brandId);
            //逻辑删除品牌关联的商品
            List<Goods> goodsList = goodsRepository.findByBrand(brand);
            if(goodsList != null && goodsList.size() > 0){
                for(Goods goods : goodsList){
                    goodsService.deleteByGoodsId(goods.getGoodsId());//逻辑删除商品
                }
            }
            //不能自动删除与分类的关联，需手动删除关联
            brand.setGoodsClassList(null);
            brandRepository.save(brand);
            //关闭品牌
            brandRepository.closeBrand(StateConstant.BRAND_STATE_ON_CLOSE.toString(),brandId);
            return true;
        }catch (Exception e){
            return false;
        }

    }

    @Override
    @CacheEvict(value = "GoodsClass",beforeInvocation = true,allEntries = true)
    public boolean physicalDelete(String brandId) {
        try {
            Brand brand = brandRepository.findByBrandIdAndDelFalse(brandId);
            //物理删除品牌关联的商品
            List<Goods> goodsList = goodsRepository.findByBrand(brand);
            if(goodsList != null && goodsList.size() > 0){
                for(Goods goods : goodsList){
                    goodsService.deleteGoods(goods.getGoodsId());//物理删除商品
                }
            }
            //物理删除品牌(自动删除与分类的关联)
            brandRepository.delete(brandId);
            return true;
        }catch (Exception e){
            return false;
        }

    }

    @Override
    @CacheEvict(value = "GoodsClass",beforeInvocation = true,allEntries = true)
    public boolean delete(String brandId){
        try{
            Brand brand = brandRepository.findByBrandIdAndDelFalse(brandId);
            //逻辑删除品牌关联的商品
            List<Goods> goodsList = goodsRepository.findByBrand(brand);
            if(goodsList != null && goodsList.size() > 0){
                for(Goods goods : goodsList){
                    goodsService.deleteByGoodsId(goods.getGoodsId());//逻辑删除商品
                }
            }
            //不能自动删除与分类的关联，需手动删除关联
            brand.setGoodsClassList(null);
            brandRepository.save(brand);
            //逻辑删除品牌
            brandRepository.deleteByBrandId(brandId);
            return true;
        }catch (Exception e){
            return false;
        }

    }

    @Override
    public Brand findById(String brandId) {
        return brandRepository.findByBrandIdAndDelFalse(brandId);
    }

    @Override
    public Brand findByBrandTitle(String brandTitle) {
        return brandRepository.findByBrandTitle(brandTitle);
    }

    @Override
    public Page<Brand> findBrand(Map<String, Object> map_param) {
        final Brand brand = (Brand) map_param.get("brand");
        final Store store = (Store)map_param.get("store");
        final long startTime = (Long) map_param.get("startTime");
        final long endTime = (Long) map_param.get("endTime");
        final UtilPage page = (UtilPage)map_param.get("page");
        final Sort sort = new Sort(page.getDirection(),page.getPageSort());
        Pageable pageable = new PageRequest(page.getPageNum(), page.getPageSize(), sort);
        final Brand initBrand = (Brand)map_param.get("initBrand");
        return brandRepository.findAll(new Specification<Brand>() {
            @Override
            public Predicate toPredicate(Root<Brand> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {

                List<Predicate> predicates = new ArrayList<Predicate>();

                //拼接品牌id
                if(StringUtils.isNotEmpty(brand.getBrandId())){
                    predicates.add(cb.equal(root.<String>get("brandId"),brand.getBrandId()));
                }else{
                    if(initBrand != null){
                        predicates.add(cb.notEqual(root.<String>get("brandId"),initBrand.getBrandId()));
                    }
                }
                //拼接品牌标题
                if(StringUtils.isNotEmpty(brand.getBrandTitle())){
                    String brandTitle = brand.getBrandTitle().trim();
                    predicates.add(cb.like(root.<String>get("brandTitle"),"%" + brandTitle + "%"));
                }
                //拼接店铺
                if(brand.getStore() != null){
                    predicates.add(cb.equal(root.<String>get("store"),brand.getStore()));
                }
                //拼接删除状态
                predicates.add(cb.equal(root.<Boolean>get("del"),brand.isDel()));
                //拼接审核状态
                if(StringUtils.isNotEmpty(brand.getState())){
                    predicates.add(cb.equal(root.<String>get("state"),brand.getState()));
                }
                //拼接创建时间区间查询
                Predicate p_createTime = null;
                if(startTime != 0 && endTime != 0 && (startTime < endTime)){
                    p_createTime = cb.between(root.<Long>get("createTime"),startTime,endTime);
                }else if(startTime != 0 && endTime == 0){
                    p_createTime = cb.between(root.<Long>get("createTime"),startTime,System.currentTimeMillis());

                }else if(startTime == 0 && endTime != 0){
                    p_createTime = cb.between(root.<Long>get("createTime"),0l,endTime);
                }else{
                    p_createTime = null;
                }
                if(p_createTime != null){
                    predicates.add(p_createTime);
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        },pageable);
    }

    @Override
    public List<Brand> findByGcAndStore(String classId, String storeId) {
        return brandRepository.findByGcAndStore(classId,storeId);
    }

    @Override
    public List<String> findBrandIdListByGc(String gcId) {
        List<String> brandIdList = new ArrayList<String>();
        List<Brand> brandList = brandRepository.findBrandIdListByGc(gcId);
        if(brandList != null && brandList.size() > 0){
            for(Brand brand : brandList){
                if(brand != null ){
                    brandIdList.add(brand.getBrandId());
                }
            }
        }
        return brandIdList;
    }

    @Override
    public Brand setBrandDefault(String brandId) {
        Brand brand = brandRepository.findBrandByTheDefaultTrue();
        if(brand != null){
            brand.setTheDefault(false);
            brandRepository.save(brand);
        }
        brand = brandRepository.findByBrandIdAndDelFalse(brandId);
        if(brand != null){
            brand.setTheDefault(true);
            return brandRepository.save(brand);
        }else{
            return null;
        }

    }

    @Override
    public Brand findBrandByTheDefaultTrue() {
        return brandRepository.findBrandByTheDefaultTrue();
    }

    @Override
    public List<Brand> findByStoreAndDelFalse(Store store) {
        return brandRepository.findByStoreAndDelFalse(store);
    }
}
