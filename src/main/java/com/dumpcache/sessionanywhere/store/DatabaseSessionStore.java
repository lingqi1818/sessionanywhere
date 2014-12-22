package com.dumpcache.sessionanywhere.store;
//
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//
//import org.apache.commons.lang.StringUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.alibaba.fastjson.JSON;
//import com.cmcc.hz.headquarter.session.store.SessionStore;
//import com.cmcc.normandy.dal.dao.CacheKvDAO;
//import com.cmcc.normandy.dal.dao.CacheKvDAO.CacheKvKeys;
//import com.cmcc.normandy.dal.dataobject.update.CacheKvDO;
//
//public class DatabaseSessionStore implements SessionStore {
//    Logger                log              = LoggerFactory.getLogger(DatabaseSessionStore.class);
//
//    private CacheKvDAO    cacheKvDAO;
//
//    private static int    CACHE_IDENTI     = 5;
//    private static String KEY_SPLIT_SYMBOL = "#";
//
//    public void setCacheKvDAO(CacheKvDAO cacheKvDAO) {
//        this.cacheKvDAO = cacheKvDAO;
//    }
//
//    public void init() {
//        log.info("DatabaseSessionStore init success !");
//    }
//
//    public void put(String sessionId, String key, Object value) {
//
//        CacheKvDO cache = new CacheKvDO();
//        cache.setCreateTime(new Date());
//        cache.setIdenti(CACHE_IDENTI);
//        cache.setUpdateTime(new Date());
//        cache.setKeyName(key);
//        cache.setSflag(sessionId);
//        String str = JSON.toJSONString(value);
//        cache.setVal(value.getClass().getName() + KEY_SPLIT_SYMBOL + str);
//        cacheKvDAO.saveCacheKv(cache);
//
//    }
//
//    public Object get(String sessionId, String key) {
//        Map<CacheKvKeys, Object> param = new HashMap<CacheKvKeys, Object>();
//        param.put(CacheKvKeys.identi, CACHE_IDENTI);
//        param.put(CacheKvKeys.keyName, key);
//        param.put(CacheKvKeys.sflag, sessionId);
//        CacheKvDO cache = cacheKvDAO.findCacheKvByIdentiAndKeyName(param);
//        if (cache != null && !StringUtils.isEmpty(cache.getVal())) {
//            String[] strs = cache.getVal().split(KEY_SPLIT_SYMBOL);
//            Class<?> clzz;
//            try {
//                clzz = Class.forName(strs[0]);
//            } catch (ClassNotFoundException e) {
//                throw new RuntimeException("reseriable object failed:", e);
//
//            }
//            return JSON.parseObject(cache.getVal().substring(strs[0].length() + 1), clzz);
//        }
//        return null;
//    }
//
//    public String[] getAllKeys(String sessionId) {
//        throw new RuntimeException("unsupport this method !");
//    }
//
//    public void delete(String sessionId, String key) {
//        Map<CacheKvKeys, Object> param = new HashMap<CacheKvKeys, Object>();
//        param.put(CacheKvKeys.identi, CACHE_IDENTI);
//        param.put(CacheKvKeys.keyName, key);
//        param.put(CacheKvKeys.sflag, sessionId);
//        cacheKvDAO.deleteCacheKv(param);
//    }
//
//    public void clean(String sessionId) {
//        Map<CacheKvKeys, Object> param = new HashMap<CacheKvKeys, Object>();
//        param.put(CacheKvKeys.identi, CACHE_IDENTI);
//        param.put(CacheKvKeys.sflag, sessionId);
//        cacheKvDAO.deleteCacheKv(param);
//    }
//
//}
