package com.dumpcache.sessionanywhere.store;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.dumpcache.sessionanywhere.SessionImpl;

public class CookieSessionStore implements SessionStore {
    private ServletContext servletContext;

    public void init() {
        
    }

    private HttpServletRequest getRequest() {
        return (HttpServletRequest) this.servletContext.getAttribute("request");

    }

    private HttpServletResponse getResponse() {
        return (HttpServletResponse) this.servletContext.getAttribute("response");
    }

    public void init(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public void put(String sessionId, String key, Object value) {
        String str = null;
        str = JSON.toJSONString(value);
        Cookie cookie = new Cookie(key, value.getClass().getName() + "#" + str);
        cookie.setMaxAge(SessionImpl.COOKIE_ALIVE_TIME);
        cookie.setPath(SessionImpl.COOKIE_PATH_VAL);
        getResponse().addCookie(cookie);
    }

    public Object get(String sessionId, String key) {
        Cookie[] cookies = getRequest().getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(key)) {
                    String value = cookie.getValue();
                    String strs[] = value.split("#");
                    try {
                        Class<?> clzz = Class.forName(strs[0]);
                        return JSON.parseObject(value.substring(strs[0].length() + 1), clzz);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException("reseriable object failed:", e);
                    }
                }
            }
        }
        return null;
    }

    public String[] getAllKeys(String sessionId) {
        List<String> list = new ArrayList<String>();
        Cookie[] cookies = getRequest().getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                list.add(cookie.getName());
            }
        }
        return (String[]) list.toArray();
    }

    public void clean(String sessionId) {
        Cookie[] cookies = getRequest().getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                cookie.setMaxAge(0);
                getResponse().addCookie(cookie);
            }
        }
    }

    public void delete(String sessionId, String key) {
        Cookie[] cookies = getRequest().getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(key)) {
                    cookie.setMaxAge(0);
                    getResponse().addCookie(cookie);
                }
            }
        }
    }

}
