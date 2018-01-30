package com.hafu365.fresh.repository.home;

import com.hafu365.fresh.core.entity.home.Adv;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 广告持久化
 * Created by SunHaiyang on 2017/8/18.
 */
@Repository
public interface AdvRepository extends JpaRepository<Adv,Long> , JpaSpecificationExecutor<Adv> {



    public List<Adv> findAdvByOnlyShowTrue();

    /**
     * 查询所有广告
     * @return
     */
    public List<Adv> findAllByOrderBySort();

}
