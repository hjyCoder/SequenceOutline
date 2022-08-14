package com.muy.view.component;

import com.google.common.collect.Maps;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManagerEvent;
import com.intellij.ui.content.ContentManagerListener;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * @Author jiyanghuang
 * @Date 2022/8/7 13:50
 */
public class SequenceOutlineComponent implements ContentManagerListener {

    private Project project;

    /**
     * {"currentContentTabName": "AbstractToolSetTab"}
     */
    private Map<String, AbstractSequenceOutlineSetTab> toolSetTabMap = Maps.newHashMap();

    /**
     * {"displayName-tabId":{"className":object}}
     */
    private Map<String, Map<String, Object>> objectCacheMap = Maps.newConcurrentMap();

    /**
     * 当前窗口
     */
    private ToolWindow toolWindow;

    /**
     * 生成对象
     *
     * @param project
     */
    protected SequenceOutlineComponent(Project project) {
        this.project = project;
    }

    /**
     * 类似于 spring 获取 bean 对象
     *
     * @param project
     * @return
     */
    public static SequenceOutlineComponent getInstance(Project project) {
        return ServiceManager.getService(project, SequenceOutlineComponent.class);
    }

    /**
     * 初始化组件
     */
    public void initRestComponent(ToolWindow toolWindow) {

        // 添加内容变更监听器
        toolWindow.getContentManager().addContentManagerListener(this);

        Content content;

        SequenceTab sequenceTab = new SequenceTab(project);
        content = ContentFactory.SERVICE.getInstance().createContent(sequenceTab.panelShow(), sequenceTab.tabName(), false);
        toolWindow.getContentManager().addContent(content);
        toolSetTabMap.put(sequenceTab.tabName(), sequenceTab);

        JsonTab jsonTab = new JsonTab(project);
        content = ContentFactory.SERVICE.getInstance().createContent(jsonTab.panelShow(), jsonTab.tabName(), false);
        toolWindow.getContentManager().addContent(content);
        toolSetTabMap.put(jsonTab.tabName(), jsonTab);

        toolWindow.activate(new Thread(), true);


    }


    /**
     * 创建面板界面
     */
    public <T extends AbstractSequenceOutlineSetTab> Content createRestClientContentPanel(Class<T> tabClass, Project project) {
        try {
            AbstractSequenceOutlineSetTab toolSetTab = tabClass.newInstance();
            toolSetTab.setProject(project);
            Content content = ContentFactory.SERVICE.getInstance().createContent(toolSetTab.panelShow(), toolSetTab.tabName(), false);
            content.setCloseable(false);
            return content;
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("content: " + ex);
            return null;
        }
    }

    @Override
    public void selectionChanged(@NotNull ContentManagerEvent event) {

    }


    public void register(Class<?> clazz, Object impl) {

    }

    /**
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T findInstance(Class<T> clazz) {
        String displayName = toolWindow.getContentManager().getSelectedContent().getDisplayName();
        String tabId = toolSetTabMap.get(displayName).getToolSetTabBar().getSelectedInfo().getText();
        Map<String, Object> classNameMap = objectCacheMap.get(cacheObjectKey(displayName, tabId));
        if (null != classNameMap) {
            T t = (T) classNameMap.get(clazz.getName());
            if (null == t) {
                System.out.println(clazz.getName());
                throw new RuntimeException("对象未初始化");
            }
            return t;
        }
        throw new RuntimeException("对象未初始化");
    }

    public void register(AbstractSequenceOutlineSetTab abstractToolSetTab, String tabId, Class<?> clazz, Object impl) {
        String displayNameTabIdKey = cacheObjectKey(abstractToolSetTab.tabName(), tabId);
        Map<String, Object> classNameMap = objectCacheMap.get(displayNameTabIdKey);
        if (null == classNameMap) {
            classNameMap = Maps.newConcurrentMap();
            objectCacheMap.put(displayNameTabIdKey, classNameMap);
        }
        classNameMap.put(clazz.getName(), impl);
    }

    public void removeRegister(String tabId) {
        String displayName = toolWindow.getContentManager().getSelectedContent().getDisplayName();
        objectCacheMap.remove(cacheObjectKey(displayName, tabId));
    }

    public String cacheObjectKey(String displayName, String tabId) {
        return displayName + "-" + tabId;
    }

    public ToolWindow getToolWindow() {
        return toolWindow;
    }

    public void setToolWindow(ToolWindow toolWindow) {
        this.toolWindow = toolWindow;
    }
}