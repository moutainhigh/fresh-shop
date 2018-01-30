package com.hafu365.fresh.repository.bills;

import com.hafu365.fresh.core.entity.bills.BillsInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 账单信息
 * Created by SunHaiyang on 2017/8/24.
 */
@Repository
public interface BillsInfoRepository extends JpaRepository<BillsInfo, String> {

    /**
     * 查询周期段账单信息
     * @param startTime 开始事假
     * @param endTime 结束事假
     * @return
     */
    public List<BillsInfo> findAllByCreateTimeBetween(long startTime,long endTime);

}
