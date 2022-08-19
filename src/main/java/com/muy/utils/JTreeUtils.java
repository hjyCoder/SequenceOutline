package com.muy.utils;

import com.intellij.openapi.util.Key;
import com.intellij.ui.AnimatedIcon;
import com.intellij.ui.ClientProperty;
import com.intellij.ui.TreeSpeedSearch;
import com.muy.common.tree.*;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @Author jiyanghuang
 * @Date 2022/6/15 23:50
 */
public class JTreeUtils {

    /**
     * com.intellij.ui.AnimatedIcon#ANIMATION_IN_RENDERER_ALLOWED
     */
    private static final String ANIMATION_IN_RENDERER_ALLOWED_KEY = "ANIMATION_IN_RENDERER_ALLOWED";

    /**
     * com.intellij.ui.render.RenderingHelper#SHRINK_LONG_RENDERER
     */
    private static final String SHRINK_LONG_RENDERER_KEY = "SHRINK_LONG_RENDERER";

    /**
     * 展开tree视图
     *
     * @param parent treePath
     * @param expand 是否展开
     */
    public static void expandOrCollapAll(@NotNull JTree tree, @NotNull TreePath parent, boolean expand) {
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration<?> e = node.children(); e.hasMoreElements(); ) {
                TreeNode n = (TreeNode) e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                expandOrCollapAll(tree, path, expand);
            }
        }

        // 展开或收起必须自下而上进行
        if (expand) {
            tree.expandPath(parent);
        } else {
            tree.collapsePath(parent);
        }
    }

    /**
     * 配置树对象
     *
     * @param tree
     * @param treePanel
     * @param rootVisible
     */
    public static void configTree(JTree tree, JScrollPane treePanel, boolean rootVisible) {
        // tree 配置
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        model.setRoot(new DefaultMutableTreeNode());
        tree.setCellRenderer(new MTTreeCellRenderer());
        // 根节点不展示
        tree.setRootVisible(rootVisible);
        // true 或 false 决策根节点左上角折号是否展示
        tree.setShowsRootHandles(true);
        treePanel.setViewportView(tree);
        // 快速搜索
        new TreeSpeedSearch(tree);
        // 用于loading的关键配置
        Key key = FieldReflectUtils.treeLoading();
        if(null != key){
            ClientProperty.put(tree, Key.create(ANIMATION_IN_RENDERER_ALLOWED_KEY), true);
        }
//        ClientProperty.put(tree, AUTO_EXPAND_ALLOWED, false);
//        ClientProperty.put(tree, Key.create(SHRINK_LONG_RENDERER_KEY), true);

//        tree.putClientProperty(SHRINK_LONG_RENDERER, true);
    }

    /**
     * 渲染遍历树
     *
     * @param tree
     * @param rootTreeCell
     */
    public static DefaultMutableTreeNode renderRepeaterTree(JTree tree, MTTreeCell rootTreeCell) {
        if (null == rootTreeCell) {
            return null;
        }
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(rootTreeCell);
        renderRepeaterTreeSub(rootTreeCell.subTreeCell(), root);
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        model.setRoot(root);
        expandOrCollapAll(tree, new TreePath(tree.getModel().getRoot()), true);
        return root;
    }

    /**
     * 递归遍历数据结构生成树节点
     *
     * @param subTreeCells
     * @param parentNode
     */
    public static void renderRepeaterTreeSub(List<MTTreeCell> subTreeCells, DefaultMutableTreeNode parentNode) {
        if (CollectionUtils.isEmpty(subTreeCells)) {
            return;
        }
        for (MTTreeCell subTreeCell : subTreeCells) {
            DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(subTreeCell);
            parentNode.add(treeNode);
            renderRepeaterTreeSub(subTreeCell.subTreeCell(), treeNode);
        }
    }

    /**
     * @param tree
     * @param treeContext 树所属的对象，一般用于树操作时需要数据传递
     */
    public static void addTreeEvent(JTree tree, TreePanelMark treeContext) {
        // RequestTree子项点击监听
        tree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode mutableTreeNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (null == mutableTreeNode || !(mutableTreeNode.getUserObject() instanceof MTTreeCell)) {
                return;
            }
            ((MTTreeCell) mutableTreeNode.getUserObject()).treeSelectionListener(tree, mutableTreeNode, treeContext);
        });

        // RequestTree子项双击监听
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    final int doubleClick = 2;
                    if (e.getClickCount() >= doubleClick) {
                        DefaultMutableTreeNode mutableTreeNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                        if (null == mutableTreeNode || !(mutableTreeNode.getUserObject() instanceof MTTreeCell)) {
                            return;
                        }
                        ((MTTreeCell) mutableTreeNode.getUserObject()).doubleClick(tree, mutableTreeNode, treeContext);
                    }
                }
            }

            /**
             * 右键菜单
             */
            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    TreePath path = tree.getPathForLocation(e.getX(), e.getY());
                    tree.setSelectionPath(path);
                    DefaultMutableTreeNode mutableTreeNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                    if (null == mutableTreeNode || !(mutableTreeNode.getUserObject() instanceof MTTreeCell)) {
                        return;
                    }
                    ((MTTreeCell) mutableTreeNode.getUserObject()).rightClick(tree, mutableTreeNode, e, treeContext);
                }
            }
        });
        // 按回车键跳转到对应方法
        tree.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    DefaultMutableTreeNode mutableTreeNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                    if (null == mutableTreeNode || !(mutableTreeNode.getUserObject() instanceof MTTreeCell)) {
                        return;
                    }
                    ((MTTreeCell) mutableTreeNode.getUserObject()).keyEnter(tree, mutableTreeNode, treeContext);
                }
            }
        });
    }

    /**
     * @param callback
     * @param method
     * @param params
     * @param <T>
     * @return
     */
    public static <T> CompletableFuture<Void> processLoading(TreeCellEventExeContext exeContext, TreeCellEventHandle<T> eventHandle) {
        try {
            /**
             * 处理前
             */
            CompletableFuture<T> cf = new CompletableFuture<>();
            MTTreeCell mtTreeCell = (MTTreeCell) exeContext.getMutableTreeNode().getUserObject();
            mtTreeCell.loaindIconMap.put(mtTreeCell, new AnimatedIcon.Default());
            ((DefaultTreeModel) exeContext.getTree().getModel()).reload(exeContext.getMutableTreeNode());
            eventHandle.before(exeContext, cf);

            /**
             * 处理结果
             */

            eventHandle.process(exeContext, cf);

            /**
             * 方法后调用
             */
            eventHandle.after(exeContext, cf);

            /**
             * 异步或同步线程执行完后执行
             */
            CompletableFuture<Void> cfThen = cf.thenRun(() -> {
                /**
                 * 扫完后，异步操作
                 */
                eventHandle.callBack(exeContext, cf);
                EventQueue.invokeLater(() -> {
                    MTTreeCell.loaindIconMap.remove(mtTreeCell);
                    ((DefaultTreeModel) exeContext.getTree().getModel()).reload(exeContext.getMutableTreeNode());
                });
            });

            return cfThen;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            // 打印日志
        }
        return null;
    }

    /**
     * 重新加载
     *
     * @param jTree
     * @param node
     */
    public static void reload(JTree jTree, DefaultMutableTreeNode node) {
        ((DefaultTreeModel) jTree.getModel()).reload(node);
    }
}
