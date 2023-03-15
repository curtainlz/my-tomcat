package com.curtainlz.cataline.servlets;

import com.curtainlz.cataline.http.SimpleServletRequest;
import com.curtainlz.cataline.http.SimpleServletResponse;

/**
 * @author curtainlz
 * @title ImplementServlet
 * @description
 */
public class ImplementServlet extends SimpleServlet {

    @Override
    public void doGet(SimpleServletRequest request, SimpleServletResponse response) {
        System.out.println("处理GET请求");
        System.out.println("请求参数为：");
        request.getParameters().forEach((key,value) -> System.out.println(key + " ==> "+value));

        doResponse(response, "GET success");
    }

    @Override
    public void doPost(SimpleServletRequest request, SimpleServletResponse response) {
        if (request.getHeaders().get("Content-Type").contains("x-www-form-urlencoded")){
            System.out.println("处理POST Form请求");
            System.out.println("请求参数为：");
            request.getParameters().forEach((key, value) -> System.out.println(key + " ==> " + value));

            doResponse(response, "POST Form success");
        }else if (request.getHeaders().get("Content-Type").contains("application/json")){
            System.out.println("处理POST json请求");
            System.out.println("请求参数为：");
            request.getPostBody().forEach((key,value) -> System.out.println(key + " ==> " + value));

            doResponse(response, "POST json success");
        }else {
            doResponse(response, "error：暂不支持其它post请求方式");
        }
    }

}
