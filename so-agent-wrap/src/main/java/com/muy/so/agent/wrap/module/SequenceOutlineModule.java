package com.muy.so.agent.wrap.module;

import com.alibaba.jvm.sandbox.api.Information;
import com.alibaba.jvm.sandbox.api.Module;
import com.alibaba.jvm.sandbox.api.ModuleLifecycle;
import com.alibaba.jvm.sandbox.api.annotation.Command;
import com.alibaba.jvm.sandbox.api.resource.ConfigInfo;
import com.alibaba.jvm.sandbox.api.resource.LoadedClassDataSource;
import com.alibaba.jvm.sandbox.api.resource.ModuleController;
import com.alibaba.jvm.sandbox.api.resource.ModuleEventWatcher;
import com.muy.so.agent.wrap.Constants;
import com.muy.so.agent.wrap.core.model.reflectinvoke.BeanFindVO;
import com.muy.so.agent.wrap.core.model.reflectinvoke.BeanInvokeType;
import com.muy.so.agent.wrap.core.model.reflectinvoke.BeanInvokeVO;
import com.muy.so.agent.wrap.core.model.reflectinvoke.MethodInvokeVO;
import com.muy.so.agent.wrap.core.spring.SpringContextAdapter;
import com.muy.so.agent.wrap.core.spring.SpringContextInnerContainer;
import com.muy.so.agent.wrap.core.util.*;
import com.muy.so.agent.wrap.module.advice.SpringInstantiateAdvice;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.kohsuke.MetaInfServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 模仿Greys的trace命令
 * <p>测试用模块</p>
 */
@MetaInfServices(Module.class)
@Information(id = SOConstants.MODULE_ID, version = SOConstants.VERSION, author = "mydeyouxiang2@sina.cn")
public class SequenceOutlineModule extends ParamSupported implements Module, ModuleLifecycle {

    private final static Logger log = LoggerFactory.getLogger(SequenceOutlineModule.class);

    @Resource
    private ModuleEventWatcher moduleEventWatcher;

    @Resource
    private ModuleController moduleController;

    @Resource
    private ModuleEventWatcher eventWatcher;

    @Resource
    private ConfigInfo configInfo;

    @Resource
    private LoadedClassDataSource loadedClassDataSource;

    private AtomicBoolean initialized = new AtomicBoolean(false);

    @Override
    public void onLoad() throws Throwable {
        // 初始化日志框架
        LogbackUtils.init(PathUtils.getConfigPath() + "/repeater-logback.xml");
        Information.Mode mode = configInfo.getMode();
        log.info("module on loaded,id={},version={},mode={}", SOConstants.MODULE_ID, SOConstants.VERSION, mode);
        /* agent方式启动 */
        if (mode == Information.Mode.AGENT) {
            log.info("agent launch mode,use Spring Instantiate Advice to register bean.");
            SpringContextInnerContainer.setAgentLaunch(true);
            SpringInstantiateAdvice.watcher(this.eventWatcher).watch();
            moduleController.active();
        }
    }

    @Override
    public void onUnload() throws Throwable {

    }

    @Override
    public void onActive() throws Throwable {
        log.info("onActive");
    }

    @Override
    public void onFrozen() throws Throwable {
        log.info("onFrozen");
    }

    /**
     * 当模块加载完全后
     */
    @Override
    public void loadCompleted() {
        log.info("loadCompleted");
    }


