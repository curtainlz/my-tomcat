package com.curtainlz.cataline.servlets;

import com.curtainlz.cataline.http.SimpleServletRequest;
import com.curtainlz.cataline.http.SimpleServletResponse;

/**
 * @author curtainlz
 * @title SimpleServlet
 * @description 实现简单servlet
 */
public abstract class SimpleServlet {

    public void service(SimpleServletRequest request, SimpleServletResponse response) {
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            System.out.printf("获取到请求地址：%s,请求参数：%s%n", request.getUrl(), request.getParameters());
            doGet(request, response);
        } else if ("POST".equalsIgnoreCase(request.getMethod())) {
            System.out.printf("获取到请求地址：%s,请求参数：%s%n", request.getUrl(), request.getParameters());
            doPost(request, response);
        } else {
            doResponse(response, "暂不支持其它请求方法");
        }
    }

    public abstract void doGet(SimpleServletRequest request, SimpleServletResponse response);

    public abstract void doPost(SimpleServletRequest request, SimpleServletResponse response);

    public void doResponse(SimpleServletResponse response, String message) {
        response.write(message);
    }
}
