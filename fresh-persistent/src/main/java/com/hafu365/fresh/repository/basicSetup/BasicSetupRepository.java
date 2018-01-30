package com.hafu365.fresh.repository.basicSetup;

import com.hafu365.fresh.core.entity.setting.BasicSetup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * 基本设置保存 dao层
 * Created by zhaihuilin on 2017/9/23  11:54.
 */
@Repository
public interface BasicSetupRepository extends JpaRepository<BasicSetup,Long>,JpaSpecificationExecutor<BasicSetup> {

    /**
     * 根据键进行查询
     * @param KeyNames
     * @return
     */
     public  BasicSetup findBasicSetupByKeyNames(String KeyNames);

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
     public  void deleteBasicSetupById(String id);
}
