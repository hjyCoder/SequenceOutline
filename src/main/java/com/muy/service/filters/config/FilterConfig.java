package com.muy.service.filters.config;

import lombok.Data;

import java.util.Set;

/**
 * @Author jiyanghuang
 * @Date 2022/7/3 23:47
 */
@Data
public class FilterConfig {

    private Boolean notGet;

    private Set<String> allowGet;

    private Boolean notSet;

    private Set<String> allowSet;

    private Boolean notConstructor;

    private Set<String> allowConstructor;

    private Boolean notPrivateMethod;

    private Set<String> allowPrivateMethod;

    private Boolean notLambda;

    /**
     * 方法名正则表达式
     */
    private Set<String> methodNamePattern;

    /**
     * 类名正则表达式
     */
    private Set<String> classNamePattern;

    /**
     * 包名正则表达式
     */
    private Set<String> packageNamePattern;

    /**
     * 排除哪些类不展示
     */
    private Set<String> exClassNamePattern;

    /**
     * 排除哪些类不展示
     */
    private Set<String> exPackageNamePattern;

    /**
     * 允许某些类的子类
     */
    private Set<String> parentClassNamePattern;

    /**
     * 当遍历到抽象类时允许遍历哪些实现类
     */
    private Set<String> implementClassSet;

    /**
     * 调用层次
     */
    private Integer callDepth;


    public static FilterConfig ofDefault(FilterConfig current) {
        if (null == current) {
            return new FilterConfig();
        }
        return current;
    }

    /**
     *
     * @param config 复制的值
     * @param filterConfig 需要更新的对象
     */
    public static void updateField(FilterConfig config, FilterConfig filterConfig){
        filterConfig.setNotGet(config.getNotGet());
        filterConfig.setAllowGet(config.getAllowGet());
        filterConfig.setNotSet(config.getNotSet());
        filterConfig.setAllowSet(config.getAllowSet());
        filterConfig.setNotConstructor(config.getNotConstructor());
        filterConfig.setAllowConstructor(config.getAllowConstructor());
        filterConfig.setNotPrivateMethod(config.getNotPrivateMethod());
        filterConfig.setAllowPrivateMethod(config.getAllowPrivateMethod());
        filterConfig.setMethodNamePattern(config.getMethodNamePattern());
        filterConfig.setClassNamePattern(config.getClassNamePattern());
        filterConfig.setPackageNamePattern(config.getPackageNamePattern());
        filterConfig.setExClassNamePattern(config.getExClassNamePattern());
        filterConfig.setExPackageNamePattern(config.getExPackageNamePattern());
        filterConfig.setParentClassNamePattern(config.getParentClassNamePattern());
        filterConfig.setImplementClassSet(config.getImplementClassSet());
        filterConfig.setCallDepth(config.getCallDepth());
        filterConfig.setNotLambda(config.getNotLambda());
    }

}
