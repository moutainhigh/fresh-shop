package com.hafu365.fresh.core.entity.member;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.Gson;
import com.hafu365.fresh.core.entity.common.Image;
import com.hafu365.fresh.core.entity.constant.StateConstant;
import com.hafu365.fresh.core.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;

/**
 * Created by HuangXueheng on 2017/9/4.
 * 用户信息实体类
 */
@Entity
@ToString
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "fresh_member_info")
@JsonIgnoreProperties({"portraitStr","organizationCodePicStr","businessLicensePicStr"})
public class MemberInfo implements Serializable {

    /**
     * 用户配置信息id
     */
    @Id
    @GenericGenerator(name = "sys-uid", strategy = "com.hafu365.fresh.core.utils.KeyGeneratorUtils", parameters = {
            @org.hibernate.annotations.Parameter(name = "k", value = "MO")
    })
    @GeneratedValue(generator = "sys-uid")
    private String id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户头像
     */
    @Transient
    private Image portrait;

    /**
     * 营业执照编号
     */
    private String businessLicenseSN;

    /**
     * 营业执照图片
     */
    @Transient
    private Image businessLicensePic;

    /**
     * 营业执照图片文本
     */
    private String businessLicensePicStr;

    /**
     * 组织机构代码
     */
    private String organizationCode;

    /**
     * 组织机构代码证图片
     */
    @Transient
    private Image organizationCodePic;

    /**
     * 组织机构代码证图片文本
     */
    private String organizationCodePicStr;

    /**
     * 办公室地址
     */
    private String officeAddress;

    /**
     * 用户头像储存字符串
     */
    private String portraitStr;

    /**
     * 用户办公室电话
     */
    private String officeTel;

    /**
     * 用户住址
     */
    private String apartment;

    /**
     * 用户家庭电话
     */
    private String homePhone;

    /**
     * 出生日期
     */
    private long birthdate;

    /**
     * 出生地址
     */
    private String birthplace;

    /**
     * 个人简介
     */
    private String userDetail;

    /**
     * 学历
     */
    private String education;

    /**
     * 毕业学校
     */
    private String graduatedFrom;

    /**
     * 工作单位
     */
    private String workUnit;

    /**
     * 血型
     */
    private String blood;

    /**
     * 创建时间
     */
    private long createTime;

    /**
     * 更新时间
     */
    private long updateTime;

    /**
     * 用户状态
     */
    private String state = StateConstant.USER_STATE_CHECK_ING.toString();

    public void setState(StateConstant state) {
        this.state = state.toString();
    }

    public void setState(String state) {
        this.state = state;
    }

    /**
     * 读入数据后
     */
    @PostLoad
    private void load(){
        Gson gson = new Gson();
        if (StringUtils.isNotEmpty(this.portraitStr)){
            this.portrait = gson.fromJson(this.portraitStr,Image.class);
        }
        if(StringUtils.isNotEmpty(this.businessLicensePicStr)){
            this.businessLicensePic = gson.fromJson(this.businessLicensePicStr,Image.class);
        }
        if(StringUtils.isNotEmpty(this.organizationCodePicStr)){
            this.organizationCodePic = gson.fromJson(this.organizationCodePicStr,Image.class);
        }
    }

    /**
     * 写入数据前
     */
    @PreUpdate
    @PrePersist
    private void save(){
        Gson gson = new Gson();
        if (this.portrait != null){
            this.portraitStr = gson.toJson(this.portrait);
        }
        if(this.businessLicensePic != null){
            this.businessLicensePicStr = gson.toJson(this.businessLicensePic);
        }
        if(this.organizationCodePic != null){
            this.organizationCodePicStr = gson.toJson(this.organizationCodePic);
        }
    }

    /**
     * 对应的用户
     */
    @OneToOne(fetch = FetchType.EAGER,cascade = {})
    @JoinColumn(name = "member_id")
    private Member member;
}
