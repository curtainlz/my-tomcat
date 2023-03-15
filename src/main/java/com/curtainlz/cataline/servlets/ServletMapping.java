package com.curtainlz.cataline.servlets;

import java.util.HashMap;
import java.util.Map;

/**
 * @author curtainlz
 * @title ServletMapping
 * @description url与对应的ImplementServlet映射
 */
public class ServletMapping {

    private static final Map<String, ImplementServlet> urlServletMapping = new HashMap<>();

    public static Map<String, ImplementServlet> getUrlServletMapping() {
        return urlServletMapping;
    }
}
