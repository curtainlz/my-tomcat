package com.curtainlz.cataline.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

import java.nio.charset.StandardCharsets;

/**
 * @author curtainlz
 * @title SimpleServletResponse
 * @description
 */
public class SimpleServletResponse {

    private ChannelHandlerContext ctx;
    private HttpRequest request;

    public SimpleServletResponse(ChannelHandlerContext ctx, HttpRequest request) {
        this.ctx = ctx;
        this.request = request;
    }

    public void write(String message) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.wrappedBuffer(message.getBytes(StandardCharsets.UTF_8))
        );
        response.headers()
                .set("Content-Type","text/html")
                .set("charset",StandardCharsets.UTF_8.name());

        ctx.channel().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
