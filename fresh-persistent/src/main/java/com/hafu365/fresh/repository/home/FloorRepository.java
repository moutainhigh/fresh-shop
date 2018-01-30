package com.hafu365.fresh.repository.home;

import com.hafu365.fresh.core.entity.home.Floor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 楼层持久层
 * Created by SunHaiyang on 2017/8/21.
 */
@Repository
public interface FloorRepository extends JpaRepository<Floor,Long> , JpaSpecificationExecutor<Floor> {

    public Floor findAllByFloorId(long id);

    public List<Floor> findAllByOrderBySort();


}
