package com.lib.base.util;

import android.util.LruCache;

import com.google.gson.annotations.SerializedName;
import com.greendao.db.util.GsonUtil;
import com.lib.base.util.encrypt.base.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 双缓存工具类(内存LruCache+sp),可以设置缓存时长
 * <p>
 * PackageName  com.lib.base.util
 * ProjectName  TempleteProject-java
 * Date         2023/2/28.
 *
 * @author dave
 */

public class DoubleCacheUtil {
    public static final int MAX_CACHE_SIZE = 20 * 1024 * 1024;//LruCache最大内存缓存大小20MB
    public static LruCache<String, CacheBean> mMemoryCache;

    static {
        mMemoryCache = new LruCache<String, CacheBean>(MAX_CACHE_SIZE) {
            @Override
            protected int sizeOf(String key, CacheBean value) {
                if (value == null) {
                    return 1;
                }
                String json = value.content /*GsonUtil.toJson(value)*/;
                return !TextUtils.isEmpty(json) ? json.getBytes().length : 1;
            }
        };
    }

    /**
     * 保存无缓存数据
     *
     * @param key
     * @param obj
     * @return true success/false fail
     */
    public static boolean put(String key, Object obj) {
        return put(key, obj, 0);
    }

    /**
     * 保存缓存数据
     *
     * @param key
     * @param obj
     * @param cacheTimeSeconds 秒
     * @return
     */
    public static boolean put(String key, Object obj, long cacheTimeSeconds) {
        if (TextUtils.isEmpty(key) || obj == null) {
            return false;
        }
        CacheBean bean = new CacheBean(obj, cacheTimeSeconds);
        String content = GsonUtil.toJson(bean);
        if (TextUtils.isEmpty(content)) {
            return false;
        }
        // 剔除data属性,只保留content,缓存时间,超时时间
        try {
            JSONObject jsonObject = new JSONObject(content);
            if (jsonObject.has("data")) {
                jsonObject.remove("data");
            }
            content = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mMemoryCache.put(key, bean);
        SPUtil.put(key, content);
        return true;
    }

    /**
     * 获取未超时数据,包括基本数据和结构对象数据:
     * 1.基本类型数据最好使用包装类接收进行判空操作;
     * 2.保存的list和map数据使用下面方法获取.
     *
     * @param key
     * @param <T>
     * @return
     */
    public static <T> T get(String key, Class<T> clazz) {
        try {
            if (TextUtils.isEmpty(key) || clazz == null) {
                return null;
            }
            CacheBean cacheBean = mMemoryCache.get(key);
            if (cacheBean != null) {
                if (cacheBean.cacheTimeSeconds > 0) {
                    long outTime = cacheBean.timeWhenCache + cacheBean.cacheTimeSeconds * 1000;
                    long timeNow = System.currentTimeMillis();
                    if (outTime < timeNow) {
                        mMemoryCache.remove(key);
                        SPUtil.put(key, "");
                        return null;
                    } else {
                        return get(clazz, cacheBean);
                    }
                } else {
                    return get(clazz, cacheBean);
                }
            } else {
                String string = SPUtil.getString(key);
                if (TextUtils.isEmpty(string)) {
                    return null;
                }
                CacheBean bean = GsonUtil.fromJsonObj(string, CacheBean.class);
                if (bean == null) {
                    return null;
                }
                if (bean.cacheTimeSeconds > 0) {
                    long outTime = bean.timeWhenCache + bean.cacheTimeSeconds * 1000;
                    long timeNow = System.currentTimeMillis();
                    if (outTime < timeNow) {
                        mMemoryCache.remove(key);
                        SPUtil.put(key, "");
                        return null;
                    } else {
                        return get(key, clazz, bean);
                    }
                } else {
                    return get(key, clazz, bean);
                }
            }
        } catch (Exception e) {
            return null;
        }
    }

    private static <T> T get(String key, Class<T> clazz, @NonNull CacheBean bean) {
        T t = String.class.equals(clazz) ? (T) bean.content : GsonUtil.fromJsonObj(bean.content, clazz);
        bean.data = t;
        mMemoryCache.put(key, bean);
        return t;
    }

    @Nullable
    private static <T> T get(Class<T> clazz, @NonNull CacheBean cacheBean) {
        Object data = cacheBean.data;
        if (data != null) {
            if (data.getClass().equals(clazz)) {
                return (T) data;
            } else {
                // 处理一些特殊情况,比如浮点默认double,如果要用float接收需要重新解析一下
                return !TextUtils.isEmpty(cacheBean.content) ? GsonUtil.fromJsonObj(cacheBean.content, clazz) : null;
            }
        } else {
            return null;
        }
    }

    public static <T> T get(String key, Type type) {
        try {
            if (TextUtils.isEmpty(key) || type == null) return null;

            CacheBean cacheBean = mMemoryCache.get(key);

            if (cacheBean == null) {
                String string = SPUtil.getString(key);
                if (TextUtils.isEmpty(string)) return null;

                cacheBean = GsonUtil.fromJsonObj(string, CacheBean.class);
                if (cacheBean == null) return null;
            }

            // 过期判断统一
            if (cacheBean.cacheTimeSeconds > 0) {
                long outTime = cacheBean.timeWhenCache + cacheBean.cacheTimeSeconds * 1000;
                if (System.currentTimeMillis() > outTime) {
                    mMemoryCache.remove(key);
                    SPUtil.put(key, "");
                    return null;
                }
            }

            Object result = GsonUtil.fromJson(cacheBean.content, type);

            cacheBean.data = result;
            mMemoryCache.put(key, cacheBean);

            return (T) result;

        } catch (Exception e) {
            return null;
        }
    }

    public static class CacheBean {

        @SerializedName("timeWhenCache")
        public long timeWhenCache;
        @SerializedName("cacheTimeSeconds")
        public long cacheTimeSeconds;
        @SerializedName("content")
        public String content;
        @SerializedName("data")
        public Object data;

        private CacheBean() {
        }

        public CacheBean(Object data, long cacheTimeSeconds) {
            this.data = data;
            this.content = data instanceof String ? (String) data : GsonUtil.toJson(data);
            this.cacheTimeSeconds = cacheTimeSeconds > 0 ? cacheTimeSeconds : -1;
            this.timeWhenCache = cacheTimeSeconds > 0 ? System.currentTimeMillis() : -1;
        }

        public CacheBean(Object data) {
            this.data = data;
            this.content = data instanceof String ? (String) data : GsonUtil.toJson(data);
            this.cacheTimeSeconds = -1;
            this.timeWhenCache = -1;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CacheBean cacheBean = (CacheBean) o;
            return Objects.equals(content, cacheBean.content);
        }

        @Override
        public int hashCode() {
            return Objects.hash(content);
        }
    }
}
