package com.hafu365.fresh.service.store.impl;


import com.hafu365.fresh.core.entity.common.UtilPage;
import com.hafu365.fresh.core.entity.constant.StateConstant;
import com.hafu365.fresh.core.entity.goods.Brand;
import com.hafu365.fresh.core.entity.goods.Goods;
import com.hafu365.fresh.core.entity.member.Member;
import com.hafu365.fresh.core.entity.order.Orders;
import com.hafu365.fresh.core.entity.store.Store;
import com.hafu365.fresh.core.utils.Constants;
import com.hafu365.fresh.core.utils.StringUtils;
import com.hafu365.fresh.repository.goods.BrandRepository;
import com.hafu365.fresh.repository.goods.GoodsRepository;
import com.hafu365.fresh.repository.order.OrderRepository;
import com.hafu365.fresh.repository.store.StoreRepository;
import com.hafu365.fresh.service.goods.BrandService;
import com.hafu365.fresh.service.goods.GoodsService;
import com.hafu365.fresh.service.order.OrderService;
import com.hafu365.fresh.service.store.StoreService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 店铺 逻辑层
 * Created by zhaihuilin on 2017/7/21  14:51.
 */
@Transactional
@Log4j
@Service
public class StoreServiceImpl implements StoreService, Constants{

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private OrderService orderService;

    @Override
    @CacheEvict(value = "Store",beforeInvocation = true,allEntries = true)
    public Store save(Store store) {
        return storeRepository.save(store);
    }

    @Override
    @CacheEvict(value = "Store",beforeInvocation = true,allEntries = true)
    public Store update(Store store) {
        Store storeSearch = storeRepository.findByStoreIdAndDelFalse(store.getStoreId());
        if(store.getBusinessLicense() != null){
            storeSearch.setBusinessLicense(store.getBusinessLicense());
        }
        storeSearch.setOfficeTel(store.getOfficeTel());
        storeSearch.setOfficeAddress(store.getOfficeAddress());
        if(store.getFaxes() != null){
            storeSearch.setFaxes(store.getFaxes());
        }
        storeRepository.save(storeSearch);

        return storeRepository.save(store);
    }

    @Override
    @CacheEvict(value = "Store",beforeInvocation = true,allEntries = true)
    public boolean closeStore(String storeId) {
        try {
            Store store = storeRepository.findByStoreIdAndDelFalse(storeId);
            //逻辑删除店铺商品
            List<Goods> goodsList = goodsRepository.findByStore(store);
            if(goodsList != null && goodsList.size() > 0){
                for(Goods goods : goodsList){
                    goodsService.deleteByGoodsId(goods.getGoodsId());//逻辑删除商品
                }
            }

            //逻辑删除店铺品牌
            List<Brand> brandList = brandRepository.findByStoreAndDelFalse(store);
            if(brandList != null && brandList.size() > 0){
                for(Brand brand : brandList){
                    brandService.delete(brand.getBrandId());
                }
            }

            //逻辑删除店铺订单
            List<Orders> ordersList = orderRepository.findByStoreAndDelFalse(store);
            if(ordersList != null && ordersList.size() > 0){
                for(Orders orders : ordersList){
                    orderService.deleteOrderByordersId(orders.getOrdersId());
                }
            }

            //物理删除店铺与员工的关联
            store.setChildMember(null);
            storeRepository.save(store);

            //关闭店铺
            storeRepository.closeStore(StateConstant.STORE_STATE_ON_CLOSE.toString(),storeId);
            return true;
        }catch (Exception e){
            return false;
        }

    }

    @Override
    @CacheEvict(value = "Store",beforeInvocation = true,allEntries = true)
    public boolean deleteByStoreId(String storeId) {
        try {
            Store store = storeRepository.findByStoreIdAndDelFalse(storeId);
            //逻辑删除店铺商品
            List<Goods> goodsList = goodsRepository.findByStore(store);
            if(goodsList != null && goodsList.size() > 0){
                for(Goods goods : goodsList){
                    goodsService.deleteByGoodsId(goods.getGoodsId());//逻辑删除商品
                }
            }

            //删除店铺品牌(自动删除与分类的关联)
            List<Brand> brandList = brandRepository.findByStoreAndDelFalse(store);
            if(brandList != null && brandList.size() > 0){
                for(Brand brand : brandList){
                    brandService.delete(brand.getBrandId());
                }
            }

            //逻辑删除店铺订单
            List<Orders> ordersList = orderRepository.findByStoreAndDelFalse(store);
            if(ordersList != null && ordersList.size() > 0){
                for(Orders orders : ordersList){
                    orderService.deleteOrderByordersId(orders.getOrdersId());
                }
            }

            //物理删除店铺与员工的关联
            store.setChildMember(null);
            storeRepository.save(store);

            //逻辑删除店铺
            storeRepository.deleteByStoreId(storeId);
            return true;
        }catch (Exception e){
            return false;
        }

    }

