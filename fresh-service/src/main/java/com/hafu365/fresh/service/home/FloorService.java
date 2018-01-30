package com.hafu365.fresh.service.home;

import com.hafu365.fresh.core.entity.home.Floor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Floor逻辑层
 * Created by SunHaiyang on 2017/8/21.
 */
public interface FloorService {

    /**
     * 通过ID查询楼层
     * @param id
     * @return
     */
    public Floor findFloorByFloorId(long id);

    /**
     * 新增楼层
     * @param floor
     * @return
     */
    public Floor saveFloor(Floor floor);

    /**
     * 修改楼层
     * @param floor
     * @return
     */
    public Floor updateFloor(Floor floor);

    /**
     * 删除楼层
     * @param id
     * @return
     */
    public boolean deleteFloorById(long id);

    /**
     * 查询所有显示楼层
     * @return
     */
    public List<Floor> findAllByOrderBySort();

    /**
     * 根据页码查询楼层
     * @param pageable
     * @return
     */
    public Page<Floor> findFloor(Pageable pageable);
}
