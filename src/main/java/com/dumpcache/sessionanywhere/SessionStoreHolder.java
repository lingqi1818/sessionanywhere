package com.dumpcache.sessionanywhere;

import java.util.Map;

import com.dumpcache.sessionanywhere.store.SessionStore;

public class SessionStoreHolder {

    private Map<String, SessionStore> storeMap;
    private String                    defaultStoreName;
    private Map<String, String>       keyStoreMap;
    private int                       sessionInvalidTime;

    public int getSessionInvalidTime() {
        return sessionInvalidTime;
    }

    public void setSessionInvalidTime(int sessionInvalidTime) {
        this.sessionInvalidTime = sessionInvalidTime;
    }

    public Map<String, SessionStore> getStoreMap() {
        return storeMap;
    }

    public void setStoreMap(Map<String, SessionStore> storeMap) {
        this.storeMap = storeMap;
    }

    public String getDefaultStoreName() {
        return defaultStoreName;
    }

    public void setDefaultStoreName(String defaultStoreName) {
        this.defaultStoreName = defaultStoreName;
    }

    public Map<String, String> getKeyStoreMap() {
        return keyStoreMap;
    }

    public void setKeyStoreMap(Map<String, String> keyStoreMap) {
        this.keyStoreMap = keyStoreMap;
    }

}
