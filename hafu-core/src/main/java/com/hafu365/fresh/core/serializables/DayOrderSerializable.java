package com.hafu365.fresh.core.serializables;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.hafu365.fresh.core.entity.order.DayOrder;
import com.hafu365.fresh.core.entity.order.SimpleDayOrder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaihuilin on 2017/10/24  15:01.
 */
public class DayOrderSerializable extends JsonSerializer<List<DayOrder>> {
    @Override
    public void serialize(List<DayOrder> value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        List<SimpleDayOrder> simpleDayOrderList = new ArrayList<SimpleDayOrder>();
        if (value !=null && value.size()>0){
              for (DayOrder dayOrder :value){
                  SimpleDayOrder simpleDayOrder=new SimpleDayOrder(dayOrder);
                  simpleDayOrderList.add(simpleDayOrder);
              }
        }
        gen.writeObject(simpleDayOrderList);
    }
}
