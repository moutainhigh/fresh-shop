package com.hafu365.fresh.service.basicSetup.impl;

import com.hafu365.fresh.core.entity.setting.BasicSetup;
import com.hafu365.fresh.repository.basicSetup.BasicSetupRepository;
import com.hafu365.fresh.service.basicSetup.BasicSetupService;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.List;

/**
 * 基本设置 服务层
 * Created by zhaihuilin on 2017/9/23  13:07.
 */
@Service
@Transactional
public class BasicSetupServiceImpl implements BasicSetupService {

    @Autowired
    private BasicSetupRepository basicSetupRepository;

    /**
     * 保存
     * @param basicSetup
     * @return
     */
    @Override
    public BasicSetup saveBasicSetup(BasicSetup basicSetup) {
        return basicSetupRepository.save(basicSetup);
    }

    /**
     * 编辑
     * @param basicSetup
     * @return
     */
    @Override
    public BasicSetup updateBasicSetup(BasicSetup basicSetup) {
        return basicSetupRepository.save(basicSetup);
    }

    /**
     * 根据 key进行查询
     * @param KeyNames
     * @return
     */
    @Override
    public BasicSetup findBasicSetupByKeyNames(String KeyNames) {
        return basicSetupRepository.findBasicSetupByKeyNames(KeyNames);
    }

    /**
     * 根据编号进行查询
     * @param id
     * @return
     */
    @Override
    public BasicSetup findBasicSetupById(String id) {
        return basicSetupRepository.findBasicSetupById(id);
    }

    /**
     * 根据编号进行删除
     * @param id
     * @return
     */
    @Override
    public boolean deleteBasicSetupById(String id) {
          try {
              basicSetupRepository.deleteBasicSetupById(id);
              return  true;
          }catch (Exception e){
              return  false;
          }
    }

    /**
     * 判断是否存在
     * @param KeyNames
     * @return
     */
    @Override
    public boolean existBasicSetupbyKeyNames(String KeyNames) {
        boolean flag = Boolean.FALSE;
        BasicSetup basicSetup=  basicSetupRepository.findBasicSetupByKeyNames(KeyNames);
        if (basicSetup !=null){
             flag =Boolean.TRUE;
        }
        return flag;
    }

    /**
     * 条件查询
     * @param basicSetup    设置实体类
     * @param pageable
     * @return
     */
    @Override
    public Page<BasicSetup> findAll(BasicSetup basicSetup, Pageable pageable) {
        return basicSetupRepository.findAll(BasicSetupWhere(basicSetup),pageable);
    }

    /**
     * 查询所有列表
     * @return
     */
    @Override
    public List<BasicSetup> findBasicSetupList() {
        return basicSetupRepository.findAll();
    }

    public static Specification<BasicSetup> BasicSetupWhere(
            final BasicSetup basicSetup
    ){
        return new Specification<BasicSetup>() {
            @Override
            public Predicate toPredicate(Root<BasicSetup> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                if (basicSetup.getId() !=null && ! basicSetup.getId() .equals("")){
                    predicates.add(criteriaBuilder.equal(root.<String>get("id"),basicSetup.getId()));
                }
                if (basicSetup.getKeyNames()!=null && !basicSetup.getKeyNames().equals("")){
                    predicates.add(criteriaBuilder.equal(root.<String>get("keyNames"),basicSetup.getKeyNames()));
                }
                return criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
            }
        };
    }
}
