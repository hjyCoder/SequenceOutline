package com.muy.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.function.Function;

/**
 * @Author jiyanghuang
 * @Date 2022/8/8 22:58
 */
public class MapStack<T> extends Stack<T> {

    private Function<T, String> keyFun;

    private Map<String, T> cacheMap;

    public MapStack(Function<T, String> keyFun) {
        this.keyFun = keyFun;
        cacheMap = new HashMap<>();
    }

    @Override
    public T push(T item) {
        T r = super.push(item);
        String key = keyFun.apply(item);
        cacheMap.put(key, r);
        return r;
    }

    @Override
    public synchronized T pop() {
        T r = super.pop();
        cacheMap.remove(keyFun.apply(r));
        return r;
    }

    public boolean containsItem(T item) {
        return cacheMap.containsKey(keyFun.apply(item));
    }

}
