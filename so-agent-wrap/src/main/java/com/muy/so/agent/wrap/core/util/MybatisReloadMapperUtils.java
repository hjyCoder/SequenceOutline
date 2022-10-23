package com.muy.so.agent.wrap.core.util;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @Author jiyanghuang
 * @Date 2022/10/23 01:18
 */
public class MybatisReloadMapperUtils {

    public static void reloadMybatisMapper(Class<?> reloadMapper, Object mapperBean){
        try{
            Object h = FieldUtils.readField(mapperBean, "h", true);
            if("org.apache.ibatis.binding.MapperProxy".equals(h.getClass().getName())){
                Object sqlSession = FieldUtils.readField(h, "sqlSession", true);
                Object configuration = MethodUtils.invokeMethod(sqlSession, "getConfiguration");
                Object mapperRegistry = FieldUtils.readField(configuration, "mapperRegistry", true);
                Map knownMappers = (Map)FieldUtils.readField(mapperRegistry, "knownMappers", true);
                // 删除已经注册的Mapper
                knownMappers.remove(reloadMapper);
                // 存储的key分为1.interface mapper 2.namespace:mapper 3. file [mapper.xml]
                Set<String> loadedResources = (Set)FieldUtils.readField(configuration, "loadedResources", true);
                Map<String, Object> mappedStatements = (Map)FieldUtils.readField(configuration, "mappedStatements", true);
                Map<String, Object> resultMaps = (Map)FieldUtils.readField(configuration, "resultMaps", true);
                // 删除已经解释过的 key, 用于重新加载新的 Mapper
                removeLoadedResources(loadedResources, reloadMapper.getName());
                // 删除 mappedStatements
                mapRemoveValueContainId((Map)mappedStatements, reloadMapper.getName());
                // 删除 resultMaps
                mapRemoveValueContainId((Map)resultMaps, reloadMapper.getName());
                // 重新加载按注解方式实现的 mybatis
                MethodUtils.invokeExactMethod(mapperRegistry, "addMapper", reloadMapper);
                // 重新加载按xml实现方式的 mybatis
                loadXmlResourceReflect(configuration, loadedResources, reloadMapper.getName(), reloadMapper.getSimpleName());
                System.out.println("");
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     *
     * @param map 被缓存的Map, value中都包含一个 id, 这个id以类名为前缀
     * @param start 类名前前缀
     */
    public static void mapRemoveValueContainId(Map<String, Object> map, String start){
        Map<String, Object> mapDelete = Maps.newHashMap();
        for(Map.Entry<String, Object>  entry : map.entrySet()){
            try{
                String id = (String)FieldUtils.readField(entry.getValue(), "id", true);
                if(null != id && id.startsWith(start)){
                    mapDelete.put(entry.getKey(), entry.getValue());
                }
            }catch (Exception ex){
//                ex.printStackTrace();
            }
        }
        for(Map.Entry<String, Object> entry : mapDelete.entrySet()){
            map.remove(entry.getKey(), entry.getValue());
        }
    }

    /**
     *
     * @param loadedResources 重新key校验 Set
     * @param className 当前需要重新加载的 mapper 类名
     */
    public static void removeLoadedResources(Set<String> loadedResources, String className){
        String nameSpace = "namespace:" + className;
        String interfaceN = "interface " + className;
        loadedResources.remove(nameSpace);
        loadedResources.remove(interfaceN);
    }

    /**
     *
     * @param configuration 当前配置类
     * @param resources 加载的资源
     * @param fulClassName mapper 的全类名
     * @param simpleName 类名
     */
    private static void loadXmlResourceReflect(Object configuration, Set<String> resources, String fulClassName, String simpleName) {
        String filePath = "";
        for(String path : resources){
            if(path.endsWith(simpleName + ".xml]")){
                filePath = path.substring("file [".length(), path.length() - 1);
            }
        }
        if(StringUtils.isBlank(filePath)){
            return;
        }
        InputStream inputStream = null;
        try {
            // 按全路径查询文件
            inputStream = new FileInputStream(filePath);
            if (inputStream != null) {
                // 删除已经加载的片断
                Map<String, Object> map = (Map)MethodUtils.invokeExactMethod(configuration, "getSqlFragments");
                Iterator<Map.Entry<String, Object>> itern = map.entrySet().iterator();
                while(itern.hasNext()){
                    Map.Entry<String, Object> entry = itern.next();
                    // 按名称前缀来删了
                    if(entry.getKey().startsWith(fulClassName)){
                        itern.remove();
                    }
                }
                Object xmlMapperBuilder = ConstructorUtils.invokeConstructor(Class.forName("org.apache.ibatis.builder.xml.XMLMapperBuilder"), inputStream, configuration, "namespace:" + fulClassName, map, fulClassName);
                MethodUtils.invokeMethod(xmlMapperBuilder, "parse");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

    }
}
