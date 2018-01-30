package com.hafu365.fresh.service.goods.impl;

import com.hafu365.fresh.core.entity.common.UtilPage;
import com.hafu365.fresh.core.entity.goods.ModifyAttrGoods;
import com.hafu365.fresh.core.utils.StringUtils;
import com.hafu365.fresh.repository.goods.ModifyAttrGoodsRepository;
import com.hafu365.fresh.service.goods.ModifyAttrGoodsService;
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
 * 修改属性商品业务实现类
 * Created by HuangWeizhen on 2017/8/10.
 */
@Transactional
@Service
public class ModifyAttrGoodsServiceImpl implements ModifyAttrGoodsService{

    @Autowired
    private ModifyAttrGoodsRepository modifyAttrGoodsRepository;

    @Override
    public ModifyAttrGoods save(ModifyAttrGoods modifyAttrGoods) {
        return modifyAttrGoodsRepository.save(modifyAttrGoods);
    }

    @Override
    public ModifyAttrGoods findById(long modifyAttrId) {
        return modifyAttrGoodsRepository.findByModifyAttrIdAndDelFalse(modifyAttrId);
    }

    @Override
    public ModifyAttrGoods findByGoodsId(String goodsId) {
        return modifyAttrGoodsRepository.findByGoodsIdAndDelFalse(goodsId);
    }

    @Override
    public boolean deleteById(long modifyAttrId) {
        try{
            modifyAttrGoodsRepository.deleteByModifyAttrId(modifyAttrId);
            return true;
        }catch (Exception e){
            return false;
        }

    }

    @Override
    public boolean physicalDeleteById(long modifyAttrId) {
        try {
            modifyAttrGoodsRepository.delete(modifyAttrId);
            return true;
        }catch (Exception e){
            return false;
        }

    }

    @Override
    public Page<ModifyAttrGoods> findAttrGoods(Map<String, Object> map_param) {
        final ModifyAttrGoods attrGoods = (ModifyAttrGoods)map_param.get("attrGoods");
        final UtilPage page = (UtilPage)map_param.get("page");
        final Sort sort = new Sort(page.getDirection(),page.getPageSort());
        Pageable pageable = new PageRequest(page.getPageNum(), page.getPageSize(), sort);

        return modifyAttrGoodsRepository.findAll(new Specification<ModifyAttrGoods>() {
            @Override
            public Predicate toPredicate(Root<ModifyAttrGoods> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                //拼接删除状态
                predicates.add(cb.equal(root.<Boolean>get("del"),attrGoods.isDel()));

                //拼接审核状态
                if(StringUtils.isNotEmpty(attrGoods.getState())){
                    predicates.add(cb.equal(root.<String>get("state"), attrGoods.getState()));
                }

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        },pageable);
    }
}
