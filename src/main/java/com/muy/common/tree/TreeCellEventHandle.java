package com.muy.common.tree;

import java.util.concurrent.CompletableFuture;

/**
 * @Author jiyanghuang
 * @Date 2022/7/17 19:56
 */
public interface TreeCellEventHandle<T> {

    /**
     * 处理前
     */
    public default void before(TreeCellEventExeContext exeContext, CompletableFuture<T> cf) {

    }

    /**
     * @param tree
     * @param mutableTreeNode
     * @throws Exception
     */
    public void process(TreeCellEventExeContext exeContext, CompletableFuture<T> cf) throws Exception;


    /**
     * @param tree
     * @param mutableTreeNode
     * @throws Exception
     */
    public default void after(TreeCellEventExeContext exeContext, CompletableFuture<T> cf) {

    }

    /**
     * 处理后
     */
    public default void callBack(TreeCellEventExeContext exeContext, CompletableFuture<T> cf) {

    }

}
