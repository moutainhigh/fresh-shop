package com.hafu365.fresh.core.entity.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.domain.Sort;

import java.io.Serializable;

/**
 * Created by HuangWeizhen on 2017/8/7.
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UtilPage implements Serializable{
    /**
     * 页数
     */
    private int pageNum;

    /**
     * 每页显示数量
     */
    private int pageSize;

    /**
     * 排序字段
     */
    private String pageSort;

    /**
     * 排序方向
     */
    private Sort.Direction direction;

}
