package com.muy.view.window.rest.bean;

import java.util.List;

public class Response {
    private final String status;
    private final List<String> headers;
    private final String contentType;
    private final String body;

    public Response(String status, List<String> headers, String contentType, String body) {
        this.status = status;
        this.headers = headers;
        this.contentType = contentType;
        this.body = body;
    }

    public String getStatus() {
        return status;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public String getContentType() {
        return contentType;
    }

    public String getBody() {
        return body;
    }
}
