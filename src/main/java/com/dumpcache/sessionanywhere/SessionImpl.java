package com.dumpcache.sessionanywhere;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;

import com.dumpcache.sessionanywhere.store.SessionStore;

/**
 * HttpSession的自定义实现，用于替换servlet容器的HttpSessionImpl，实现session的扩展性
 * 
 * @author chenke
 * @date 2014-6-17 上午11:32:30
 */
public class SessionImpl implements HttpSession {

    private SessionRequestWrapper              request;
    private HttpServletResponse                response;
    private int                                maxInactiveInterval;
    private boolean                            isRequestedSessionIdValid;
    private boolean                            isSessionIdFromURL    = false;
    private boolean                            isSessionIdFromCookie = true;
    private static String                      SESSION_ID_NAME       = "cmcc_jsession_id";
    private static String                      SESSION_CREATE_TIME   = "cmcc_jsession_create_time";
    public static String                       COOKIE_PATH_VAL       = "/";
    private SessionIDMaker                     sidMaker              = new DefaultSessionIdMaker();
    public static int                          COOKIE_ALIVE_TIME     = 3600 * 24 * 365 * 100;
    private SessionInternal                    session;
    private ServletContext                     servletContext;
    private SessionStoreHolder                 holder;
    public static ThreadLocal<SessionInternal> localSession          = new ThreadLocal<SessionInternal>();

    public SessionImpl(SessionRequestWrapper request, HttpServletResponse response,
                       ServletContext servletContext, SessionStoreHolder holder) {
        this.request = request;
        this.response = response;
        this.servletContext = servletContext;
        this.holder = holder;
        init();

    }

    private void init() {
        this.session = restoreSessionState(request);
        synchronized (request) {

            if (StringUtils.isEmpty(session.getSessionId()) && !request.isSessionNew()) {
                request.setSessionNew(true);
                this.session = createNewSession();
                return;
            }

        }
        request.setSessionNew(false);
        session.setLastAccessTime(new Date().getTime());

        validateSession();
    }

    private void validateSession() {
        long createTime = 0;
        try {
            createTime = Long.valueOf(session.getCreationTime());
        } catch (Exception ex) {
            //throw new RuntimeException("parse create time error:", ex);
            createTime = 0L;
        }
        if (new Date().getTime() - createTime > holder.getSessionInvalidTime()) {
            invalidate();
        }
    }

    private SessionInternal createNewSession() {
        SessionInternal session = new SessionInternal();
        String id = sidMaker.makeNewId();
        Cookie sid = new Cookie(SESSION_ID_NAME, id);
        sid.setMaxAge(COOKIE_ALIVE_TIME);
        sid.setPath(COOKIE_PATH_VAL);
        String time = String.valueOf(new Date().getTime());
        Cookie createTime = new Cookie(SESSION_CREATE_TIME, time);
        createTime.setMaxAge(COOKIE_ALIVE_TIME);
        createTime.setPath(COOKIE_PATH_VAL);
        response.addCookie(sid);
        response.addCookie(createTime);
        session.setSessionId(id);
        session.setCreationTime(time);
        localSession.set(session);
        return session;
    }

