package com.muy.utils;

import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 为了简单起见，全部展示短名，如果需要查看全部名称可以查库或其他方式获取
 */
@Data
public class PlantUmlSequenceCreator<T> {

    /**
     * {"唯一名称":"简单名称"}
     */
    private Map<String, String> uSNameMap = new LinkedHashMap<>();

    /**
     * 如果 2 表示 简单名称 有重复，唯一名称重复时，序列为 2，1 表示没有重复
     * {"简单名称":{"唯一名称":2}}
     */
    private Map<String, Map<String, Integer>> suRepeatNumMap = new HashMap<>();

    /**
     * 生成时序图
     */
    private StringBuilder linkSb = new StringBuilder();

    /**
     * 树型对象
     */
    private T t;

    /**
     * 返回子对象函数
     */
    private Function<T, List<T>> subFun;

    /**
     * 返回唯一名称函数
     */
    private Function<T, String> upNameFun;

    /**
     * 返回简单名称函数
     */
    private Function<T, String> spNameFun;

    /**
     * 返回节点解释名称函数
     */
    private Function<T, String> nodeNameFun;

    /**
     * 返回值描述函数
     */
    private Function<T, String> reValueFun;

    /**
     * 判断是否展示函数
     */
    private Predicate<T> showFun;

    public PlantUmlSequenceCreator() {

    }

    /**
     *
     * @param t  转换对象
     * @param subFun 获取子节点的方法
     * @param upNameFun 唯一名称函数
     * @param spNameFun 简单名称函数
     * @param nodeNameFun 节点函数名称
     * @param reValueFun
     */
    public PlantUmlSequenceCreator(T t, Function<T, List<T>> subFun, Function<T, String> upNameFun, Function<T, String> spNameFun, Function<T, String> nodeNameFun, Function<T, String> reValueFun) {
        this.t = t;
        this.subFun = subFun;
        this.upNameFun = upNameFun;
        this.spNameFun = spNameFun;
        this.nodeNameFun = nodeNameFun;
        this.reValueFun = reValueFun;
    }

    /**
     *
     * @param t
     * @param subFun
     * @param upNameFun
     * @param spNameFun
     * @param nodeNameFun
     * @param reValueFun 显示的返回值
     * @param predicate 是否要显示的判断
     */
    public PlantUmlSequenceCreator(T t, Function<T, List<T>> subFun, Function<T, String> upNameFun, Function<T, String> spNameFun, Function<T, String> nodeNameFun, Function<T, String> reValueFun, Predicate<T> predicate) {
        this.t = t;
        this.subFun = subFun;
        this.upNameFun = upNameFun;
        this.spNameFun = spNameFun;
        this.nodeNameFun = nodeNameFun;
        this.reValueFun = reValueFun;
        this.showFun = predicate;
    }

    /**
     * 生成时序字符串
     */
    public void callLink() {
        Stack<MutablePair<T, Integer>> stack = new Stack<MutablePair<T, Integer>>();
        stack.push(MutablePair.of(t, 0));
        addParticipantName(upNameFun.apply(t), spNameFun.apply(t));
        while (!stack.isEmpty()) {
            MutablePair<T, Integer> ele = stack.peek();
            if (CollectionUtils.isEmpty(subFun.apply(ele.getLeft()))) {
                stack.pop();
                linkSb.append("return").append(" " + nodeNameFun.apply(ele.getLeft())).append("\n");
            } else {
                if (ele.getRight() < subFun.apply(ele.getLeft()).size()) {
                    T p = ele.getLeft();
                    T s = subFun.apply(ele.getLeft()).get(ele.getRight());
                    addParticipantName(upNameFun.apply(s), spNameFun.apply(s));
                    linkSb.append(participantName(upNameFun.apply(p), spNameFun.apply(p))).append("->").append(participantName(upNameFun.apply(s), spNameFun.apply(s))).append("++:").append(nodeNameFun.apply(s)).append("\n");
                    ele.setRight(ele.getRight() + 1);
                    stack.push(MutablePair.of(s, 0));
                } else {
                    stack.pop();
                    if (stack.size() > 0) {
                        linkSb.append("return").append(" " + reValueFun.apply(ele.getLeft())).append("\n");
                    }
                }
            }
        }
    }

