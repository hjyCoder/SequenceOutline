package com.muy.view.component;

import com.intellij.openapi.project.Project;
import com.muy.common.tree.TreePanelMark;

import javax.swing.*;
import java.awt.*;

/**
 * @Author jiyanghuang
 * @Date 2022/7/17 21:17
 */
public class TabContentRightShow<T extends TreePanelMark> extends JPanel {

    private final Project project;

    private JComponent currentShow = null;

    private T t;
    public TabContentRightShow(Project project){
        this.project = project;
        setLayout(new BorderLayout());
    }

    public void fillLeft(T t){
        this.t = t;
    }

    /**
     * 实践表示
     * 1.validate() 和 repaint() 是可以生效的，但是 validate() 会有部分组件重叠现象，而 repaint() 不是立马执行，而是在组件大小被调节时才会更新
     * 2.updateUI() 效果会更好一些
     * @param panel
     */
    public void updatePanel(JComponent panel){
        if(null != t && !t.showRightContent()){
            setVisible(false);
            updateUI();
            return;
        }
        setVisible(true);
        if(null != currentShow){
            remove(currentShow);
        }
        currentShow = panel;
        add(currentShow, BorderLayout.CENTER);
        updateUI();
    }
}
