package com.muy.view.window.sequence.bean;

import com.muy.domain.bean.invoketree.TreeInvokeModel;
import com.muy.domain.bean.invoketree.TreeNodeModel;
import com.muy.service.filters.config.FilterConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author jiyanghuang
 * @Date 2022/8/5 00:44
 */
@EqualsAndHashCode(callSuper=false)
@Data
public class TreeNodeModelSequence extends TreeNodeModel {

    /**
     * 用于决定遍历到哪些方法
     * 如果不配置则可能遍历所有的方法，耗时严重
     */
    private FilterConfig filterConfig;

    /**
     * 用于决定哪些需要展示
     */
    private FilterConfig filterConfigShow;

    /**
     * 调用树的根
     */
    private TreeInvokeModel root;

    public TreeNodeModelSequence clone() {
        try {
            return (TreeNodeModelSequence) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public TreeNodeModelSequence copyConfig(){
        TreeNodeModelSequence treeNodeModelSequence = new TreeNodeModelSequence();
        treeNodeModelSequence.setFilterConfig(filterConfig);
        treeNodeModelSequence.setFilterConfigShow(filterConfigShow);
        return treeNodeModelSequence;
    }

    public static boolean configValid(TreeNodeModelSequence jsonConvert) {
        if (null == jsonConvert) {
            return false;
        }
        if (null == jsonConvert.getFilterConfig() || null == jsonConvert.getFilterConfigShow()) {
            return false;
        }
        return true;
    }
}
