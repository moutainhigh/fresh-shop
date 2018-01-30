package com.hafu365.fresh.core.entity.goods;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 商品库存实体类
 * Created by HuangWeizhen on 2017/8/22.
 */
@Entity
@Data
@ToString
@NoArgsConstructor
@Table(name = "fresh_goods_stock")
public class GoodsStock implements Serializable{
    /**
     * 库存id
     */
    @Id
    @GenericGenerator(name = "sys-uid", strategy = "com.hafu365.fresh.core.utils.KeyGeneratorUtils", parameters = {
            @Parameter(name = "k", value = "GS")
    })
    @GeneratedValue(generator = "sys-uid")
    private String stockId;

    /**
     * 库存单位
     */
    private String sku;

    /**
     * 库存数量
     */
    private long stockNum;

}
