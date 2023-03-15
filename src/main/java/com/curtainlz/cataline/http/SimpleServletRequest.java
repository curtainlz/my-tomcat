package com.curtainlz.cataline.http;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author curtainlz
 * @title SimpleServletRequest
 * @description
 */
public class SimpleServletRequest {

    private ChannelHandlerContext ctx;
    private HttpRequest request;

    public SimpleServletRequest(ChannelHandlerContext ctx, HttpRequest request) {
        this.ctx = ctx;
        this.request = request;
    }

    public String getUrl() {
        return request.uri();
    }

    public String getMethod() {
        String name = request.method().name();
        return name;
    }

    public HttpHeaders getHeaders() {
        return request.headers();
    }

    public Map<String, List<String>> getParameters() {
        QueryStringDecoder decoders = new QueryStringDecoder(request.uri());
        return decoders.parameters();
    }

    public Map<String, String> getPostParameters() {
        Map<String, String> parameters = new HashMap<>();

        HttpPostRequestDecoder decoders = new HttpPostRequestDecoder(request);
        List<InterfaceHttpData> interfaceHttpDataS = decoders.getBodyHttpDatas();
        for (InterfaceHttpData interfaceHttpData : interfaceHttpDataS) {
            if (interfaceHttpData.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                Attribute attribute = (Attribute) interfaceHttpData;
                try {
                    String key = attribute.getName();
                    String value = attribute.getValue();

                    parameters.put(key, value);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return parameters;
    }

    public Map<String, Object> getPostBody() {
        ByteBuf content = ((FullHttpRequest) request).content();
        byte[] bytes = new byte[content.readableBytes()];
        content.readBytes(bytes);

        return JSON.parseObject(new String(bytes)).getInnerMap();
    }
}
