package com.muy.utils;

import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.function.*;

/**
 * @Author jiyanghuang
 * @Date 2022/2/19 10:15 PM
 */
public class TreeUtils {

    /**
     * 更新所有子树属性值
     * [注意]如果在遍历时修改属性，可能导致最后一个没能遍历时，不被修改到，所以弹出时修改，后续遍历
     */
    public static <T> void changeFieldValue(T t, Function<T, List<T>> subFun, Consumer<T>... consumers) {
        Stack<MutablePair<T, Integer>> stack = new Stack<MutablePair<T, Integer>>();
        stack.push(MutablePair.of(t, 0));
        while (!stack.isEmpty()) {
            MutablePair<T, Integer> ele = stack.peek();
            if (CollectionUtils.isEmpty(subFun.apply(ele.getLeft()))) {
                changeValue(ele.getLeft(), consumers);
                stack.pop();
            } else {
                if (ele.getRight() < subFun.apply(ele.getLeft()).size()) {
                    T p = ele.getLeft();
                    T s = subFun.apply(ele.getLeft()).get(ele.getRight());
                    ele.setRight(ele.getRight() + 1);
                    stack.push(MutablePair.of(s, 0));
                } else {
                    changeValue(ele.getLeft(), consumers);
                    stack.pop();
                }
            }
        }
    }

    private static <T> void changeValue(T t, Consumer<T>... consumers) {
        for (Consumer<T> consumer : consumers) {
            consumer.accept(t);
        }
    }

    /**
     * 按前序遍历
     * 找到第一个符合条件的树节点
     *
     * @param t
     * @param subFun
     * @param predicate
     * @param <T>
     * @return
     */
    public static <T> T preorderTraversalMatchFirst(T t, Function<T, List<T>> subFun, Predicate<T> predicate) {
        if (predicate.test(t)) {
            return t;
        }
        Stack<MutablePair<T, Integer>> stack = new Stack<MutablePair<T, Integer>>();
        stack.push(MutablePair.of(t, 0));
        while (!stack.isEmpty()) {
            MutablePair<T, Integer> ele = stack.peek();
            if (CollectionUtils.isEmpty(subFun.apply(ele.getLeft()))) {
                stack.pop();
            } else {
                if (ele.getRight() < subFun.apply(ele.getLeft()).size()) {
                    T p = ele.getLeft();
                    T s = subFun.apply(ele.getLeft()).get(ele.getRight());
                    if (predicate.test(s)) {
                        return s;
                    }
                    ele.setRight(ele.getRight() + 1);
                    stack.push(MutablePair.of(s, 0));
                } else {
                    stack.pop();
                }
            }
        }
        return null;
    }

    /**
     * 总结：如果一个函数不关心有没有返回值，就使用 Consumer,否则如果使用函数Function就必须有返回值
     *
     * @param t          原树型结构
     * @param convertFun 转换函数
     * @param listFun    获取子List函数
     * @param addFun     添加到父List函数
     * @param <T>        原树对象
     * @param <R>        新转换对象
     * @return
     */
    public static <T, R> Pair<T, R> treeConvert(T t, Function<T, R> convertFun, Function<T, List<T>> listFun, BiConsumer<R, R> addFun) {
        Queue<Pair<T, R>> queue = new LinkedList<>();
        Pair<T, R> root = Pair.of(t, convertFun.apply(t));
        queue.offer(root);
        while (!queue.isEmpty()) {
            Pair<T, R> elePoll = queue.poll();
            if (CollectionUtils.isNotEmpty(listFun.apply(elePoll.getLeft()))) {
                for (T subT : listFun.apply(elePoll.getLeft())) {
                    R subR = convertFun.apply(subT);
                    addFun.accept(elePoll.getRight(), subR);
                    queue.offer(Pair.of(subT, subR));
                }
            }
        }
        return root;
    }

    /**
     * 总结：如果一个函数不关心有没有返回值，就使用 Consumer,否则如果使用函数Function就必须有返回值
     * 按是否展示过滤节点
     *
     * @param t          原树型结构
     * @param convertFun 转换函数
     * @param subFun     获取子List函数
     * @param addFun     添加到父List函数
     * @param showFun    是否展示该节点
     * @param <T>        待转换对象类型
     * @param <R>        新生成对象类型
     * @return
     */
    public static <T, R> R treeConvertOnlyShow(T t, Function<T, R> convertFun, Function<T, List<T>> subFun, BiConsumer<R, R> addFun, Predicate<T> showFun) {
        Stack<Pair<R, MutablePair<T, Integer>>> stack = new Stack<Pair<R, MutablePair<T, Integer>>>();
        R r = convertFun.apply(t);
        Pair<R, MutablePair<T, Integer>> rootPair = Pair.of(r, MutablePair.of(t, 0));
        stack.push(rootPair);
        while (!stack.isEmpty()) {
            Pair<R, MutablePair<T, Integer>> elePoll = stack.peek();
            MutablePair<T, Integer> tPair = elePoll.getRight();
            List<T> sub = subFun.apply(elePoll.getRight().getLeft());
            if (CollectionUtils.isEmpty(sub)) {
                stack.pop();
            } else {
                if (tPair.getRight() < sub.size()) {
                    T s = sub.get(tPair.getRight());
                    T find = TreeUtils.preorderTraversalMatchFirst(s, subFun, showFun);
                    if (null != find) {
                        R rFind = convertFun.apply(find);
                        Pair<R, MutablePair<T, Integer>> findPair = Pair.of(rFind, MutablePair.of(find, 0));
                        addFun.accept(elePoll.getLeft(), rFind);
                        stack.push(findPair);
                    }
                    tPair.setRight(tPair.getRight() + 1);
                } else {
                    stack.pop();
                }
            }
        }
        return rootPair.getLeft();
    }

    /**
     * 删除树中指定节点
     *
     * @param currentNode
     * @param parentNode
     * @param subFun
     * @param <T>
     */
    public static <T> void removeCurrentNode(T currentNode, T parentNode, Function<T, List<T>> subFun) {
        if (null == parentNode || null == currentNode || null == subFun) {
            return;
        }
        List<T> subs = subFun.apply(currentNode);
        if (CollectionUtils.isNotEmpty(subs)) {
            List<T> parentSubs = Lists.newArrayList(subFun.apply(parentNode));
            List<T> parentSubsRef = subFun.apply(parentNode);
            parentSubsRef.clear();
            for (T parentSub : parentSubs) {
                if (parentSub.equals(currentNode)) {
                    parentSubsRef.addAll(subs);
                    continue;
                }
                parentSubsRef.add(parentSub);
            }
        } else {
            List<T> parentSubs = subFun.apply(parentNode);
            if (CollectionUtils.isNotEmpty(parentSubs)) {
                parentSubs.removeIf(s -> s.equals(currentNode));
            }
        }
    }
}
