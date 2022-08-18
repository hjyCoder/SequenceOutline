package com.muy.service;

import com.google.common.collect.Lists;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiLambdaExpression;
import com.intellij.psi.PsiMethod;
import com.muy.utils.MethodDescUtils;
import lombok.Getter;

import java.util.List;

/**
 * @Author jiyanghuang
 * @Date 2022/8/18 21:49
 */
public class FindLambdaVisitor extends JavaRecursiveElementVisitor {

    private static final int MAX_SIZE = 100;

    @Getter
    private List<PsiLambdaExpression> findResult = Lists.newArrayList();

    /**
     * lambda 方法签名
     */
    private String methodSignature;

    /**
     * lambda 的父方法，即该 lambda 在该方法内
     */
    private PsiMethod psiMethod;

    public FindLambdaVisitor(String methodSignature, PsiMethod psiMethod) {
        this.methodSignature = methodSignature;
        this.psiMethod = psiMethod;
    }

    /**
     * 开始扫描方法
     */
    public void startVisit() {
        psiMethod.accept(this);
    }

    @Override
    public void visitLambdaExpression(PsiLambdaExpression expression) {
        String methodSign = MethodDescUtils.getMethodDescriptor(expression);
        if (methodSign.equals(methodSignature)) {
            findResult.add(expression);
        }
        if (findResult.size() <= MAX_SIZE) {
            super.visitLambdaExpression(expression);
        }
    }
}
