package com.hafu365.fresh.service.basicSetup;

import com.hafu365.fresh.core.entity.setting.BasicSetup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 基本设置 逻辑层
 * Created by zhaihuilin on 2017/9/23  11:56.
 */
public interface BasicSetupService {

    /**
     * 保存
     * @param basicSetup
     * @return
     */
      public BasicSetup saveBasicSetup(BasicSetup basicSetup);

    /**
     * 编辑
     * @param basicSetup
     * @return
     */
      public BasicSetup updateBasicSetup(BasicSetup basicSetup);

    /**
     * 根据键的名称查询
     * @param KeyNames
     * @return
     */
      public BasicSetup findBasicSetupByKeyNames(String KeyNames);

    /**
     * 判断是否存在
     * @param KeyNames
     * @return
     */
      public boolean  existBasicSetupbyKeyNames(String KeyNames);

    /**
     * 根据编号进行查询
     * @param id
     * @return
     */
    public  BasicSetup findBasicSetupById(String id);

    /**
     * 根据编号进行删除
     * @param id
     * @return
     */
    public  boolean  deleteBasicSetupById(String id);

    /**
     * 条件查询
     * @param basicSetup    设置实体类
     * @param pageable
     * @return
     */
    public Page<BasicSetup> findAll(
            BasicSetup basicSetup,
            Pageable pageable
    );

    /**
     * 查询所有列表
     * @return
     */
    public List<BasicSetup> findBasicSetupList();
}
