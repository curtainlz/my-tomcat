package com.curtainlz.cataline.handler;

import com.curtainlz.cataline.http.SimpleServletRequest;
import com.curtainlz.cataline.http.SimpleServletResponse;
import com.curtainlz.cataline.servlets.ServletMapping;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

import java.nio.charset.StandardCharsets;

/**
 * @author curtainlz
 * @title CustomServerHandler
 * @description 自定义处理器
 */
public class CustomServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
        String uri = request.uri();
        String path = uri;
        if (uri.contains("?")) {
            path = uri.substring(0, uri.indexOf("?"));
        }

        if (ServletMapping.getUrlServletMapping().containsKey(path)) {
            SimpleServletRequest servletRequest = new SimpleServletRequest(ctx, request);
            SimpleServletResponse servletResponse = new SimpleServletResponse(ctx, request);

            ServletMapping.getUrlServletMapping().get(path).service(servletRequest, servletResponse);
        } else {
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            response.content().writeCharSequence("404 NOT FOUND：" + path + "不存在", StandardCharsets.UTF_8);

            ctx.channel().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }
    }
}
