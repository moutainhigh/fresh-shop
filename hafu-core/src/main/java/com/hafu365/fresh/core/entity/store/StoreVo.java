package com.hafu365.fresh.core.entity.store;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by zhaihuilin on 2017/10/20  10:35.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoreVo implements Serializable {

    private String storeId;
    private String storeName;
    private String username;
    private String memberId;

    public StoreVo(Store store) {
        this.storeId = store.getStoreId();
        this.storeName = store.getStoreName();
        this.username = store.getMember().getUsername();
        this.memberId = store.getMember().getMemberId();
    }
}
