package com.hafu365.fresh.core.serializables;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.hafu365.fresh.core.entity.store.Store;
import com.hafu365.fresh.core.entity.store.StoreVo;

import java.io.IOException;

/**
 * 在获取值的时候 对 store 做序列化处理
 * Created by zhaihuilin on 2017/10/23  9:25.
 */
public class StoreSerializable extends JsonSerializer<Store> {
    @Override
    public void serialize(Store value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        StoreVo  storeVo=new StoreVo(value);
        gen.writeObject(storeVo);
    }
}
