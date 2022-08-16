package com.muy.utils;

import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * @Author jiyanghuang
 * @Date 2022/8/16 21:31
 */
public class MRListUtils {

    public static <T> void swrap(List<T> data, int indexA, int indexB) {
        if (indexA == indexB) {
            return;
        }
        if (CollectionUtils.isEmpty(data)) {
            return;
        }

        if (checkIndexInvalid(indexA, data.size()) || checkIndexInvalid(indexB, data.size())) {
            return;
        }

        T t = data.get(indexA);
        data.set(indexA, data.get(indexB));
        data.set(indexB, t);
    }

    private static boolean checkIndexInvalid(int index, int size) {
        if (index < 0 || index >= size) {
            return true;
        }
        return false;
    }
}
