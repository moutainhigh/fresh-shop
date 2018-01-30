package com.hafu365.fresh.core.entity.home.bodyType;

import com.hafu365.fresh.core.entity.common.Image;
import com.hafu365.fresh.core.entity.goods.Goods;
import com.hafu365.fresh.core.entity.home.FloorBody;
import lombok.*;

import java.util.List;

/**
 * 商品内容
 * Created by SunHaiyang on 2017/8/21.
 */
@ToString
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsBody implements FloorBody {

    /**
     * 图片
     */
    private Image image;

    /**
     * 商品
     */
    private List<Goods> goods;

}
