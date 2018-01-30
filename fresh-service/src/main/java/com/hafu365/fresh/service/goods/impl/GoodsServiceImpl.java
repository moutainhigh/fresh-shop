package com.hafu365.fresh.service.goods.impl;

import com.hafu365.fresh.core.entity.common.UtilPage;
import com.hafu365.fresh.core.entity.constant.StateConstant;
import com.hafu365.fresh.core.entity.goods.*;
import com.hafu365.fresh.core.entity.store.Store;
import com.hafu365.fresh.core.utils.StringUtils;
import com.hafu365.fresh.repository.goods.GoodsCommentRepository;
import com.hafu365.fresh.repository.goods.GoodsRepository;
import com.hafu365.fresh.repository.goods.GoodsStockRepository;
import com.hafu365.fresh.repository.goods.ModifyAttrGoodsRepository;
import com.hafu365.fresh.service.goods.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 商品业务实现类
 * Created by HuangWeizhen on 2017/7/26.
 */
@Transactional
@Service
public class GoodsServiceImpl implements GoodsService {


    @PersistenceContext
    private EntityManager em;
    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private ModifyAttrGoodsRepository modifyAttrGoodsRepository;

    @Autowired
    private GoodsCommentRepository goodsCommentRepository;

    @Autowired
    private GoodsStockRepository stockRepository;

    @Override
    public Goods save(Goods goods,GoodsStock goodsStock) {
        GoodsStock stock = stockRepository.save(goodsStock);//添加商品库存
        goods.setGoodsStock(stock);
        return goodsRepository.save(goods);//添加商品
    }

    @Override
    @CacheEvict(value = "Goods",key = "#goods.goodsId",beforeInvocation = true,allEntries = true)
    public Goods update(Goods goods, GoodsStock goodsStock) {

        GoodsStock stock = stockRepository.save(goodsStock);//更新商品库存
        Goods goodsSearch = goodsRepository.findByGoodsIdAndDelFalse(goods.getGoodsId());
        goodsSearch.setGoodsPic(goods.getGoodsPic());
        goodsSearch.setPrice(goods.getPrice());
        goodsRepository.save(goodsSearch);

        goods.setGoodsStock(stock);
        return goodsRepository.save(goods);
    }

    @Override
    @CacheEvict(value = "Goods",key = "#goods.goodsId",beforeInvocation = true,allEntries = true)
    public Goods updateGoods(Goods goods) {
        Goods goodsSearch = goodsRepository.findByGoodsIdAndDelFalse(goods.getGoodsId());
        goodsSearch.setGoodsPic(goods.getGoodsPic());
        goodsSearch.setPrice(goods.getPrice());
        goodsRepository.save(goodsSearch);
        return goodsRepository.save(goods);
    }

    /**
     * 获取某店铺下所有的商品
     * @param store   ZHL
     * @return
     */
    @Override
    public List<Goods> findByStoreAndDelFalse(Store store) {
        return goodsRepository.findByStore(store);
    }

    @Override
    public Goods findByGoodsId(String goodsId) {
        Goods goods = goodsRepository.findByGoodsIdAndDelFalse(goodsId);
        return goods;
    }

    /**
     * 根据商品编号获取商品信息
     * @param goodsId     ZHL
     * @return
     */
    @Override
    public Goods findGoodsByGoodsId(String goodsId) {
        Goods goods =goodsRepository.findByGoodsIdAndDelFalse(goodsId);
        return goods;
    }

    @Override
    public boolean isExist(String goodsId) {
        if(goodsRepository.exists(goodsId)){
            Goods goods = goodsRepository.findByGoodsIdAndDelFalse(goodsId);
            if(goods != null){
                return true;
            }
        }
        return false;
    }

