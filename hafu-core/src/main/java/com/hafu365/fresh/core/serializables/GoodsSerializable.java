package com.hafu365.fresh.core.serializables;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.hafu365.fresh.core.entity.goods.Goods;
import com.hafu365.fresh.core.entity.goods.SimpleGoods;

import java.io.IOException;

/**
 * json
 * Created by zhaihuilin on 2017/10/23  9:18.
 */
public class GoodsSerializable extends JsonSerializer<Goods> {
    @Override
    public void serialize(Goods value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        SimpleGoods simpleGoods = new SimpleGoods(value);
        gen.writeObject(simpleGoods);
    }
}
