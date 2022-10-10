package com.muy.view.window.rest.bean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Maps;
import com.muy.common.restful.RestfulReqForm;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class HttpRequestData {

    /**
     * 应用名称
     */
    @Getter
    @Setter
    private String appName = "metadatacenter";

    /**
     * 模块名称
     */
    @Getter
    @Setter
    private String moduleName = "";

    /**
     * 域名
     */
    @Getter
    @Setter
    private String host;

    /**
     * 端口
     */
    @Getter
    @Setter
    private String port;

    /**
     * 请求方法
     */
    @Getter
    @Setter
    @Nullable
    private HttpMethod method;

    /**
     * 请求路径
     */
    @Getter
    @Setter
    private String path;

    /**
     * 请求头
     */
    @Getter
    @Setter
    private Map<String, String> headers = Maps.newHashMap();

    /**
     * 请求查询参数
     */
    @Getter
    @Setter
    private Map<String, String> queryParams = Maps.newHashMap();

    /**
     * 请求body参数
     */
    @Getter
    @Setter
    private String bodyJsonStr;

    /**
     * 静态参数，一些相对默认的值
     */
    @Getter
    @Setter
    private String staticParamsValues;

    /**
     * 请求界面
     */
    private RestfulReqForm restfulReqForm;


    public HttpRequestData(){

    }

    public HttpRequestData(RestfulReqForm restfulReqForm){
        this.restfulReqForm = restfulReqForm;
    }

    public HttpRequestData(String host, String port, @Nullable HttpMethod method, String path, String headerJsonStr, String requestParamsJsonStr, String bodyJsonStr){
        this.host = host;
        this.port = port;
        this.method = method;
        this.path = path;

        if(StringUtils.isNotBlank(headerJsonStr)){
            headers = JSON.parseObject(headerJsonStr, new TypeReference<Map<String,String>>(){});
        }
        if(StringUtils.isNotBlank(requestParamsJsonStr)){
            queryParams = JSON.parseObject(requestParamsJsonStr, new TypeReference<Map<String,String>>(){});
        }
        this.bodyJsonStr = bodyJsonStr;
    }
    public HttpRequestData(String host, String port, @Nullable HttpMethod method, String path, Map<String, String> headers, Map<String, String> queryParams, String bodyJsonStr) {
        this.host = host;
        this.port = port;
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.queryParams = queryParams;
        this.bodyJsonStr = bodyJsonStr;
    }

    public HttpRequestData(String host, String port, @Nullable HttpMethod method, String path, Map<String, String> headers, Map<String, String> queryParams, String bodyJsonStr, String staticParamsValues) {
        this.host = host;
        this.port = port;
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.queryParams = queryParams;
        this.bodyJsonStr = bodyJsonStr;

    }

    public HttpRequestBase createHttpRequest() throws UnsupportedEncodingException {
        if(HttpMethod.GET.equals(method)){
            HttpGet httpGet = new HttpGet(fillUri());
            fillHeaders(httpGet);
            return httpGet;
        }

        if(HttpMethod.POST.equals(method)){
            HttpPost httpPost = new HttpPost(fillUri());
            fillHeaders(httpPost);
            fillBody(httpPost);
            return httpPost;
        }

        if(HttpMethod.PUT.equals(method)){
            HttpPut httpPut = new HttpPut(fillUri());
            fillHeaders(httpPut);
            fillBody(httpPut);
            return httpPut;
        }

        if(HttpMethod.PATCH.equals(method)){
            HttpPatch httpPatch = new HttpPatch(fillUri());
            return httpPatch;
        }

        if(HttpMethod.DELETE.equals(method)){
            HttpDelete httpDelete = new HttpDelete(fillUri());
            fillHeaders(httpDelete);
            return httpDelete;
        }

        if(HttpMethod.OPTIONS.equals(method)){
            HttpDelete httpDelete = new HttpDelete(fillUri());
            fillHeaders(httpDelete);
            return httpDelete;
        }

        throw new IllegalStateException();
    }

    private void fillHeaders(HttpRequestBase httpRequest){
        if(MapUtils.isNotEmpty(headers)){
            headers.forEach((k,v) -> {
                httpRequest.addHeader(k, v);
            });
        }
    }

    /**
     * 请求body
     *
     * @param httpRequest
     * @throws UnsupportedEncodingException
     */
    private void fillBody(HttpEntityEnclosingRequestBase httpRequest) throws UnsupportedEncodingException {
        if(StringUtils.isNotBlank(bodyJsonStr)){
            StringEntity s = new StringEntity(bodyJsonStr);
            s.setContentEncoding("UTF-8");
            s.setContentType("application/json");
            httpRequest.setEntity(s);
        }
    }

    public String fillBaseUrl(){
        return host + (StringUtils.isBlank(port) ? "" : ":" + port);
    }

    private String replacePath(){
        String[] pathSplits = path.split("/");
        StringBuilder pathBuilder = new StringBuilder();
        for(int i = 0; i < pathSplits.length; i++){
            if(StringUtils.isBlank(pathSplits[i])){
                continue;
            }
            pathBuilder.append("/");
            if(pathSplits[i].startsWith("{") && pathSplits[i].endsWith("}")){
                String v = queryParams.get(pathSplits[i]);
                if(StringUtils.isBlank(v)){
                    // todo 异常
                }else{
                    pathBuilder.append(v);
                }
                continue;
            }
            pathBuilder.append(pathSplits[i]);
        }
        // 拼接参数 ?key=value
        StringBuilder paramStr = new StringBuilder();
        if(MapUtils.isNotEmpty(queryParams)){
            paramStr.append("?");
            try{
                for(Map.Entry<String, String> entry : queryParams.entrySet()){
                    if(entry.getKey().startsWith("{") && entry.getKey().endsWith("}")){
                        continue;
                    }
                    paramStr.append(entry.getKey()).append("=").append(Optional.ofNullable(URLEncoder.encode(entry.getValue(), "UTF-8")).orElse("")).append("&");
                }
                paramStr.deleteCharAt(paramStr.length() - 1);
            }catch (Exception ex){
                ex.printStackTrace();
            }

        }
        return pathBuilder.toString() + paramStr.toString();
    }

    private String fillUri(){
        return fillBaseUrl() + replacePath();
    }

    @Override
    public String toString() {
        return "ShowName";
    }

    @NotNull
    public String getIdentity(String... itemIds) {
        return path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HttpRequestData that = (HttpRequestData) o;
        return Objects.equals(appName, that.appName) &&
                Objects.equals(host, that.host) &&
                Objects.equals(port, that.port) &&
                method == that.method &&
                Objects.equals(path, that.path) &&
                Objects.equals(headers, that.headers) &&
                Objects.equals(queryParams, that.queryParams) &&
                Objects.equals(bodyJsonStr, that.bodyJsonStr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appName, host, port, method, path, headers, queryParams, bodyJsonStr);
    }

    public void fetchData(){
        restfulReqForm.fetchData(this);
    }

    public void fillDataDefault(){
        this.host = "http://localhost";
        this.port = "8822";
        this.method = HttpMethod.POST;
        this.path = "/sandbox/default/module/http/sequenceOutline/reflectInvoke";
        restfulReqForm.fillData(this);
    }

    public static Map<String, String> jsonToMap(String json) {
        if (StringUtils.isNotBlank(json)) {
            return JSON.parseObject(json, new TypeReference<Map<String, String>>() {
            });
        }
        return null;
    }
}
