package com.dumpcache.sessionanywhere;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SessionRequestWrapper extends HttpServletRequestWrapper {

    private SessionImpl         session;
    private HttpServletResponse response;
    private ServletContext      servletContext;
    private SessionStoreHolder  storeHolder;
    private boolean             isSessionNew;

    public boolean isSessionNew() {
        return isSessionNew;
    }

    public void setSessionNew(boolean isSessionNew) {
        this.isSessionNew = isSessionNew;
    }

    public SessionRequestWrapper(HttpServletRequest request, HttpServletResponse response,
                                 ServletContext servletContext, SessionStoreHolder storeHolder) {
        super(request);
        this.response = response;
        this.servletContext = servletContext;
        this.storeHolder = storeHolder;
    }

    /**
     * The default behavior of this method is to return getRequestedSessionId()
     * on the wrapped request object.
     */
    public String getRequestedSessionId() {
        if (session == null) {
            return null;
        }
        return session.getId();
    }

    /**
     * The default behavior of this method is to return getSession(boolean
     * create) on the wrapped request object.
     */
    public HttpSession getSession(boolean create) {
        return getSession();
    }

    /**
     * The default behavior of this method is to return getSession() on the
     * wrapped request object.
     */
    public HttpSession getSession() {
        return new SessionImpl(this, response, servletContext, this.storeHolder);
    }

    /**
     * The default behavior of this method is to return
     * isRequestedSessionIdValid() on the wrapped request object.
     */

    public boolean isRequestedSessionIdValid() {
        return session.isRequestedSessionIdValid();
    }

    /**
     * The default behavior of this method is to return
     * isRequestedSessionIdFromCookie() on the wrapped request object.
     */
    public boolean isRequestedSessionIdFromCookie() {
        return session.isSessionIdFromCookie();
    }

    /**
     * The default behavior of this method is to return
     * isRequestedSessionIdFromURL() on the wrapped request object.
     */
    public boolean isRequestedSessionIdFromURL() {
        return session.isSessionIdFromURL();
    }

    /**
     * The default behavior of this method is to return
     * isRequestedSessionIdFromUrl() on the wrapped request object.
     */
    public boolean isRequestedSessionIdFromUrl() {
        return isRequestedSessionIdFromURL();
    }

}