   @Override
   @CacheEvict(value = "Store",beforeInvocation = true,allEntries = true)
    public boolean deleteStore(String storeId) {
        try {
            Store store = storeRepository.findByStoreIdAndDelFalse(storeId);
            //物理删除店铺商品
            List<Goods> goodsList = goodsRepository.findByStore(store);
            if(goodsList != null && goodsList.size() > 0){
                for(Goods goods : goodsList){
                    goodsService.deleteGoods(goods.getGoodsId());//物理删除商品
                }
            }

            //物理删除店铺品牌(自动删除与分类的关联)
            List<Brand> brandList = brandRepository.findByStoreAndDelFalse(store);
            if(brandList != null && brandList.size() > 0){
                for(Brand brand : brandList){
                    brandService.physicalDelete(brand.getBrandId());
                }
            }

            //物理删除店铺订单
            List<Orders> ordersList = orderRepository.findByStoreAndDelFalse(store);
            if(ordersList != null && ordersList.size() > 0){
                for(Orders orders : ordersList){
                    orderService.physicallyDeleteByordersId(orders.getOrdersId());
                }
            }

            //物理删除店铺与员工的关联
            store.setChildMember(null);
            storeRepository.save(store);

            //物理删除店铺
            storeRepository.delete(storeId);
            return true;
        }catch (Exception e){
            return false;
        }

    }

    @Override
    public Store findByStoreId(String storeId) {
        return storeRepository.findByStoreIdAndDelFalse(storeId);
    }

    @Override
    public Store findByStoreName(String storeName) {
        return storeRepository.findByStoreNameAndDelFalse(storeName);
    }

    @Override
    //@Cacheable(value = "Store", unless = "#result == null ")
    public Page<Store> findByCondition(Map<String,Object> map_param) {
        final Store store = (Store) map_param.get("store");
        final UtilPage page = (UtilPage)map_param.get("page");
        final long startTime = (Long) map_param.get("startTime");
        final long endTime = (Long) map_param.get("endTime");
        final Pageable pageable = new PageRequest(page.getPageNum(),page.getPageSize(),new Sort(page.getDirection(),page.getPageSort()));

        return storeRepository.findAll(new Specification<Store>() {
            @Override
            public javax.persistence.criteria.Predicate toPredicate(Root<Store> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<Predicate>();

                //拼接店铺id查询
                if(StringUtils.isNotEmpty(store.getStoreId())){
                    predicates.add(cb.equal(root.<String>get("storeId"),store.getStoreId()));
                }

                //拼接店铺名称模糊查询
                if(StringUtils.isNotEmpty(store.getStoreName())){
                    predicates.add(cb.like(root.<String>get("storeName"),"%" + store.getStoreName().trim() + "%"));
                }

                //拼接店主查询
                if(store.getMember() != null){
                    predicates.add(cb.equal(root.<String>get("member"),store.getMember()));
                }
                //拼接执照编号查询
                if(StringUtils.isNotEmpty(store.getBusinessLicenseNo())){
                    predicates.add(cb.equal(root.get("businessLicenseNo"),store.getBusinessLicenseNo()));
                }

                //拼接公司成立时间查询
                if(store.getRegTime() != 0){
                    predicates.add(cb.equal(root.get("regTime"),store.getRegTime()));
                }

                //拼接删除状态查询
                predicates.add(cb.equal(root.get("del"),store.isDel()));

                //拼接审核状态查询
                if(StringUtils.isNotEmpty(store.getState())){
                    predicates.add(cb.equal(root.get("state"), store.getState()));
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
    public boolean isExist(String storeId) {
        if(storeRepository.exists(storeId)){
            Store store = storeRepository.findByStoreIdAndDelFalse(storeId);
            if(store != null){
                return true;
            }
        }
        return false;
    }

    @Override
    public Store findBychildMember(String memberId) {
        return storeRepository.findBychilMember(memberId);
    }

    @Override
    public Store findByMember(Member member) {
        return storeRepository.findByMemberAndDelFalse(member);
    }

    @Override
    public List<Store> findByDelFalseAndState(String state) {
        return storeRepository.findByDelFalseAndState(state);
    }

    @Override
    public Store setStoreDefault(String storeId) {
        Store store = storeRepository.findStoreByTheDefaultTrue();
        if(store != null){
            store.setTheDefault(false);
            storeRepository.save(store);
        }
        store = storeRepository.findByStoreIdAndDelFalse(storeId);
        if(store != null){
            store.setTheDefault(true);
            return storeRepository.save(store);
        }else{
            return null;
        }
    }

    @Override
    public Store findStoreByTheDefaultTrue() {
        return storeRepository.findStoreByTheDefaultTrue();
    }
}
