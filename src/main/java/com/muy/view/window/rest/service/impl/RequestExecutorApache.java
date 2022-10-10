package com.muy.view.window.rest.service.impl;

import com.muy.view.window.rest.bean.HttpRequestData;
import com.muy.view.window.rest.bean.Response;
import com.muy.view.window.rest.service.RequestExecutor;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RequestExecutorApache implements RequestExecutor {

    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    private final HttpClient httpClient = createHttpClient();

    @Override
    public Response execute(HttpRequestData httpRequestData) throws IOException {
        try{
            HttpRequestBase httpRequestBase = httpRequestData.createHttpRequest();
            HttpResponse response = httpClient.execute(httpRequestBase);
            return new Response(
                    response.getStatusLine().toString(),
                    getHeaders(response),
                    getContentType(response),
                    EntityUtils.toString(response.getEntity(), DEFAULT_CHARSET)
            );
        }finally {

        }
    }


    /**
     * 创建生成请求对象
     * @return
     */
    private CloseableHttpClient createHttpClient(){
        try{
            SSLContextBuilder contextBuilder = SSLContextBuilder.create();
            contextBuilder.loadTrustMaterial(null, (x509Certificates, s) -> true);
            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(contextBuilder.build(), NoopHostnameVerifier.INSTANCE);
            return HttpClients.custom().setSSLSocketFactory(sslConnectionSocketFactory).build();
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    private String getContentType(HttpResponse response){
        Header header = response.getFirstHeader("Content-type");
        if(null != header){
            String contentType = header.getValue();
            int semicolonIndex = contentType.indexOf(";");
            if(semicolonIndex >= 0){
                return contentType.substring(0, semicolonIndex);
            }else{
                return contentType;
            }
        }
        return null;
    }

    public List<String> getHeaders(HttpResponse response){
        Header[] allHeaders = response.getAllHeaders();
        if(null != allHeaders){
            ArrayList<String> result = new ArrayList<>();
            for(Header header : allHeaders){
                result.add(header.getName() + ": " + header.getValue());
            }
            return result;
        }
        return Collections.EMPTY_LIST;
    }
}
