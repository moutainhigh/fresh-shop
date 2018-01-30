package com.hafu365.fresh.core.serializables;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.hafu365.fresh.core.entity.member.Member;
import com.hafu365.fresh.core.entity.member.SimpleMember;

import java.io.IOException;

/**
 * Created by zhaihuilin on 2017/10/23  13:24.
 */
public class MemberSerializable extends JsonSerializer<Member> {

    @Override
    public void serialize(Member value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        SimpleMember simpleMember =new SimpleMember(value);
        gen.writeObject(simpleMember);
    }
}
