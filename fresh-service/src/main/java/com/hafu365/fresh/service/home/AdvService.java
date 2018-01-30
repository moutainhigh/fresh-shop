package com.hafu365.fresh.service.home;

import com.hafu365.fresh.core.entity.home.Adv;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 广告逻辑层
 * Created by SunHaiyang on 2017/8/18.
 */
public interface AdvService {

    /**
     * 通过ID查询
     * @param advId
     * @return
     */
    public Adv findAdvByAdvId(Long advId);

    /**
     * 保存广告
     * @param adv
     * @return
     */
    public Adv saveAdv(Adv adv);

    /**
     * 查询所有显示的广告
     * @return
     */
    public List<Adv> findAllByOrderBySort();

    /**
     * 更新Adv
     * @param adv
     * @return
     */
    public Adv updateAdv(Adv adv);

    /**
     * 分页查询Adv
     * @param pageable
     * @return
     */
    public Page<Adv> findAdv(Pageable pageable);

    /**
     * 删除广告
     * @param advId
     * @return
     */
    public boolean deleteAdv(long advId);
}
