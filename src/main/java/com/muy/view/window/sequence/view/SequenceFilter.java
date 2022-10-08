package com.muy.view.window.sequence.view;

import com.google.common.collect.Sets;
import com.intellij.ui.components.JBScrollPane;
import com.muy.service.filters.config.FilterConfig;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @Author jiyanghuang
 * @Date 2022/8/5 00:52
 */
public class SequenceFilter extends JPanel {

    private JCheckBox notGet;
    private JTextField tfAllowGet;
    private JCheckBox notSet;
    private JCheckBox notPrivate;
    private JCheckBox notConstructor;
    private JTextField tfAllowSet;
    private JTextField tfAllowPrivate;
    private JTextField tfAllowConstructor;
    private JTextField tfMethodNames;
    private JTextField tfClassNames;
    private JTextField tfPackageNames;
    private JTextField tfExClassName;
    private JTextField tfExPackageNames;
    private JTextField tfParentClassNames;
    private JTextField tfImplClassNames;
    private JTextField tfTreeDepth;
    private JPanel rootPanel;
    private JCheckBox notLambda;
    private JCheckBox notExternal;

    private FilterConfig filterConfig;

    public SequenceFilter(FilterConfig filterConfig) {
        setLayout(new BorderLayout());
        add(new JBScrollPane(rootPanel), BorderLayout.CENTER);
        this.filterConfig = filterConfig;
        fillContent(filterConfig);
    }

    public void saveConfig() {
        fetchConfig(filterConfig);
    }

    public void fetchConfig(FilterConfig filterConfig){
        filterConfig.setNotGet(notGet.isSelected());
        filterConfig.setNotSet(notSet.isSelected());
        filterConfig.setNotPrivateMethod(notPrivate.isSelected());
        filterConfig.setNotConstructor(notConstructor.isSelected());
        filterConfig.setNotLambda(notLambda.isSelected());
        filterConfig.setNotLambda(notExternal.isSelected());
        fillTextToSet(tfAllowGet.getText(), filterConfig::setAllowGet);
        fillTextToSet(tfAllowSet.getText(), filterConfig::setAllowSet);
        fillTextToSet(tfAllowPrivate.getText(), filterConfig::setAllowPrivateMethod);
        fillTextToSet(tfAllowConstructor.getText(), filterConfig::setAllowConstructor);
        fillTextToSet(tfMethodNames.getText(), filterConfig::setMethodNamePattern);
        fillTextToSet(tfClassNames.getText(), filterConfig::setClassNamePattern);
        fillTextToSet(tfPackageNames.getText(), filterConfig::setPackageNamePattern);
        fillTextToSet(tfExClassName.getText(), filterConfig::setExClassNamePattern);
        fillTextToSet(tfExPackageNames.getText(), filterConfig::setExPackageNamePattern);
        fillTextToSet(tfParentClassNames.getText(), filterConfig::setParentClassNamePattern);
        fillTextToSet(tfImplClassNames.getText(), filterConfig::setImplementClassSet);
        if (StringUtils.isNumeric(tfTreeDepth.getText())) {
            filterConfig.setCallDepth(Integer.valueOf(tfTreeDepth.getText()));
        } else {
            filterConfig.setCallDepth(5);
        }
    }

    public void fillContent(){
        fillContent(filterConfig);
    }

    public void fillContent(FilterConfig filterConfig){
        fillCheck(filterConfig::getNotGet, notGet);
        fillCheck(filterConfig::getNotSet, notSet);
        fillCheck(filterConfig::getNotPrivateMethod, notPrivate);
        fillCheck(filterConfig::getNotConstructor, notConstructor);
        fillCheck(filterConfig::getNotLambda, notLambda);
        if(null == filterConfig.getNotExternal()){
            filterConfig.setNotExternal(true);
        }
        fillCheck(filterConfig::getNotExternal, notExternal);
        fillSetToText(filterConfig::getAllowGet, tfAllowGet);
        fillSetToText(filterConfig::getAllowSet, tfAllowSet);
        fillSetToText(filterConfig::getAllowPrivateMethod, tfAllowPrivate);
        fillSetToText(filterConfig::getAllowConstructor, tfAllowConstructor);
        fillSetToText(filterConfig::getMethodNamePattern, tfMethodNames);
        fillSetToText(filterConfig::getClassNamePattern, tfClassNames);
        fillSetToText(filterConfig::getPackageNamePattern, tfPackageNames);
        fillSetToText(filterConfig::getExClassNamePattern, tfExClassName);
        fillSetToText(filterConfig::getExPackageNamePattern, tfExPackageNames);
        fillSetToText(filterConfig::getParentClassNamePattern, tfParentClassNames);
        fillSetToText(filterConfig::getImplementClassSet, tfImplClassNames);
        if(null != filterConfig.getCallDepth()){
            tfTreeDepth.setText(String.valueOf(filterConfig.getCallDepth()));
        }
    }

    public void fillSetToText(Supplier<Set<String>> supplier, JTextField tf){
        Set<String> content = supplier.get();
        if(null == content){
            tf.setText("");
            return;
        }
        tf.setText(String.join(",", content));
    }

    public void fillCheck(Supplier<Boolean> supplier, JCheckBox checkBox){
        Boolean su = supplier.get();
        if(null == su || !su){
            checkBox.setSelected(false);
            return;
        }
        checkBox.setSelected(true);
    }

    /**
     * 填充内容
     *
     * @param text
     * @param consumer
     */
    public void fillTextToSet(String text, Consumer<Set<String>> consumer) {
        if (StringUtils.isBlank(text)) {
            consumer.accept(Sets.newHashSet());
            return;
        }
        consumer.accept(Sets.newHashSet(text.split(",")));
    }
}
