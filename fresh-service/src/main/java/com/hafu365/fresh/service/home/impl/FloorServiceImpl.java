package com.hafu365.fresh.service.home.impl;

import com.hafu365.fresh.core.entity.home.Floor;
import com.hafu365.fresh.repository.home.FloorRepository;
import com.hafu365.fresh.service.home.FloorService;
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
 * Created by SunHaiyang on 2017/8/21.
 */
@Service
@Transactional
public class FloorServiceImpl implements FloorService {

    @Autowired
    FloorRepository floorRepository;

    @Override
    public Floor findFloorByFloorId(long id) {
        return floorRepository.findAllByFloorId(id);
    }

    @Override
    @CacheEvict(value = "Floor",beforeInvocation = true,allEntries = true)
    public Floor saveFloor(Floor floor) {
        return floorRepository.save(floor);
    }

    @Override
    @CacheEvict(value = "Floor",beforeInvocation = true,allEntries = true)
    public Floor updateFloor(Floor floor) {
        return floorRepository.save(floor);
    }

    @Override
    @CacheEvict(value = "Floor",beforeInvocation = true,allEntries = true)
    public boolean deleteFloorById(long id) {
        try {
            floorRepository.delete(id);
            return true;
        }catch (Exception e){
            return false;
        }
    }


    @Override
    @Cacheable(value = "Floor",unless = "#result == null" )
    public List<Floor> findAllByOrderBySort() {
        return floorRepository.findAllByOrderBySort();
    }

    @Override
    public Page<Floor> findFloor(Pageable pageable) {
        return floorRepository.findAll(pageable);
    }

    public Specification<Floor> floorWhere(){
        return new Specification<Floor>() {
            @Override
            public Predicate toPredicate(Root<Floor> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                long time = new Date().getTime();
                List<Predicate> predicates = new ArrayList<Predicate>();
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
