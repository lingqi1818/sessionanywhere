package com.dumpcache.sessionanywhere;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dumpcache.sessionanywhere.config.SessionConfig;
import com.dumpcache.sessionanywhere.store.CookieSessionStore;
import com.dumpcache.sessionanywhere.store.SessionStore;

/**
 * 分布式session总入口过滤器
 * 
 * @author chenke
 * @date 2014-6-18 上午9:24:59
 */
public class SessionFilter implements Filter {
    private ServletContext            servletContext;
    private SessionConfig             config;
    private Map<String, SessionStore> storeMap            = new HashMap<String, SessionStore>();
    private SessionStoreHolder        holder;
    private Map<String, SessionStore> targetSessionStores = new HashMap<String, SessionStore>();

    public void setTargetSessionStores(Map<String, SessionStore> targetSessionStores) {
        this.targetSessionStores = targetSessionStores;
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        this.servletContext = filterConfig.getServletContext();
        String file = filterConfig.getInitParameter("configFile");
        this.config = SessionConfig.getConfig(file);
        initSessionStore();
        initSessionStoreHolder();
    }

    private void initSessionStoreHolder() {
        SessionStoreHolder holder = new SessionStoreHolder();
        holder.setDefaultStoreName(config.getDefaultStoreName());
        holder.setKeyStoreMap(config.getKeyStoreMap());
        holder.setStoreMap(storeMap);
        holder.setSessionInvalidTime(config.getSessionInvalidTime());
        this.holder = holder;
    }

    private void initSessionStore() {
        Map<String, String> map = config.getStoreClassMap();
        Set<String> keys = map.keySet();
        if (keys != null) {
            try {
                for (String key : keys) {
                    SessionStore store = targetSessionStores.get(map.get(key));
                    if (store == null) {
                        store = (SessionStore) Class.forName(map.get(key)).newInstance();
                    }
                    store.init();
                    if (store instanceof CookieSessionStore) {
                        ((CookieSessionStore) store).init(servletContext);
                    }
                    storeMap.put(key, store);

                }
            } catch (Exception ex) {
                throw new RuntimeException("init session store error:", ex);
            }
        }
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        this.servletContext.setAttribute("request", request);
        this.servletContext.setAttribute("response", response);
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpServletRequest req = new SessionRequestWrapper((HttpServletRequest) request, resp,
                servletContext, holder);
        chain.doFilter(req, resp);
        SessionImpl.localSession.remove();
    }

    public void destroy() {
        //noting
    }

}