    @Override
    public Page<Goods> findByCondition(Map<String,Object> map_param) {

        //获取参数
        final Goods goods = (Goods)map_param.get("goods");
        final Store store = (Store)map_param.get("store");
        final List<GoodsClass> gcList = (List<GoodsClass>)map_param.get("gcList");
        final Brand brand = (Brand)map_param.get("brand");
        final long startTime = (Long) map_param.get("startTime");
        final long endTime = (Long) map_param.get("endTime");
        final String goodsShowStr = (String) map_param.get("goodsShowStr");
        final Long stockNum = (Long)map_param.get("stockNum");
        final String numCondition = (String)map_param.get("numCondition");
        final String inSoldTime = (String) map_param.get("inSoldTime");
        final UtilPage page = (UtilPage)map_param.get("page");
        final Sort sort = new Sort(page.getDirection(),page.getPageSort());
        Pageable pageable = new PageRequest(page.getPageNum(), page.getPageSize(), sort);

        return goodsRepository.findAll(new Specification<Goods>() {
            @Override
            public Predicate toPredicate(Root<Goods> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {

                Join<Goods, GoodsStock> stockJoin = root.join("goodsStock", JoinType.INNER);    //库存关联
                Join<Goods, GoodsClass> gcJoin = root.join("goodsClass", JoinType.INNER);   //分类关联

                List<Predicate> predicates = new ArrayList<Predicate>();

                //拼接库存数量查询
                if(stockNum != -1 && StringUtils.isNotEmpty(numCondition)){
                    if(numCondition.equals("gt")){
                        predicates.add(cb.greaterThan(stockJoin.<Long>get("stockNum"),stockNum));
                    }else if(numCondition.equals("gtAndEq")){
                        predicates.add(cb.greaterThanOrEqualTo(stockJoin.<Long>get("stockNum"),stockNum));
                    }else if(numCondition.equals("eq")){
                        predicates.add(cb.equal(stockJoin.<Long>get("stockNum"),stockNum));
                    }else if(numCondition.equals("lt")){
                        predicates.add(cb.lessThan(stockJoin.<Long>get("stockNum"),stockNum));
                    }else if(numCondition.equals("ltAndEq")){
                        predicates.add(cb.lessThanOrEqualTo(stockJoin.<Long>get("stockNum"),stockNum));
                    }else{

                    }

                }

                //拼接id查询
                if(StringUtils.isNotEmpty(goods.getGoodsId())){
                    predicates.add(cb.equal(root.<String>get("goodsId"),goods.getGoodsId()));
                }
                //拼接商品标题模糊查询
                List<Predicate> or_predicates_gt = new ArrayList<Predicate>();
                if(StringUtils.isNotEmpty(goods.getGoodsTitle())){
                    String goodsTitle = goods.getGoodsTitle().trim();
                    Predicate p_title_1 = cb.like(root.<String>get("goodsTitle"),"%"+goodsTitle+"%");
                    Predicate p_title_2 = cb.like(root.<String>get("goodsSubTitle"),"%"+goodsTitle+"%");
                    Predicate p_title_3 = cb.like(root.<String>get("keywords"),"%"+goodsTitle+"%");
                    or_predicates_gt.add(p_title_1);
                    or_predicates_gt.add(p_title_2);
                    or_predicates_gt.add(p_title_3);
                    Predicate gt_predicate_or = cb.or(or_predicates_gt.toArray(new Predicate[or_predicates_gt.size()]));
                    predicates.add(gt_predicate_or);
                }

                //拼接商品副标题模糊查询
                List<Predicate> or_predicates_gst = new ArrayList<Predicate>();
                if(StringUtils.isNotEmpty(goods.getGoodsSubTitle())){
                    String goodsSubTitle = goods.getGoodsSubTitle().trim();
                    Predicate p_subTitle_1 = cb.like(root.<String>get("goodsTitle"),"%"+goodsSubTitle+"%");
                    Predicate p_subTitle_2 = cb.like(root.<String>get("goodsSubTitle"),"%"+goodsSubTitle+"%");
                    Predicate p_subTitle_3 = cb.like(root.<String>get("keywords"),"%"+goodsSubTitle+"%");
                    or_predicates_gst.add(p_subTitle_1);
                    or_predicates_gst.add(p_subTitle_2);
                    or_predicates_gst.add(p_subTitle_3);
                    Predicate gst_predicate_or = cb.or(or_predicates_gst.toArray(new Predicate[or_predicates_gst.size()]));
                    predicates.add(gst_predicate_or);
                }
                //拼接关键字模糊查询
                List<Predicate> or_predicates_kw = new ArrayList<Predicate>();
                if(StringUtils.isNotEmpty(goods.getKeywords())){
                    String keyword = goods.getKeywords().trim();
                    Predicate p_keyword_1 = cb.like(root.<String>get("goodsTitle"),"%"+keyword+"%");
                    Predicate p_keyword_2 = cb.like(root.<String>get("goodsSubTitle"),"%"+keyword+"%");
                    Predicate p_keyword_3 = cb.like(root.<String>get("keywords"),"%"+keyword+"%");
                    or_predicates_kw.add(p_keyword_1);
                    or_predicates_kw.add(p_keyword_2);
                    or_predicates_kw.add(p_keyword_3);
                    Predicate kw_predicate_or = cb.or(or_predicates_kw.toArray(new Predicate[or_predicates_kw.size()]));
                    predicates.add(kw_predicate_or);
                }

                //拼接删除状态查询
                Predicate p_del = cb.equal(root.<Boolean>get("del"),goods.isDel());
                predicates.add(p_del);

                //拼接显示状态查询（不显示的分类下的商品也给显示）
                if(goodsShowStr.equals("0")){//不显示
                    /*List<Predicate> or_predicates_show = new ArrayList<Predicate>();
                    Predicate p_goodsShow_false = cb.equal(root.get("goodsShow"),false);
                    or_predicates_show.add(p_goodsShow_false);
                    //拼接分类 显示状态 查询
                    or_predicates_show.add(cb.equal(gcJoin.<Boolean>get("gcShow"), Boolean.FALSE));
                    predicates.add(cb.or(or_predicates_show.toArray(new Predicate[or_predicates_show.size()])));*/
                    predicates.add(cb.equal(root.<Boolean>get("goodsShow"),false));

                }else if(goodsShowStr.equals("1")){//显示
                    Predicate p_goodsShow_true = cb.equal(root.<Boolean>get("goodsShow"),true);
                    predicates.add(p_goodsShow_true);
                    //拼接分类显示状态 查询
//                    predicates.add(cb.equal(gcJoin.<Boolean>get("gcShow"), Boolean.TRUE));
                }else{//全部

                }

                //拼接审核状态查询（审核通过且当前时间在上下架范围内）
                if(StringUtils.isNotEmpty(goods.getState())){
                    Predicate p_state = cb.equal(root.<String>get("state"), goods.getState());
                    predicates.add(p_state);

                }

                //拼接是否在上下架区间内查询
                if(inSoldTime.equals("true")){
                    long time = System.currentTimeMillis();
                    predicates.add(cb.lessThanOrEqualTo(root.<Long>get("soldInTime"),time));
                    predicates.add(cb.greaterThanOrEqualTo(root.<Long>get("soldOutTime"),time));
                }

                //拼接创建时间区间查询
                Predicate p_createTime = null;
                if(startTime != 0 && endTime != 0 && (startTime < endTime)){
                    p_createTime = cb.between(root.<Long>get("createTime"),startTime,endTime);
                }else if(startTime != 0 && endTime == 0){
                    p_createTime = cb.between(root.<Long>get("createTime"),startTime,System.currentTimeMillis());

                }else if(startTime == 0 && endTime != 0){
                    p_createTime = cb.between(root.<Long>get("createTime"),0l,endTime);
                }else{
                    p_createTime = null;
                }
                if(p_createTime != null){
                    predicates.add(p_createTime);
                }
                //拼接佣金查询
                if(goods.getCommission() != 0){
                    predicates.add(cb.equal(root.<Double>get("commission"),goods.getCommission()));

                }
                //拼接库存查询
                if(goods.getGoodsStock() != null){
                    predicates.add(cb.equal(root.<String>get("goodsStock"),goods.getGoodsStock()));

                }

                //拼接商品所属店铺查询语句
                if(store != null && StringUtils.isNotEmpty(store.getStoreId())){
                    Predicate p_store = cb.equal(root.<String>get("store"),store);
                    predicates.add(p_store);

                }

                //拼接商品分类查询语句
                List<Predicate> or_predicates_gc = new ArrayList<Predicate>();
                if(gcList != null && gcList.size() != 0){
                    for(GoodsClass gc : gcList){
                        Predicate p_gc_child = cb.equal(root.<String>get("goodsClass"),gc);
                        or_predicates_gc.add(p_gc_child);
                    }
                }
                if(or_predicates_gc.size() != 0){
                    Predicate gc_predicate_or = cb.or(or_predicates_gc.toArray(new Predicate[or_predicates_gc.size()]));
                    predicates.add(gc_predicate_or);
                }
                //拼接品牌查询语句
                if(brand != null && StringUtils.isNotEmpty(brand.getBrandId())){
                    predicates.add(cb.equal(root.<String>get("brand"),brand));
                }


                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, pageable);

    }

    @Override
    public Page<Goods> findSimpleGoodsByCondition(Map<String,Object> map_param) {

        //获取参数
        final Goods goods = (Goods)map_param.get("goods");
        final Store store = (Store)map_param.get("store");
        final List<GoodsClass> gcList = (List<GoodsClass>)map_param.get("gcList");
        final Brand brand = (Brand)map_param.get("brand");
        final long startTime = (Long) map_param.get("startTime");
        final long endTime = (Long) map_param.get("endTime");
        final String goodsShowStr = (String) map_param.get("goodsShowStr");
        final Long stockNum = (Long)map_param.get("stockNum");
        final String numCondition = (String)map_param.get("numCondition");
        final String inSoldTime = (String) map_param.get("inSoldTime");
        final UtilPage page = (UtilPage)map_param.get("page");
        final Sort sort = new Sort(page.getDirection(),page.getPageSort());
        final Pageable pageable = new PageRequest(page.getPageNum(), page.getPageSize(), sort);

        //创建查询
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Goods> cq = cb.createQuery(Goods.class);
        final Root<Goods> root = cq.from(Goods.class);

        Join<Goods, GoodsStock> stockJoin = root.join("goodsStock", JoinType.INNER);    //库存关联
        Join<Goods, GoodsClass> gcJoin = root.join("goodsClass", JoinType.INNER);   //分类关联

        List<Predicate> predicates = new ArrayList<Predicate>();

        //拼接库存数量查询
        if(stockNum != -1 && StringUtils.isNotEmpty(numCondition)){
            if(numCondition.equals("gt")){
                predicates.add(cb.greaterThan(stockJoin.<Long>get("stockNum"),stockNum));
            }else if(numCondition.equals("gtAndEq")){
                predicates.add(cb.greaterThanOrEqualTo(stockJoin.<Long>get("stockNum"),stockNum));
            }else if(numCondition.equals("eq")){
                predicates.add(cb.equal(stockJoin.<Long>get("stockNum"),stockNum));
            }else if(numCondition.equals("lt")){
                predicates.add(cb.lessThan(stockJoin.<Long>get("stockNum"),stockNum));
            }else if(numCondition.equals("ltAndEq")){
                predicates.add(cb.lessThanOrEqualTo(stockJoin.<Long>get("stockNum"),stockNum));
            }else{

            }

        }

        //拼接商品标题模糊查询
        List<Predicate> or_predicates_gt = new ArrayList<Predicate>();
        if(StringUtils.isNotEmpty(goods.getGoodsTitle())){
            String goodsTitle = goods.getGoodsTitle().trim();
            Predicate p_title_1 = cb.like(root.<String>get("goodsTitle"),"%"+goodsTitle+"%");
            Predicate p_title_2 = cb.like(root.<String>get("goodsSubTitle"),"%"+goodsTitle+"%");
            Predicate p_title_3 = cb.like(root.<String>get("keywords"),"%"+goodsTitle+"%");
            or_predicates_gt.add(p_title_1);
            or_predicates_gt.add(p_title_2);
            or_predicates_gt.add(p_title_3);
            Predicate gt_predicate_or = cb.or(or_predicates_gt.toArray(new Predicate[or_predicates_gt.size()]));
            predicates.add(gt_predicate_or);
        }

        //拼接商品副标题模糊查询
        List<Predicate> or_predicates_gst = new ArrayList<Predicate>();
        if(StringUtils.isNotEmpty(goods.getGoodsSubTitle())){
            String goodsSubTitle = goods.getGoodsSubTitle().trim();
            Predicate p_subTitle_1 = cb.like(root.<String>get("goodsTitle"),"%"+goodsSubTitle+"%");
            Predicate p_subTitle_2 = cb.like(root.<String>get("goodsSubTitle"),"%"+goodsSubTitle+"%");
            Predicate p_subTitle_3 = cb.like(root.<String>get("keywords"),"%"+goodsSubTitle+"%");
            or_predicates_gst.add(p_subTitle_1);
            or_predicates_gst.add(p_subTitle_2);
            or_predicates_gst.add(p_subTitle_3);
            Predicate gst_predicate_or = cb.or(or_predicates_gst.toArray(new Predicate[or_predicates_gst.size()]));
            predicates.add(gst_predicate_or);
        }
        //拼接关键字模糊查询
        List<Predicate> or_predicates_kw = new ArrayList<Predicate>();
        if(StringUtils.isNotEmpty(goods.getKeywords())){
            String keyword = goods.getKeywords().trim();
            Predicate p_keyword_1 = cb.like(root.<String>get("goodsTitle"),"%"+keyword+"%");
            Predicate p_keyword_2 = cb.like(root.<String>get("goodsSubTitle"),"%"+keyword+"%");
            Predicate p_keyword_3 = cb.like(root.<String>get("keywords"),"%"+keyword+"%");
            or_predicates_kw.add(p_keyword_1);
            or_predicates_kw.add(p_keyword_2);
            or_predicates_kw.add(p_keyword_3);
            Predicate kw_predicate_or = cb.or(or_predicates_kw.toArray(new Predicate[or_predicates_kw.size()]));
            predicates.add(kw_predicate_or);
        }

        //拼接删除状态查询
        Predicate p_del = cb.equal(root.get("del"),goods.isDel());
        predicates.add(p_del);

        //拼接显示状态查询
        if(goodsShowStr.equals("0")){//不显示
            List<Predicate> or_predicates_show = new ArrayList<Predicate>();
            Predicate p_goodsShow_false = cb.equal(root.get("goodsShow"),false);
            or_predicates_show.add(p_goodsShow_false);
            //拼接分类 显示状态 查询
            or_predicates_show.add(cb.equal(gcJoin.<Boolean>get("gcShow"), Boolean.FALSE));
            predicates.add(cb.or(or_predicates_show.toArray(new Predicate[or_predicates_show.size()])));

        }else if(goodsShowStr.equals("1")){//显示
            Predicate p_goodsShow_true = cb.equal(root.get("goodsShow"),true);
            predicates.add(p_goodsShow_true);
            //拼接分类 显示状态 查询
            predicates.add(cb.equal(gcJoin.<Boolean>get("gcShow"), Boolean.TRUE));
        }else{//全部

        }

        //拼接审核状态
        if(StringUtils.isNotEmpty(goods.getState())){
            Predicate p_state = cb.equal(root.<String>get("state"), goods.getState());
            predicates.add(p_state);

        }

        //拼接是否在上下架区间内查询
        if(inSoldTime.equals("true")){
            long time = System.currentTimeMillis();
            predicates.add(cb.lessThanOrEqualTo(root.<Long>get("soldInTime"),time));
            predicates.add(cb.greaterThanOrEqualTo(root.<Long>get("soldOutTime"),time));
        }

        //拼接创建时间区间查询
        Predicate p_createTime = null;
        if(startTime != 0 && endTime != 0 && (startTime < endTime)){
            p_createTime = cb.between(root.<Long>get("createTime"),startTime,endTime);
        }else if(startTime != 0 && endTime == 0){
            p_createTime = cb.between(root.<Long>get("createTime"),startTime,System.currentTimeMillis());

        }else if(startTime == 0 && endTime != 0){
            p_createTime = cb.between(root.<Long>get("createTime"),0l,endTime);
        }else{
            p_createTime = null;
        }
        if(p_createTime != null){
            predicates.add(p_createTime);
        }
        //拼接佣金查询
        if(goods.getCommission() != 0){
            predicates.add(cb.equal(root.<Double>get("commission"),goods.getCommission()));

        }
        //拼接库存查询
        if(goods.getGoodsStock() != null){
            predicates.add(cb.equal(root.<String>get("goodsStock"),goods.getGoodsStock()));

        }

        //拼接商品所属店铺查询语句
        if(store != null && StringUtils.isNotEmpty(store.getStoreId())){
            Predicate p_store = cb.equal(root.get("store"),store);
            predicates.add(p_store);

        }

        //拼接商品分类查询语句
        List<Predicate> or_predicates_gc = new ArrayList<Predicate>();
        if(gcList != null && gcList.size() != 0){
            for(GoodsClass gc : gcList){
                Predicate p_gc_child = cb.equal(root.<String>get("goodsClass"),gc);
                or_predicates_gc.add(p_gc_child);
            }
        }
        if(or_predicates_gc.size() != 0){
            Predicate gc_predicate_or = cb.or(or_predicates_gc.toArray(new Predicate[or_predicates_gc.size()]));
            predicates.add(gc_predicate_or);
        }

        //拼接品牌查询语句
        if(brand != null && StringUtils.isNotEmpty(brand.getBrandId())){
            predicates.add(cb.equal(root.<String>get("brand"),brand));
        }

        cq.where(predicates.toArray(new Predicate[predicates.size()]));//设置where
        cq.multiselect(root.<String>get("goodsId"),root.<String>get("goodsTitle"),root.<String>get("priceStr"),root.<String>get("pics"),root.<String>get("goodsStock"));//设置select
        // 设置分页
        if(page.getDirection() == Sort.Direction.ASC){
            cq.orderBy(cb.asc(root.<String>get(page.getPageSort())));
        }else{
            cq.orderBy(cb.desc(root.<String>get(page.getPageSort())));
        }

        TypedQuery<Goods> typedQuery = em.createQuery(cq);
        List<Goods> goodsList = typedQuery.getResultList();
        int total = 0;
        if(goodsList != null){
            total = goodsList.size();
        }
        List<Goods> goodsListRes = new ArrayList<Goods>();
        int startIndex = page.getPageSize() * page.getPageNum();
        int endIndex = startIndex + page.getPageSize();
        if(goodsList.size() > 0){
            if(goodsList.size() > endIndex){
                goodsListRes = goodsList.subList(startIndex,endIndex);
            }else{
                goodsListRes = goodsList.subList(startIndex,goodsList.size());
            }
        }
        Page<Goods> goodsPage = new PageImpl<Goods>(goodsListRes,pageable,total);
        return goodsPage;

    }

    @Override
    @CacheEvict(value = "Goods",key = "#goods.goodsId",beforeInvocation = true,allEntries = true)
    public boolean deleteByGoodsId(String goodsId) {
        try{
            Goods goods = goodsRepository.findByGoodsIdAndDelFalse(goodsId);
            goodsCommentRepository.deleteByGoods(goods);//物理删除商品评论
            modifyAttrGoodsRepository.deleteByGoodsId(goodsId);//物理删除属性商品
            goodsRepository.deleteByGoodsId(goodsId);//逻辑删除商品
            return true;
        }catch (Exception e){
            return false;
        }

    }

    @Override
    @CacheEvict(value = "Goods",key = "#goodsId",beforeInvocation = true,allEntries = true)
    public boolean deleteGoods(String goodsId){
        try{
            Goods goods = goodsRepository.findByGoodsIdAndDelFalse(goodsId);
            goodsCommentRepository.deleteByGoods(goods);//物理删除商品评论
            modifyAttrGoodsRepository.deleteByGoodsId(goodsId);//物理删除属性商品
            goodsRepository.delete(goodsId);//物理删除商品
            return true;
        }catch (Exception e){
            return false;
        }

    }

    @Override
    @CacheEvict(value = "Goods",key = "#modifyGoodsId",beforeInvocation = true,allEntries = true)
    public boolean checkEditGoods(long modifyGoodsId,String result) {
        try{
            ModifyAttrGoods modifyAttrGoods = modifyAttrGoodsRepository.getOne(modifyGoodsId);
            Goods goods = modifyAttrGoods.getGoods();

            if(result.equals("success")){   //属性修改审核通过
                modifyAttrGoods.setState(StateConstant.MODIFY_GOODS_STATE_CHECK_ON.toString());

                //更新商品属性(注意只更新属性)
                Goods goodsRes = goodsRepository.findByGoodsIdAndDelFalse(goods.getGoodsId());
                goodsRes.setGoodsPic(goods.getGoodsPic());
                goods.setPrice(goodsRes.getPrice());//将价格设置成与数据库一样再更新
                goods.setSoldInTime(goodsRes.getSoldInTime());//将上架时间设置成与数据库一致
                goods.setSoldOutTime(goodsRes.getSoldOutTime());//将下架时间设置成与数据库一致
                goods.setUpdateTime(System.currentTimeMillis());
                Goods goodsSearch = goodsRepository.save(goods);
            }else{  //属性修改审核不通过
                modifyAttrGoods.setState(StateConstant.MODIFY_GOODS_STATE_CHECK_OFF.toString());
            }
            modifyAttrGoodsRepository.save(modifyAttrGoods);
            modifyAttrGoodsRepository.deleteByModifyAttrId(modifyGoodsId);//审核后逻辑删除该属性商品
            return true;
        }catch (Exception e){
            return false;
        }

    }

    @Override
    public List<Goods> findByGoodsClass(GoodsClass goodsClass) {
        return goodsRepository.findByGoodsClassAndDelFalse(goodsClass);
    }

    @Override
    public List<Goods> findByBrandAndDelFalse(Brand brand) {
        return goodsRepository.findByBrandAndDelFalse(brand);
    }

    /**
     * 获取所有失效的商品编号
     * @param time  时间
     * @return
     */
    @Override
    public List<String> findDelGoods(long time) {
        return goodsRepository.findDelGoods(time);
    }

    /**
     * 获取失效的商品
     * @return
     */
    @Override
    public List<Goods> findAllFailGoods() {
        return goodsRepository.findAll(failGoodsWhere());
    }

    /**
     * 商品失效条件
     * @return
     */
    public Specification<Goods> failGoodsWhere(){
        return new Specification<Goods>(){
            @Override
            public Predicate toPredicate(Root<Goods> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                long time = new Date().getTime();
                List<Predicate> predicates = new ArrayList<Predicate>();
                predicates.add(cb.greaterThan(root.<Long>get("soldOutTime"),time));
                predicates.add(cb.lessThan(root.<Long>get("soldInTime"),time));
                predicates.add(cb.equal(root.<Boolean>get("del"),true));
                predicates.add(cb.notEqual(root.<Boolean>get("state"),StateConstant.GOODS_STATE_CHECK_ON.toString()));
                return query.where(predicates.toArray(new Predicate[predicates.size()])).getRestriction();
            }
        };

    }
}
