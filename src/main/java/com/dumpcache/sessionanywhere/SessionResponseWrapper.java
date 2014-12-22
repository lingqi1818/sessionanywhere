package com.dumpcache.sessionanywhere;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class SessionResponseWrapper extends HttpServletResponseWrapper {
    private HttpServletResponse response;

    public SessionResponseWrapper(HttpServletResponse response) {
        super(response);
        this.response = response;
    }

    public void sendRedirect(String location) throws IOException {
        response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
        response.setHeader("Location", location);
    }

}
