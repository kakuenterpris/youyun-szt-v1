package com.ustack.chat.config;

import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.nio.charset.Charset;

public class CharsetTypeAdapter extends TypeAdapter<Charset> {

    // 新建Charset适配器
    @Override
    public void write(JsonWriter out, Charset value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        out.value(value.name());
    }

    @Override
    public Charset read(JsonReader in) throws IOException {
        String charsetName = in.nextString();
        try {
            return Charset.forName(charsetName);
        } catch (IllegalArgumentException e) {
            throw new JsonSyntaxException("Invalid charset: " + charsetName, e);
        }
    }

}
