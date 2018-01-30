package com.hafu365.fresh.service.bills;

import com.hafu365.fresh.core.entity.bills.BillsInfo;
import com.hafu365.fresh.core.entity.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 账单信息逻辑层
 * Created by SunHaiyang on 2017/8/24.
 */
public interface BillsInfoService {

    /**
     * 保存账单信息
     * @param billsInfo
     * @return
     */
    public BillsInfo save(BillsInfo billsInfo);

    /**
     * 更新账单信息
     * @param billsInfo
     * @return
     */
    public BillsInfo update(BillsInfo billsInfo);

    /**
     * 删除账单[仅超级管理员可以使用]
     * @param id
     * @return
     */
    public boolean delete(String id);

    /**
     * 根据Id 查询账单信息
     * @param id
     * @return
     */
    public BillsInfo findBillsInfoById(String id);

    /**
     * 分页查询账单信息
     * @param pageable
     * @return
     */
    public Page<BillsInfo> findAll(Pageable pageable);

    /**
     * 查询账单
     * @param memberId
     * @return
     */
    public List<BillsInfo> findAllByBetween(String memberId);


}