    private SessionInternal restoreSessionState(HttpServletRequest request) {
        SessionInternal session = localSession.get();
        if (session != null) {
            return session;
        }

        session = new SessionInternal();
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (SESSION_ID_NAME.equalsIgnoreCase(cookie.getName())) {
                    session.setSessionId(cookie.getValue());
                    continue;
                }
                if (SESSION_CREATE_TIME.equalsIgnoreCase(cookie.getName())) {
                    session.setCreationTime(cookie.getValue());
                    continue;
                }

            }
        }

        return session;
    }

    public boolean isRequestedSessionIdValid() {
        return isRequestedSessionIdValid;
    }

    public void setRequestedSessionIdValid(boolean isRequestedSessionIdValid) {
        this.isRequestedSessionIdValid = isRequestedSessionIdValid;
    }

    public boolean isSessionIdFromURL() {
        return isSessionIdFromURL;
    }

    public void setSessionIdFromURL(boolean isSessionIdFromURL) {
        this.isSessionIdFromURL = isSessionIdFromURL;
    }

    public boolean isSessionIdFromCookie() {
        return isSessionIdFromCookie;
    }

    public void setSessionIdFromCookie(boolean isSessionIdFromCookie) {
        this.isSessionIdFromCookie = isSessionIdFromCookie;
    }

    public long getCreationTime() {
        if (session == null || StringUtils.isEmpty(session.getCreationTime())) {
            return 0;
        }
        return Long.valueOf(session.getCreationTime());
    }

    public String getId() {
        return session == null ? null : session.getSessionId();
    }

    public long getLastAccessedTime() {
        return session == null ? 0 : session.getLastAccessTime();
    }

    public ServletContext getServletContext() {
        return this.servletContext;
    }

    public void setMaxInactiveInterval(int interval) {
        this.maxInactiveInterval = interval;
    }

    public int getMaxInactiveInterval() {
        return this.maxInactiveInterval;
    }

    @SuppressWarnings("deprecation")
    public javax.servlet.http.HttpSessionContext getSessionContext() {
        throw new IllegalAccessError("this method can't use in our work,is deprecated.");
    }

    public Object getAttribute(String name) {
        String storeName = getStoreName(name);
        if (!isDefaultStore(storeName) && !StringUtils.isEmpty(storeName)) {
            return holder.getStoreMap().get(storeName).get(this.getId(), name);
        }
        return holder.getStoreMap().get(holder.getDefaultStoreName()).get(this.getId(), name);
    }

    private boolean isDefaultStore(String storeName) {
        return holder.getDefaultStoreName().equals(storeName);
    }

    public Object getValue(String name) {
        return getAttribute(name);
    }

    public Enumeration<String> getAttributeNames() {
        List<String> array = new ArrayList<String>();
        Collections.addAll(array, getValueNames());
        return Collections.enumeration(array);
    }

    public String[] getValueNames() {
        Set<String> set = new HashSet<String>();
        Set<String> keys = holder.getStoreMap().keySet();
        if (keys != null) {
            for (String key : keys) {
                String[] names = holder.getStoreMap().get(key).getAllKeys(this.getId());
                if (names != null) {
                    for (String name : names) {
                        set.add(name);
                    }
                }
            }
        }
        return (String[]) set.toArray();
    }

    public void setAttribute(String name, Object value) {
        String storeName = getStoreName(name);
        if (!isDefaultStore(storeName) && !StringUtils.isEmpty(storeName)) {
            holder.getStoreMap().get(storeName).put(this.getId(), name, value);
        }
        holder.getStoreMap().get(holder.getDefaultStoreName()).put(this.getId(), name, value);
    }

    public void putValue(String name, Object value) {
        setAttribute(name, value);
    }

    public void removeAttribute(String name) {
        String storeName = getStoreName(name);
        if (!isDefaultStore(storeName)) {
            SessionStore store = holder.getStoreMap().get(storeName);
            if (store != null) {
                store.delete(this.getId(), name);
            }
        }
        holder.getStoreMap().get(holder.getDefaultStoreName()).delete(this.getId(), name);
    }

    public void removeValue(String name) {
        removeAttribute(name);
    }

    public void invalidate() {
        Set<String> keys = holder.getStoreMap().keySet();
        if (keys != null) {
            for (String key : keys) {
                holder.getStoreMap().get(key).clean(this.getId());
            }
        }
        createNewSession();
    }

    public boolean isNew() {
        return request.isSessionNew();
    }

    private String getStoreName(String key) {
        return holder.getKeyStoreMap().get(key);
    }

    private static class SessionInternal {
        private String sessionId;
        private String creationTime;
        private long   lastAccessTime;

        public long getLastAccessTime() {
            return lastAccessTime;
        }

        public void setLastAccessTime(long lastAccessTime) {
            this.lastAccessTime = lastAccessTime;
        }

        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }

        public String getCreationTime() {
            return creationTime;
        }

        public void setCreationTime(String creationTime) {
            this.creationTime = creationTime;
        }

    }

}
