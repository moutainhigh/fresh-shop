package com.hafu365.fresh.core.entity.home.bodyType;

import com.hafu365.fresh.core.entity.common.Image;
import com.hafu365.fresh.core.entity.home.FloorBody;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * 图片类型
 * Created by SunHaiyang on 2017/8/21.
 */
@ToString
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageBody implements FloorBody {
    private List<Image> images;
}
