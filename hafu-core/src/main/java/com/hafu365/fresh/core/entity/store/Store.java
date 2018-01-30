package com.hafu365.fresh.core.entity.store;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hafu365.fresh.core.entity.common.Image;
import com.hafu365.fresh.core.entity.member.Member;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 店铺实体类
 * Created by HuangWeizhen on 2017/7/21.
 */
@Entity
@Data
@ToString(exclude = "member")
@NoArgsConstructor
@Table(name = "fresh_store")
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler","fieldHandler"})
public class Store implements Serializable {
    /**
     * 店铺id
     */
    @Id
    @GenericGenerator(name = "sys-uid", strategy = "com.hafu365.fresh.core.utils.KeyGeneratorUtils", parameters = {
            @Parameter(name = "k", value = "S")
    })
    @GeneratedValue(generator = "sys-uid")
    private String storeId;
    /**
     * 店铺名称
     */
    private String storeName;
    /**
     * 店主
     */
    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;
    /**
     * 店员
     */
    @OneToMany
    @JoinTable(name = "fresh_store_member",joinColumns = {@JoinColumn(name = "store_id",referencedColumnName = "storeId")},
            inverseJoinColumns = {@JoinColumn(name = "member_id",referencedColumnName = "memberId")})
    private List<Member> childMember;
    /**
     * 执照编号
     */
    private String businessLicenseNo;
    /**
     * 执照图片
     */
    @Transient
    private Image businessLicense;
    @Lob
    @JsonBackReference
    private String licensePic;
    /**
     * 办公地址
     */
    @Transient
    private List<String> officeAddress;
    @JsonBackReference
    @Lob
    private String addressStr;
    /**
     * 办公电话
     */
    @Transient
    private List<String> officeTel;
    @JsonBackReference
    @Lob
    private String telStr;
    /**
     * 传真
     */
    @Transient
    private List<String> faxes;
    @JsonBackReference
    @Lob
    private String faxeStr;
    /**
     * 公司简介
     */
    @Lob
    private StringBuffer about;
    /**
     * 店铺创建时间
     */
    private long createTime;
    /**
     * 公司成立时间
     */
    private long regTime;
    /**
     * 是否删除
     */
    private boolean del = Boolean.FALSE;
    /**
     * 店铺状态
     */
    private String state;

    /**
     * 默认店铺
     */
    private boolean theDefault;

    public Store(Store store) {
        this.storeId = store.storeId;
        this.storeName = store.storeName;
        this.member = new Member(store.member);
        this.businessLicenseNo = store.businessLicenseNo;
        this.businessLicense = store.businessLicense;
        this.licensePic = store.licensePic;
        this.officeAddress = store.officeAddress;
        this.addressStr = store.addressStr;
        this.officeTel = store.officeTel;
        this.telStr = store.telStr;
        this.faxes = store.faxes;
        this.faxeStr = store.faxeStr;
        this.about = store.getAbout();
        this.createTime = store.createTime;
        this.regTime = store.regTime;
        this.del = store.del;
        this.state = store.state;
        this.theDefault = store.theDefault;
    }

    @PrePersist
    @PreUpdate
    private void save(){
        if(businessLicense != null){
            Gson gson = new Gson();
            licensePic = gson.toJson(businessLicense);
        }
        if(officeAddress != null && officeAddress.size() > 0){
            addressStr = "";
            for(String address : officeAddress){
                addressStr = addressStr + address + ",";
            }
            addressStr = addressStr.substring(0, addressStr.length() - 1);
        }

        if(officeTel != null && officeTel.size() > 0){
            telStr = "";
            for(String tel : officeTel){
                telStr = telStr + tel + ",";
            }
            telStr = telStr.substring(0, telStr.length() - 1);
        }

        if(faxes != null && faxes.size() > 0){
            faxeStr = "";
            for(String fax : faxes){
                faxeStr = faxeStr + fax + ",";
            }
            faxeStr = faxeStr.substring(0, faxeStr.length() - 1);
        }
    }
    @PostLoad
    private void load(){
        if(licensePic != null){
            Gson gson = new Gson();
            Type type = new TypeToken<Image>() {}.getType();
            businessLicense = gson.fromJson(licensePic,type);
            licensePic = null;
        }
        if(addressStr != null){
            officeAddress = new ArrayList<String>();
            if(addressStr.contains(",")){
                String[] addressArray = addressStr.split(",");
                for(String address : addressArray){
                    officeAddress.add(address);
                }
            }else{
                officeAddress.add(addressStr);
            }
            addressStr = null;
        }

        if(telStr != null){
            officeTel = new ArrayList<String>();
            if(telStr.contains(",")){
                String[] telArray = telStr.split(",");
               for(String tel : telArray){
                   officeTel.add(tel);
               }
            }else{
                officeTel.add(telStr);
            }
            telStr = null;
        }

        if(faxeStr != null){
            faxes = new ArrayList<String>();
            if(faxeStr.contains(",")){
                String[] faxArray = faxeStr.split(",");
                for(String fax : faxArray){
                    faxes.add(fax);
                }
            }else{
                faxes.add(faxeStr);
            }
            faxeStr = null;
        }
    }

    @Override
    public String toString() {
        return "Store{" +
                "storeId='" + storeId + '\'' +
                ", storeName='" + storeName + '\'' +
                ", businessLicenseNo='" + businessLicenseNo + '\'' +
                ", businessLicense=" + businessLicense +
                ", licensePic='" + licensePic + '\'' +
                ", officeAddress=" + officeAddress +
                ", addressStr='" + addressStr + '\'' +
                ", officeTel=" + officeTel +
                ", telStr='" + telStr + '\'' +
                ", faxes=" + faxes +
                ", faxeStr='" + faxeStr + '\'' +
                ", about=" + about +
                ", createTime=" + createTime +
                ", regTime=" + regTime +
                ", del=" + del +
                ", state='" + state + '\'' +
                ", theDefault=" + theDefault +
                '}';
    }
}
