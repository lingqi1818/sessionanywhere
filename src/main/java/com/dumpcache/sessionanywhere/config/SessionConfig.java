package com.dumpcache.sessionanywhere.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

/**
 * session的配置文件
 * 
 * @author chenke
 * @date 2014-6-17 上午10:14:51
 */
public class SessionConfig {
    private Properties           prop                 = new Properties();
    private static SessionConfig config;
    private static String        SESSION_STORE_PREFIX = "session.store.";
    private static String        SESSION_KEY_PREFIX   = "session.key.";
    private static String        CONFIG_SPLIT_SYMBOL  = "\\.";
    private String               defaultStoreName;
    private Map<String, String>  storeClassMap        = new HashMap<String, String>();
    private Map<String, String>  keyStoreMap          = new HashMap<String, String>();

    private int                  sessionInvalidTime   = 0;

    private SessionConfig(String file) {
        if (StringUtils.isEmpty(file)) {
            throw new IllegalArgumentException("the config file is null.");
        }
        loadPropFile(file);
        loadConfigFromFile();
    }

    private void loadConfigFromFile() {
        Enumeration<?> keys = prop.propertyNames();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            if (key.startsWith(SESSION_STORE_PREFIX)) {
                String strs[] = key.split(CONFIG_SPLIT_SYMBOL);
                if (strs.length == 3) {
                    if ("default".equalsIgnoreCase(strs[2])) {
                        defaultStoreName = prop.getProperty(key);
                        continue;
                    } else {
                        storeClassMap.put(strs[2], prop.getProperty(key));
                        continue;
                    }
                }
            }

            if (key.startsWith(SESSION_KEY_PREFIX)) {
                String strs[] = key.split(CONFIG_SPLIT_SYMBOL);
                if (strs.length == 4 && "store".equalsIgnoreCase(strs[3])) {
                    keyStoreMap.put(strs[2], prop.getProperty(key));
                }
            }

            if ("session.invalid.time".equalsIgnoreCase(key)) {
                this.sessionInvalidTime = Integer.valueOf(prop.getProperty(key));
            }
        }
    }

    private void loadPropFile(String file) {
        try {
            if (file.startsWith("classpath:")) {
                prop.load(Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream(file.substring(10)));
                return;
            }
            prop.load(new FileInputStream(file));
        } catch (IOException e) {
            throw new RuntimeException("load config failed ,file is:" + file, e);
        }
    }

    public static synchronized SessionConfig getConfig(String file) {
        if (config == null) {
            config = new SessionConfig(file);
        }
        return config;
    }

    /**
     * 获取所有的store名称和类名的map
     * 
     * @return
     */
    public Map<String, String> getStoreClassMap() {
        return storeClassMap;

    }

    /**
     * 获取默认store名称
     * 
     * @return
     */
    public String getDefaultStoreName() {
        return defaultStoreName;

    }

    /**
     * 获取自定义key和store的map
     * 
     * @return
     */
    public Map<String, String> getKeyStoreMap() {
        return keyStoreMap;

    }

    public int getSessionInvalidTime() {
        return sessionInvalidTime;
    }

    public static void main(String args[]) {
        SessionConfig config = SessionConfig
                .getConfig("/Users/chenke/Documents/javawork/open-read/easy-restful/src/main/java/org/codeanywhere/easyRestful/base/session/session.properties");
        System.out.println(config.getDefaultStoreName());
        System.out.println(config.getStoreClassMap());
        System.out.println(config.getKeyStoreMap());
    }

}
