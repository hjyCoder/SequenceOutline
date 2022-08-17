package com.muy.view.window.sequence;

import com.google.common.collect.Sets;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.muy.common.select.MRMultiSelect;
import com.muy.common.tree.AbstractMTTreePanel;
import com.muy.common.tree.MTTreeCell;
import com.muy.utils.JTreeUtils;
import com.muy.view.window.sequence.bean.TreeNodeModelSequence;
import com.muy.view.window.sequence.configuration.SequenceConfiguration;
import com.muy.view.window.sequence.tree.MTTreeCellSequenceEntrance;
import com.muy.view.window.sequence.tree.MTTreeCellSequenceRoot;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.List;

/**
 * @Author jiyanghuang
 * @Date 2022/8/4 20:55
 */
public class SequenceLeft extends AbstractMTTreePanel {

    public static final String SHOW_RIGHT_PANEL = "showRightPanel";

    private MRMultiSelect multiSelect;

    public SequenceLeft(Project project) {
        super(project, false, new MTTreeCellSequenceRoot(project));
        SequenceConfiguration sequenceConfiguration = SequenceConfiguration.getInstance(project);
        List<TreeNodeModelSequence> entrances = sequenceConfiguration.getEntrances();
        if(CollectionUtils.isNotEmpty(entrances)){
            for(TreeNodeModelSequence treeNodeModelSequence : entrances){
                MTTreeCellSequenceEntrance sequenceEntrance = new MTTreeCellSequenceEntrance(project, treeNodeModelSequence);
                DefaultMutableTreeNode sequenceTreeNode = new DefaultMutableTreeNode(sequenceEntrance);
                rootTreeNode.add(sequenceTreeNode);
            }
            JTreeUtils.reload(jTree(), rootTreeNode);
        }
    }

    @Override
    public boolean showRightContent() {
        return multiSelect.getItemSelects().contains(SHOW_RIGHT_PANEL);
    }

    @Override
    protected void addActions(DefaultActionGroup actionGroup) {
        multiSelect = new MRMultiSelect(Sets.newHashSet(Pair.of(SHOW_RIGHT_PANEL, (b) -> { handleSelectTree();})), "Config");
        actionGroup.add(multiSelect);
    }

    public void handleSelectTree(){
        DefaultMutableTreeNode mutableTreeNode = (DefaultMutableTreeNode) this.jTree().getLastSelectedPathComponent();
        if (null == mutableTreeNode) {
            mutableTreeNode = this.rootTreeNode();
        }
        final DefaultMutableTreeNode mutableTreeNodeT = mutableTreeNode;
        if (mutableTreeNodeT.getUserObject() instanceof MTTreeCell) {
            DumbService.getInstance(project).runWhenSmart(() -> {
                MTTreeCell mtTreeCell = (MTTreeCell) mutableTreeNodeT.getUserObject();
                mtTreeCell.treeSelectionListener(jTree(), mutableTreeNodeT, this);
            });
        }
    }
}
