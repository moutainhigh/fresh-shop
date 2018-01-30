package com.hafu365.fresh.service.goods.impl;

import com.hafu365.fresh.core.entity.common.UtilPage;
import com.hafu365.fresh.core.entity.goods.ModifyPriceGoods;
import com.hafu365.fresh.core.utils.StringUtils;
import com.hafu365.fresh.repository.goods.ModifyPriceGoodsRepository;
import com.hafu365.fresh.service.goods.ModifyPriceGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 修改价格商品业务实现类
 * Created by HuangWeizhen on 2017/10/11.
 */
@Transactional
@Service
public class ModifyPriceGoodsServiceImpl implements ModifyPriceGoodsService {

    @Autowired
    private ModifyPriceGoodsRepository priceRepository;

    @Override
    public ModifyPriceGoods save(ModifyPriceGoods modifyPriceGoods) {
        return priceRepository.save(modifyPriceGoods);
    }

    @Override
    public ModifyPriceGoods findById(long modifyPriceId) {
        return priceRepository.findByModifyPriceIdAndDelFalse(modifyPriceId);
    }

    @Override
    public ModifyPriceGoods findByGoodsId(String goodsId) {
        return priceRepository.findByGoodsIdAndDelFalse(goodsId);
    }

    @Override
    public boolean deleteById(long modifyPriceId) {
        try{
            priceRepository.deleteByModifyPriceId(modifyPriceId);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public boolean physicalDeleteById(long modifyPriceId) {
        try{
            priceRepository.delete(modifyPriceId);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public Page<ModifyPriceGoods> findPriceGoods(Map<String, Object> map_param) {
        final UtilPage page = (UtilPage)map_param.get("page");
        final Sort sort = new Sort(page.getDirection(),page.getPageSort());
        Pageable pageable = new PageRequest(page.getPageNum(), page.getPageSize(), sort);

        return priceRepository.findAll(new Specification<ModifyPriceGoods>() {
            @Override
            public Predicate toPredicate(Root<ModifyPriceGoods> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                //拼接删除状态
                predicates.add(cb.equal(root.<Boolean>get("del"),false));

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        },pageable);
    }

    @Override
    public List<ModifyPriceGoods> findAll() {
        return priceRepository.findAll(new Specification<ModifyPriceGoods>() {
            @Override
            public Predicate toPredicate(Root<ModifyPriceGoods> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                //拼接删除状态
                predicates.add(cb.equal(root.<Boolean>get("del"),false));

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        });
    }
}
