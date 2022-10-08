package com.muy.common.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.muy.common.notification.SequenceOutlineNotifier;
import com.muy.utils.GoToSourceUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import static com.intellij.openapi.actionSystem.CommonDataKeys.EDITOR;

/**
 * @Author jiyanghuang
 * @Date 2022/10/8 20:15
 */
public class MRGotoLineAction extends AnAction {

    public static final String ACTION_TEXT = "go to line";


    public MRGotoLineAction() {
        super(ACTION_TEXT, ACTION_TEXT, AllIcons.Actions.ShowReadAccess);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        DataContext dataContext = e.getDataContext();
        Editor editor = EDITOR.getData(dataContext);
        if (null == editor) {
            return;
        }

        String text = editor.getSelectionModel().getSelectedText();
        if (StringUtils.isBlank(text)) {
            SequenceOutlineNotifier.notifyError("select text is blank");
            return;
        }
        Pair<String, String> classNameLine = findLineNumClassName(text);
        if (null == classNameLine) {
            SequenceOutlineNotifier.notifyError("invalid line String");
            return;
        }

        GoToSourceUtils.openLineInEditor(e.getProject(), classNameLine.getLeft(), Integer.valueOf(classNameLine.getRight()));
    }

    public static Pair<String, String> findLineNumClassName(String line) {
        line = line.trim();
        if (line.startsWith("at ")) {
            line = line.substring("at ".length());
        } else if (line.startsWith("@")) {
            line = line.substring("@".length());
        } else {
            return null;
        }

        int methodNameSplit = line.indexOf("(");
        String simpleClassNameAndLineNum = line.substring(methodNameSplit);
        String[] clineNum = simpleClassNameAndLineNum.split(":");
        if (clineNum.length != 2) {
            return null;
        }
        int lineNum = Integer.valueOf(clineNum[1].substring(0, clineNum[1].length() - 1));
        // 类名与方法名
        String clMethod = line.substring(0, methodNameSplit);
        int clMethodSplit = clMethod.lastIndexOf(".");
        String className = clMethod.substring(0, clMethodSplit);
        return Pair.of(className, String.valueOf(lineNum - 1));
    }
}
