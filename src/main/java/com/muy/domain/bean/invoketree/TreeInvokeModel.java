package com.muy.domain.bean.invoketree;

import com.google.common.collect.Lists;
import lombok.Data;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * @Author jiyanghuang
 * @Date 2022/8/7 13:50
 */
@Data
public class TreeInvokeModel implements Serializable {

    private Long id;

    private Date gmtCreate;

    private Date gmtModified;

    private String appName;

    private String uriMd5;

    private Long parentId = 0L;

    private Integer orderIndex;

    /**
     * 当前调用上下文，用于关联调用树，不用保存
     */
    private String invokeTreeTraceId;

    /**
     * 该值只有要节点时生成，在保存时才会传到子节点
     */
    private String treeMd5;

    /**
     * 子调用
     */
    private List<TreeInvokeModel> subInvoke = Lists.newArrayList();

    /**
     *
     */
    private TreeNodeModel treeNodeModel;

    /**
     * 表示该节点所在树的层数
     * 用于根据 row,col 及 treeId 找到出参与入参
     */
    private Integer row;

    /**
     * 表示该节点所在树的列数
     * 注意这里只表示列数，并不代表构建出树后
     * 从左到右，从 0 开始的顺序，因为在多线程场景下，顺序并不一定是从左到右
     * 但是可以 row,col 可以唯一表示一个节点
     */
    private Integer col;

    /**
     * 采用层次遍历，标志(层数，index)MD5#(层数，index)MD5
     * 最终得到字符串生成 md5
     *
     * @return
     */
    public String treeMd5Gen() {
        Queue<Pair<TreeInvokeModel, Pair<Integer, Integer>>> treeQueue = new LinkedList<>();
        treeQueue.add(Pair.of(this, Pair.of(0, 0)));
        StringBuilder sb = new StringBuilder();
        while (!treeQueue.isEmpty()) {
            Pair<TreeInvokeModel, Pair<Integer, Integer>> pair = treeQueue.poll();
            // [row,col] 行与列表示元素的位置,最终形式是[row,col]uri_md5
            sb.append("[" + pair.getRight().getLeft() + "," + pair.getRight().getRight() + "]");
            sb.append(pair.getLeft().getUriMd5());
            if (null != pair.getLeft().getSubInvoke() && pair.getLeft().getSubInvoke().size() > 0) {
                Integer currentRow = pair.getRight().getLeft() + 1;
                for (int i = 0; i < pair.getLeft().getSubInvoke().size(); i++) {
                    TreeInvokeModel sub = pair.getLeft().getSubInvoke().get(i);
                    treeQueue.add(Pair.of(sub, Pair.of(currentRow, i)));
                }
            }
        }
        treeMd5 = DigestUtils.md5Hex(sb.toString());
        return treeMd5;
    }

    public static TreeInvokeModel of(TreeNodeModel treeNodeModel) {
        TreeInvokeModel treeInvokeModel = new TreeInvokeModel();
        treeInvokeModel.setTreeNodeModel(treeNodeModel);
        treeInvokeModel.setUriMd5(treeNodeModel.getUriMd5());
        return treeInvokeModel;
    }

    public String className(){
        return treeNodeModel.getClassName();
    }

    public String fClassName(){
        return treeNodeModel.getPackageName() + "." + treeNodeModel.getClassName();
    }

    public String reValue(){
        return "";
    }

    public String nodeValue(){
        return treeNodeModel.getMethodName();
    }

    /**
     * 用于生成时序图时，虚拟和入口调用
     *
     * @return
     */
    public static TreeInvokeModel ofRoot() {
        TreeInvokeModel treeInvokeModel = new TreeInvokeModel();

        TreeNodeModel treeNodeModel = new TreeNodeModel();
        treeNodeModel.setClassName("RootEntrance");
        treeNodeModel.setMethodName("");

        treeInvokeModel.setTreeNodeModel(treeNodeModel);
        return treeInvokeModel;
    }
}