    /**
     * 生成时序字符串
     * 这里暂时根一定展示
     */
    public void callLinkSkip() {
        // 如果根节点展示，则直接使用根节点
        if (showFun.test(t)) {
            callLinkSkip(t);
            return;
        }
        // 遍历子节点
        List<T> subs = subFun.apply(t);
        for (int i = 0; i < subs.size(); i++) {
            T curSub = subs.get(i);
            T find = TreeUtils.preorderTraversalMatchFirst(curSub, subFun, showFun);
            if (null != find) {
                callLinkSkip(find);
            }
        }
    }

    public void callLinkSkip(T currentT) {
        Stack<MutablePair<T, Integer>> stack = new Stack<MutablePair<T, Integer>>();
        stack.push(MutablePair.of(currentT, 0));
        addParticipantName(upNameFun.apply(currentT), spNameFun.apply(currentT));
        while (!stack.isEmpty()) {
            MutablePair<T, Integer> ele = stack.peek();
            if (CollectionUtils.isEmpty(subFun.apply(ele.getLeft()))) {
                stack.pop();
                linkSb.append("return").append(" " + nodeNameFun.apply(ele.getLeft())).append("\n");
            } else {
                if (ele.getRight() < subFun.apply(ele.getLeft()).size()) {
                    T p = ele.getLeft();
                    T s = subFun.apply(ele.getLeft()).get(ele.getRight());
                    T find = TreeUtils.preorderTraversalMatchFirst(s, subFun, showFun);
                    // 如果找到一个展示的，则添加到时序字符串当中
                    if (null != find) {
                        addParticipantName(upNameFun.apply(find), spNameFun.apply(find));
                        linkSb.append(participantName(upNameFun.apply(p), spNameFun.apply(p))).append("->").append(participantName(upNameFun.apply(find), spNameFun.apply(find))).append("++:").append(nodeNameFun.apply(find)).append("\n");
                        stack.push(MutablePair.of(find, 0));
                    }
                    // 不管找不到得到，index 得往上加
                    ele.setRight(ele.getRight() + 1);
                } else {
                    stack.pop();
                    if (stack.size() > 0) {
                        linkSb.append("return").append(" " + reValueFun.apply(ele.getLeft())).append("\n");
                    }
                }
            }
        }
    }

    /**
     * 添加参与者名称
     *
     * @param uName
     * @param sName
     */
    public void addParticipantName(String uName, String sName) {
        if (!uSNameMap.containsKey(uName)) {
            uSNameMap.put(uName, sName);
        }
        Map<String, Integer> uNameNumMap = suRepeatNumMap.get(sName);
        if (null == uNameNumMap) {
            uNameNumMap = new HashMap<>();
            uNameNumMap.put(uName, 1);
            suRepeatNumMap.put(sName, uNameNumMap);
        }
        Integer num = uNameNumMap.get(uName);
        if (null == num) {
            uNameNumMap.put(uName, uNameNumMap.size() + 1);
        }
    }

    /**
     * 获取参数者名称
     *
     * @param uName
     * @param sName
     * @return
     */
    public String participantName(String uName, String sName) {
        Integer num = suRepeatNumMap.get(sName).get(uName);
        if (num > 1) {
            return sName.replaceAll("\\$", "__") + "_" + String.valueOf(num);
        }
        return sName.replaceAll("\\$", "__");
    }

    /**
     * 生成最终 plantUml 文本
     *
     * @return
     */
    public String plantUmlText() {
        callLink();
        StringBuilder header = new StringBuilder();
        header.append("@startuml").append("\n");
        for (Map.Entry<String, String> entry : uSNameMap.entrySet()) {
            String nameShow = participantName(entry.getKey(), entry.getValue());
            header.append("participant ").append(" \"" + nameShow + "\" ").append(" as ").append(nameShow).append("\n");
        }
        linkSb.append("@enduml").append("\n");
        return header.toString() + linkSb.toString();
    }

    /**
     * 生成最终 plantUml 文本
     *
     * @return
     */
    public String plantUmlTextSkip() {
        callLinkSkip();
        StringBuilder header = new StringBuilder();
        header.append("@startuml").append("\n");
        for (Map.Entry<String, String> entry : uSNameMap.entrySet()) {
            String nameShow = participantName(entry.getKey(), entry.getValue());
            header.append("participant ").append(" \"" + nameShow + "\" ").append(" as ").append(nameShow).append("\n");
        }
        linkSb.append("@enduml").append("\n");
        return header.toString() + linkSb.toString();
    }
}