    /**
     * 回放http接口
     *
     * @param req    请求参数
     * @param writer printWriter
     */
    @Command("reflectInvoke")
    public void repeat(final Map<String, String> req, final PrintWriter writer) {
        try {
            String data = req.get(Constants.DATA_TRANSPORT_IDENTIFY);
            if (StringUtils.isEmpty(data)) {
                writer.write("invalid request, cause parameter {" + Constants.DATA_TRANSPORT_IDENTIFY + "} is required");
                return;
            }
            BeanInvokeVO beanInvokeVO = JacksonUtils.toJavaObject(data, BeanInvokeVO.class);
            Class<?> clazz = Class.forName(beanInvokeVO.getClassFullName());
            Object obj = null;
            if(BeanInvokeType.ONLY_METHOD.getCode() == beanInvokeVO.getInvokeType()){
                if(null != beanInvokeVO.getBeanFind()){
                    BeanFindVO beanFindVO = beanInvokeVO.getBeanFind();
                    Object indirectObj = null;
                    // 如果bean名称找不到则按类名称查询
                    indirectObj = SpringContextAdapter.getBeanByName(beanFindVO.getBeanName());
                    if(null == indirectObj){
                        indirectObj = SpringContextAdapter.getBeanByType(beanFindVO.getClassFullName());
                    }
                    if(null == indirectObj){
                        writer.write("can't not find bean indirectObj");
                        return;
                    }
                    obj = FieldUtils.readField(indirectObj, beanFindVO.getFieldName(), true);
                }else{
                    // 如果bean名称找不到则按类名称查询
                    obj = SpringContextAdapter.getBeanByName(beanInvokeVO.getBeanName());
                    if(null == obj){
                        obj = SpringContextAdapter.getBeanByType(beanInvokeVO.getClassFullName());
                    }
                    if(null != obj){
                        // 如果对象是 FactoryBean 则需要 getObject 才能生效
                        if(ReflectInvokeUtils.inheritClassName("org.springframework.beans.factory.FactoryBean", obj.getClass())){
                            obj = MethodUtils.invokeMethod(obj, "getObject");
                        }
                    }
                }

            }else if(BeanInvokeType.CONSTRUCT_INVOKE_METHOD.getCode() == beanInvokeVO.getInvokeType()){
                MethodInvokeVO constructorMethod = beanInvokeVO.getConstructorMethod();
                if(CollectionUtils.isEmpty(constructorMethod.getMpjtcs())){
                    Constructor constructor = clazz.getConstructor();
                    obj = constructor.newInstance();
                }else{
                    Constructor constructor = clazz.getConstructor(ReflectInvokeUtils.classesArr(constructorMethod));
                    obj = constructor.newInstance(ReflectInvokeUtils.paramArr(constructorMethod));
                }
            }else if(BeanInvokeType.RELOAD_MYBATIS.getCode() == beanInvokeVO.getInvokeType()){
                obj = SpringContextAdapter.getBeanByName(beanInvokeVO.getBeanName());
                if(null == obj){
                    obj = SpringContextAdapter.getBeanByType(beanInvokeVO.getClassFullName());
                }
                if(null == obj){
                    writer.write("can't find the mapper ");
                    return;
                }
                // obj 是 org.mybatis.spring.mapper.MapperFactoryBean
                obj = MethodUtils.invokeMethod(obj, "getObject");
                MybatisReloadMapperUtils.reloadMybatisMapper(clazz, obj);
                writer.write("reload mybatis success !");
                return;
            }

            MethodInvokeVO methodDesc = beanInvokeVO.getMethod();
            if(methodDesc.constructor()){
                writer.write("new Object success !");
                return;
            }
            Method method = ReflectInvokeUtils.findMethod(clazz, methodDesc.getMethodName(), ReflectInvokeUtils.classesArr(methodDesc));
            if(null == method){
                writer.write("can't not find method");
                return;
            }
            method.setAccessible(true);
            Object[] reqParam = ReflectInvokeUtils.paramArr(methodDesc);
            Object result = method.invoke(obj, reqParam);
            String resultJson = ReflectInvokeUtils.ofJson(result, reqParam);
            writer.write(resultJson);
        } catch (NullPointerException npe){
            writer.write(ReflectInvokeUtils.ofFailJson("NullPointerException"));
        }catch (InvocationTargetException invocationTargetException){
            writer.write(ReflectInvokeUtils.ofFailJson(invocationTargetException.getTargetException().getMessage()));
        }catch (Throwable e) {
            writer.write(ReflectInvokeUtils.ofFailJson(e.getMessage()));
        }
    }

}
