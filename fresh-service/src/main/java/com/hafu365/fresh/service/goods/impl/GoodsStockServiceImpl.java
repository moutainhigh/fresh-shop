package com.hafu365.fresh.service.goods.impl;

import com.hafu365.fresh.core.entity.common.UtilPage;
import com.hafu365.fresh.core.entity.goods.Goods;
import com.hafu365.fresh.core.entity.goods.GoodsStock;
import com.hafu365.fresh.core.utils.StringUtils;
import com.hafu365.fresh.repository.goods.GoodsRepository;
import com.hafu365.fresh.repository.goods.GoodsStockRepository;
import com.hafu365.fresh.service.goods.GoodsStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 库存业务实现类
 * Created by HuangWeizhen on 2017/8/22.
 */
@Transactional
@Service
public class GoodsStockServiceImpl implements GoodsStockService {
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private GoodsStockRepository stockRepository;

    @Autowired
    private GoodsRepository goodsRepository;

    @Override
    public GoodsStock saveOrUpdate(GoodsStock goodsStock) {
        return stockRepository.save(goodsStock);
    }

    @Override
    public GoodsStock findByStockId(String stockId) {
        return stockRepository.getOne(stockId);
    }

    @Override
    public Page<GoodsStock> findByCondition(Map<String, Object> paramMap) {
        final GoodsStock stock = (GoodsStock)paramMap.get("stock");
        final String numCondition = (String)paramMap.get("numCondition");
        final UtilPage page = (UtilPage)paramMap.get("page");
        Pageable pageable = new PageRequest(page.getPageNum(),page.getPageSize(),new Sort(page.getDirection(),page.getPageSort()));
        return stockRepository.findAll(new Specification<GoodsStock>() {
            @Override
            public Predicate toPredicate(Root<GoodsStock> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                //拼接id查询
                if(StringUtils.isNotEmpty(stock.getStockId())){
                    predicates.add(cb.equal(root.<Long>get("stockId"),stock.getStockId()));
                }
                //拼接sku模糊查询
                if(StringUtils.isNotEmpty(stock.getSku())){
                    predicates.add(cb.like(root.<String>get("sku"),"%" + stock.getSku() + "%"));
                }
                //拼接库存数量查询
                if(stock.getStockNum() != -1 && StringUtils.isNotEmpty(numCondition)){
                    if(numCondition.equals("gt")){
                        predicates.add(cb.greaterThan(root.<Long>get("stockNum"),stock.getStockNum()));
                    }else if(numCondition.equals("gtAndEq")){
                        predicates.add(cb.greaterThanOrEqualTo(root.<Long>get("stockNum"),stock.getStockNum()));
                    }else if(numCondition.equals("eq")){
                        predicates.add(cb.equal(root.<Long>get("stockNum"),stock.getStockNum()));
                    }else if(numCondition.equals("lt")){
                        predicates.add(cb.lessThan(root.<Long>get("stockNum"),stock.getStockNum()));
                    }else if(numCondition.equals("ltAndEq")){
                        predicates.add(cb.lessThanOrEqualTo(root.<Long>get("stockNum"),stock.getStockNum()));
                    }else{

                    }

                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, pageable);
    }

}
