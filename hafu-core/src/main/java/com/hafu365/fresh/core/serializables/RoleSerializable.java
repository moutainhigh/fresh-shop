package com.hafu365.fresh.core.serializables;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.hafu365.fresh.core.entity.member.Role;
import com.hafu365.fresh.core.entity.member.SimpleRole;

import java.io.IOException;

/**
 * Created by zhaihuilin on 2017/10/23  13:24.
 */
public class RoleSerializable extends JsonSerializer<Role> {
    @Override
    public void serialize(Role value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        SimpleRole simpleRole=new SimpleRole(value);
        gen.writeObject(simpleRole);
    }
}
