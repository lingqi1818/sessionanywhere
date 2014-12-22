package com.dumpcache.sessionanywhere.store;

/**
 * session存储接口，不同的实现可以将session数据持久化到任意地方
 * 
 * @author chenke
 * @date 2014-6-17 上午11:19:40
 */
public interface SessionStore {
    /**
     * 初始化store
     */
    public void init();

    /**
     * put数据到store中
     * 
     * @param sessionId
     * @param key
     * @param value
     */
    public void put(String sessionId, String key, Object value);

    /**
     * 从store中根据key获取数据
     * 
     * @param sessionId
     * @param key
     * @return
     */
    public Object get(String sessionId, String key);

    /**
     * 获取所有的key列表
     * 
     * @param sessionId
     * @return
     */
    public String[] getAllKeys(String sessionId);

    /**
     * 删除指定key的内容
     * 
     * @param sessionId
     * @param key
     */
    public void delete(String sessionId, String key);

    /**
     * 清理store
     * 
     * @param sessionId
     */
    public void clean(String sessionId);

}
