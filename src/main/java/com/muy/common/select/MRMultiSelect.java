package com.muy.common.select;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.util.NlsActions;
import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @Author jiyanghuang
 * @Date 2022/3/16 10:51 PM
 */
public class MRMultiSelect extends DefaultActionGroup{

    @Getter
    private Set<String> itemSelects = new HashSet<>();

    public MRMultiSelect(Set<Pair<String, Consumer<Boolean>>> allItems, String shortName){
        super(shortName, true);
        getTemplatePresentation().setIcon(AllIcons.General.Settings);
        getTemplatePresentation().setText(shortName);
        getTemplatePresentation().setDescription(shortName);
        for(Pair<String, Consumer<Boolean>> item : allItems){
            add(new SelectToggleAction(item.getLeft(), item.getLeft(), item.getRight()));
        }
    }

    public void update(Set<Pair<String, Consumer<Boolean>>> allItems){
        removeAll();
        for(Pair<String, Consumer<Boolean>> item : allItems){
            if("None".equals(item.getLeft())){
                continue;
            }
            add(new SelectToggleAction(item.getLeft(), item.getLeft(), item.getRight()));
        }
    }

    public Set<String> itemSelects(){
        return itemSelects;
    }

    private class SelectToggleAction extends ToggleAction {

        private String itemName;

        private Consumer<Boolean> consumer;

        public SelectToggleAction(String itemName, @NlsActions.ActionText String message, Consumer<Boolean> consumer){
            super(message);
            this.itemName = itemName;
            this.consumer = consumer;
        }
        @Override
        public boolean isSelected(@NotNull AnActionEvent e) {
            return itemSelects.contains(itemName);
        }

        @Override
        public void setSelected(@NotNull AnActionEvent e, boolean state) {
            if (state) {
                itemSelects.add(itemName);
            }
            else {
                itemSelects.remove(itemName);
            }
            if(null != consumer){
                consumer.accept(state);
            }
        }
    }
}
