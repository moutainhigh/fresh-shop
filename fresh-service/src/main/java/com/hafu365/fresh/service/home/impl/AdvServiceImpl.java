package com.hafu365.fresh.service.home.impl;

import com.hafu365.fresh.core.entity.home.Adv;
import com.hafu365.fresh.repository.home.AdvRepository;
import com.hafu365.fresh.service.home.AdvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by SunHaiyang on 2017/8/18.
 */
@Service
@Transactional
public class AdvServiceImpl implements AdvService {

    @Autowired
    private AdvRepository advRepository;

    @Override
    public Adv findAdvByAdvId(Long advId) {
        return advRepository.findOne(advId);
    }

    @Override
    @CacheEvict(value = "Adv",beforeInvocation = true,allEntries = true)
    public Adv saveAdv(Adv adv) {
        return advRepository.save(adv);
    }

    @Override
    @Cacheable(value = "Adv",unless = "#result == null" )
    public List<Adv> findAllByOrderBySort() {
        return advRepository.findAllByOrderBySort();
    }

    @Override
    @CacheEvict(value = "Adv",beforeInvocation = true,allEntries = true)
    public Adv updateAdv(Adv adv) {
        return advRepository.save(adv);
    }

    @Override
    public Page<Adv> findAdv(Pageable pageable) {
        return advRepository.findAll(pageable);
    }

    @Override
    @CacheEvict(value = "Adv",beforeInvocation = true,allEntries = true)
    public boolean deleteAdv(long advId) {
        try {
            advRepository.delete(advId);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public static Specification<Adv> advWhere(){
        return new Specification<Adv>() {
            @Override
            public Predicate toPredicate(Root<Adv> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                long time = new Date().getTime();
                predicates.add(cb.or(cb.equal(root.<Boolean>get("onlyShow"),true),
                        cb.and(
                                cb.greaterThanOrEqualTo(root.<Long>get("endTime"),time),
                                cb.lessThanOrEqualTo(root.<Long>get("startTime"),time)
                        )
                ));
                query.orderBy(cb.asc(root.<Integer>get("sort")));
                return query.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
            }
        };
    }
}
