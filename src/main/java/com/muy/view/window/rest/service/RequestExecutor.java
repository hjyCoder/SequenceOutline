package com.muy.view.window.rest.service;


import com.muy.view.window.rest.bean.HttpRequestData;
import com.muy.view.window.rest.bean.Response;

import java.io.IOException;

public interface RequestExecutor {

    /**
     * 执行 http 请求
     * @param httpRequestData
     * @return
     */
    public Response execute(HttpRequestData httpRequestData) throws IOException;
}
