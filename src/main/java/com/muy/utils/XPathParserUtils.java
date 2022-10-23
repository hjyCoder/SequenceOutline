package com.muy.utils;

import org.w3c.dom.Node;

import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @Author jiyanghuang
 * @Date 2022/10/22 23:57
 */
public class XPathParserUtils {

    public static String mapperNamespace(String path){
        try{
            InputStream inputStream = new FileInputStream(path);
            XPathParser xPathParser = new XPathParser(inputStream);
            Node node = xPathParser.evalNode("/mapper");
            if(null != node){
                return node.getAttributes().getNamedItem("namespace").getNodeValue();
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return "";
    }
}
