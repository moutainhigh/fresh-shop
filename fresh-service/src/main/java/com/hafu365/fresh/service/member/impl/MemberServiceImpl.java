package com.hafu365.fresh.service.member.impl;


import com.hafu365.fresh.core.entity.member.Member;
import com.hafu365.fresh.core.entity.member.Role;
import com.hafu365.fresh.core.entity.store.Store;
import com.hafu365.fresh.core.utils.Constants;
import com.hafu365.fresh.core.utils.StringUtils;
import com.hafu365.fresh.repository.member.MemberRepository;
import com.hafu365.fresh.repository.role.RoleRepository;
import com.hafu365.fresh.service.member.MemberService;
import com.hafu365.fresh.service.role.RoleService;
import com.hafu365.fresh.service.store.StoreService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;


/**
 * 用户 逻辑层
 * Created by zhaihuilin on 2017/7/21  14:45.
 */
@Transactional
@Service
@Log4j
public class MemberServiceImpl implements MemberService, Constants {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StoreService storeService;

    @Autowired
    RoleRepository roleRepository;

    @PersistenceContext
    @Qualifier(value = "entityManagerFactory")
    private EntityManager em;


    @Override
    @Transactional(readOnly = true)
    public Member findMemberByUsername(String username) {
        Member member = memberRepository.findMemberByUsernameAndDelFalse(username);
        em.contains(member);
        return member;
    }

    @Override
    @Transactional(readOnly = true)
    public Member findMemberByMemberId(String memberId) {
        Member member = memberRepository.findMemberByMemberIdAndDelFalse(memberId);
        em.contains(member);
        return member;
    }


    /**
     *  保存 用户信息
     * @param member
     * @return
     */
    @Override
    public Member saveMember(Member member) {
        return memberRepository.save(member);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Member> findAllByMember(Member member, Pageable pageable) {
        Page<Member> members = memberRepository.findAll(memberWhere(member),pageable);
        em.contains(members);
        return members;
    }

    @Override
    public boolean deleteByMemberId(String memberId) {
        try {
            //删除用户时，关联删除店铺
            Member m = memberRepository.findMemberByMemberIdAndDelFalse(memberId);
            Store store = storeService.findByMember(m);
            if(store != null){
                storeService.deleteByStoreId(store.getStoreId());
            }
            memberRepository.deleteByMemberId(memberId);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public boolean physicallyDeleteByMemberId(String memberId) {
        try{
            //删除用户时，关联删除店铺
            Member m = memberRepository.findMemberByMemberIdAndDelFalse(memberId);
            Store store = storeService.findByMember(m);
            if(store != null){
                storeService.deleteByStoreId(store.getStoreId());
            }
            memberRepository.delete(memberId);
            return true;
        }catch (Exception e){
            return false;
        }

    }

    @Override
    public boolean existMemberbyUsername(String username) {
        boolean flag = Boolean.FALSE;
        Member member = memberRepository.findMemberByUsername(username);
        if (member != null){
            flag = Boolean.TRUE;
        }
        return flag;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Member> findAll() {
        List<Member> members = memberRepository.findAllByDelFalse();
        em.contains(members);
        return members;
    }

    @Override
    public List<String> findUsername() {
        return memberRepository.findUsernames();
    }

    /**
     * 根据用户名获取简易的用户信息
     * @param username  用户名   ZHL
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public Member findSimpleMemberByUsername(String username) {
        Member member = memberRepository.findMemberByUsername(username);
        em.contains(member);
        return member;

    }

    /**
     *根据用户编号获取简易的用户信息
     * @param memberId  用户编号   ZHL
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public Member findSimpleMemberByMemberId(String memberId) {
        Member member = memberRepository.findMemberByMemberIdAndDelFalse(memberId);
        em.contains(member);
        return member;
    }


    /**
     * 条件查询
     * @param member
     * @return
     */
    public static Specification<Member> memberWhere(
            final Member member
    ){
        return new Specification<Member>() {
            @Override
            public Predicate toPredicate(Root<Member> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                List<Predicate> predicates = new ArrayList<Predicate>();
                if(member.getMemberId() != null && !member.getMemberId().equals("")){
                    predicates.add(cb.equal(root.<String>get("memberId"),member.getMemberId()));
                }
                if(member.getUsername() != null && !member.getUsername().equals("")){
                    predicates.add(cb.like(root.<String>get("username"),"%"+member.getUsername()+"%"));
                }
                if(member.getNickName() != null && !member.getNickName().equals("")){
                    predicates.add(cb.like(root.<String>get("nickName"),"%"+member.getNickName()+"%"));
                }
                if(StringUtils.isNotEmpty(member.getName())){
                    predicates.add(cb.like(root.<String>get("name"),"%"+member.getName()+"%"));
                }
                if (member.getEMail() != null && !member.getEMail().equals("")){
                    predicates.add(cb.like(root.<String>get("eMail"),"%"+member.getEMail()+"%"));
                }
                if (member.getIdCardNo() != null && !member.getIdCardNo().equals("")){
                    predicates.add(cb.like(root.<String>get("idCardNo"),"%"+member.getIdCardNo()+"%"));
                }
                if(member.getPhone() != null && !member.getPhone().equals("")){
                    predicates.add(cb.like(root.<String>get("phone"),"%"+member.getPhone()+"%"));
                }
                if(member.getState() != null && !member.getState().equals("")){
                    predicates.add(cb.equal(root.<String>get("state"),member.getState()));
                }
                predicates.add(cb.equal(root.<Boolean>get("del"),member.isDel()));
                return query.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
            }
        };
    }

}
