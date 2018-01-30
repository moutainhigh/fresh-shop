package com.hafu365.fresh.core.entity.setting;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 基本设置类
 * Created by zhaihuilin on 2017/9/23  10:58.
 */
@Entity
@Data
@ToString
@NoArgsConstructor
@Table(name = "fresh_basicSetup")
public class BasicSetup implements Serializable {

    /**
     * 编号
     */
    @Id
    @GenericGenerator(name = "sys-uid",strategy = "com.hafu365.fresh.core.utils.KeyGeneratorUtils",parameters = {
            @Parameter(name = "k",value = "BS")
    })
    @GeneratedValue(generator = "sys-uid")
     private  String id;

    /**
     * 键
     */
     private  String keyNames;

    /**
     * 值
     */
    private  String keyCode;







}
