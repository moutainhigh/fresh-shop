package com.hafu365.fresh.repository.goods;

import com.hafu365.fresh.core.entity.goods.GoodsStock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 商品库存持久层接口
 * Created by HuangWeizhen on 2017/8/22.
 */
public interface GoodsStockRepository extends JpaRepository<GoodsStock,String>,JpaSpecificationExecutor<GoodsStock> {


    /**
     * 根据条件分页查询库存
     * @return
     */
    Page<GoodsStock> findAll(Specification<GoodsStock> stockSpecification, Pageable pageable);
}
