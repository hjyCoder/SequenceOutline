package com.muy.utils;

import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.ClassUtil;
import com.intellij.util.concurrency.NonUrgentExecutor;

/**
 * @Author jiyanghuang
 * @Date 2022/2/26 2:09 PM
 */
public class GoToSourceUtils {

    public static final String CONSTRUCTORS_METHOD_NAME = "<init>";

    public static void openClassInEditor(Project project, final String className) {
        ReadAction
                .nonBlocking(() -> {
                    final PsiClass psiClass = ClassUtil.findPsiClass(psiManager(project), className);
                    if (psiClass == null) return null;

                    VirtualFile virtualFile = SequenceOutlinePsiUtils.findVirtualFile(psiClass);
                    final int offset = SequenceOutlinePsiUtils.findNaviOffset(psiClass);

                    return new Pair<>(virtualFile, offset);
                })
                .finishOnUiThread(ModalityState.defaultModalityState(), p -> {
                    if (p != null)
                        openInEditor(project, p.first, p.second);
                })
                .inSmartMode(project)
                .submit(NonUrgentExecutor.getInstance());

    }

    /**
     *
     * @param project
     * @param fClassName 全类名
     * @param methodName 方法名，如果是构建函数，则是类名
     * @param methodSign 方法签名
     */
    public static void openMethodInEditor(Project project, String fClassName, String methodName, String methodSign) {

        ReadAction
                .nonBlocking(() -> {
                    final PsiMethod psiMethod = SequenceOutlinePsiUtils.findPsiMethodSign(psiManager(project), fClassName, methodName, methodSign);
                    if (psiMethod == null) return null;

                    final PsiClass containingClass = psiMethod.getContainingClass();
                    if (containingClass == null) return null;

                    VirtualFile virtualFile = SequenceOutlinePsiUtils.findVirtualFile(containingClass);

                    final int offset = SequenceOutlinePsiUtils.findNaviOffset(psiMethod);

                    return new Pair<>(virtualFile, offset);

                })
                .finishOnUiThread(ModalityState.defaultModalityState(), p -> {
                    if (p != null)
                        openInEditor(project, p.first, p.second);
                })
                .inSmartMode(project)
                .submit(NonUrgentExecutor.getInstance());
    }

    protected static void openInEditor(Project project, VirtualFile virtualFile, int offset) {
        if (virtualFile == null)
            return;

        FileEditorManager.getInstance(project).openTextEditor(new OpenFileDescriptor(project,
                virtualFile, offset), true);
    }

    public static PsiManager psiManager(Project project){
        return PsiManager.getInstance(project);
    }
}
