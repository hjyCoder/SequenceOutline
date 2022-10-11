package com.muy.common.restful;

import com.google.common.collect.Maps;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.concurrency.NonUrgentExecutor;
import com.intellij.util.ui.components.BorderLayoutPanel;
import com.muy.common.tab.TabJsonPanelCompareWrap;
import com.muy.common.tab.TabJsonWrap;
import com.muy.common.utils.JsonUtils;
import com.muy.view.window.rest.bean.HttpMethod;
import com.muy.view.window.rest.bean.HttpRequestData;
import com.muy.view.window.rest.bean.Request;
import com.muy.view.window.rest.bean.Response;
import com.muy.view.window.rest.convert.BaseConvert;
import com.muy.view.window.rest.convert.JsonConvert;
import com.muy.view.window.rest.service.RequestExecutor;
import com.muy.view.window.rest.service.impl.RequestExecutorApache;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.jdesktop.swingx.JXButton;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author jiyanghuang
 * @Date 2022/9/14 20:18
 */
public class RestfulReqForm extends BorderLayoutPanel {
    private static final int REQUEST_TIMEOUT = 1000 * 10;

    private static final String IDENTITY_HEAD = "HEAD";
    private static final String IDENTITY_REQUEST_PARAM = "REQUEST_PARAM";
    private static final String IDENTITY_BODY = "BODY";

    public static final String AGENT_INVOKE_PATH = "/sandbox/default/module/http/sequenceOutline/reflectInvoke";

    public static final String REQ_PARAM_KEY = "_data";


    private final Project project;
    private final ThreadPoolExecutor poolExecutor;
    private final BaseConvert<?> convert;

    @Getter
    private Map<String,String> applicationProperties = Maps.newHashMap();

    /**
     * 下拉框 - 选择选择请求方法
     */
    private JComboBox<HttpMethod> requestMethod;

    /**
     * 下拉框 - 选择选择请求方法
     */
    private JComboBox<String> hostPortsCb;

    /**
     * 输入框 - url地址
     */
    private JTextField requestUrl;
    /**
     * 按钮 - 发送请求
     */
    private JButton sendRequest;
    /**
     * 选项卡面板 - 请求信息
     */
    private JTabbedPane tabbedPane;
    /**
     * 文本域 - 请求头
     */
    private TabJsonWrap requestHead;

    /**
     * 文本域 - 请求参数，包含 @path 参数
     */
    @Getter
    @Setter
    private TabJsonWrap requestParams;

    /**
     * 文本域 - 请求体
     */
    @Getter
    @Setter
    private TabJsonWrap requestBody;
    /**
     * 标签 - 显示返回结果
     */
    private TabJsonPanelCompareWrap responseView;

    /**
     * 选中的Request
     */
    private Request chooseRequest;

    /**
     * 按钮 - 保存请求参数
     */
    private JButton reqParamSave;

    /**
     * 按钮 - 通过 rest 发送请求
     */
    private JButton sendR;

    private RequestExecutor requestExecutor = new RequestExecutorApache();;

    public RestfulReqForm(Project project){
        this.project = project;
        this.poolExecutor = new ThreadPoolExecutor(
                1,
                1,
                1000,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(8),
                new ThreadPoolExecutor.DiscardOldestPolicy()
        );

        this.convert = new JsonConvert();

        initViewTop();
        initViewCenter();
        initEvent();
    }

    private void initViewTop(){
        JPanel panelInput = new JPanel();
        add(panelInput, BorderLayout.NORTH);
        panelInput.setLayout(new BorderLayout(0, 0));

        JPanel httpAll = new JPanel();
        httpAll.setLayout(new BorderLayout(0, 0));


        requestMethod = new ComboBox<>(HttpMethod.getValues());
        httpAll.add(requestMethod, BorderLayout.WEST);

        String[] hostPorts = new String[5];
        hostPorts[0] = "http://localhost:8822";

        hostPortsCb = new ComboBox<String>(hostPorts);
        hostPortsCb.setEditable(true);
        hostPortsCb.setPreferredSize(new Dimension(300, 30));
        httpAll.add(hostPortsCb, BorderLayout.CENTER);

        panelInput.add(httpAll, BorderLayout.WEST);

        requestUrl = new JBTextField();
        panelInput.add(requestUrl);
        requestUrl.setColumns(45);

        JPanel buttonGroup = new JPanel();
        buttonGroup.setLayout(new BorderLayout(0, 0));
        sendRequest = new JXButton("sendD");
        buttonGroup.add(sendRequest, BorderLayout.EAST);

        sendR = new JXButton("sendR");
        buttonGroup.add(sendR, BorderLayout.CENTER);

        reqParamSave = new JXButton("reqParamSave");
        buttonGroup.add(reqParamSave, BorderLayout.WEST);
        panelInput.add(buttonGroup, BorderLayout.EAST);
    }

    private void initViewCenter(){
        tabbedPane = new JBTabbedPane(JTabbedPane.TOP);
        addToCenter(tabbedPane);
        requestHead = new TabJsonWrap(project, 0, "head", tabbedPane, "{}", false);
        requestParams = new TabJsonWrap(project, 1, "Params", tabbedPane, "{}", false);
        requestBody = new TabJsonWrap(project, 2, "body", tabbedPane, "{}", false);
        responseView = new TabJsonPanelCompareWrap(project, 3, "response", tabbedPane, "{}", false);
    }

    /**
     * 初始化事件
     */
    private void initEvent() {
        // 发送请求按钮监听
        sendRequest.addActionListener(event -> {
            try {
                ReadAction.nonBlocking(() -> {
                    String resp = "";
                    HttpRequestData httpRequestData = new HttpRequestData(RestfulReqForm.this);
                    httpRequestData.fetchData();
                    Response response = null;
                    try {
                        response = requestExecutor.execute(httpRequestData);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return e.getMessage();
                    }
                    resp = response.getBody();
                    return resp;
                }).finishOnUiThread(ModalityState.NON_MODAL, (c) -> {
                    if (null != c) {
                        responseView.fillSourceSelect(JsonUtils.formatJsonWrap(c));
                    }
                }).inSmartMode(project).submit(NonUrgentExecutor.getInstance());
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    public void ofDefaultValue(){
        requestUrl.setText("/sandbox/default/module/http/sequenceOutline/reflectInvoke");
    }

    public void fetchData(HttpRequestData httpRequestData) {
        HttpMethod method = (HttpMethod) requestMethod.getSelectedItem();
        if (method == null) {
            method = HttpMethod.GET;
        }
        String url = requestUrl.getText();

        if (url == null || "".equals(url.trim())) {
            responseView.fillSource("{\"re\":\"request path must be not empty!\"}");
            return;
        }

        String head = requestHead.text();
        String body = requestBody.text();
        String requestParam = requestParams.text();

        RequestExecutor requestExecutor = new RequestExecutorApache();

        String[] hostPort = hostPortsCb.getSelectedItem().toString().split(":");
        String host = hostPort[0];
        String port = null;
        if (null != hostPort) {
            if (hostPort.length == 2) {
                host = hostPort[0] + ":" + hostPort[1];
            } else if (hostPort.length == 3) {
                host = hostPort[0] + ":" + hostPort[1];
                port = hostPort[2];
            }
        }

        httpRequestData.setHost(host);
        httpRequestData.setPort(port);
        httpRequestData.setMethod(method);
        httpRequestData.setPath(requestUrl.getText());
        httpRequestData.setHeaders(HttpRequestData.jsonToMap(head));
        if(AGENT_INVOKE_PATH.equals(httpRequestData.getPath())){
            if(StringUtils.isNotBlank(requestParam)){
                Map<String,String> reqParamMap = Maps.newHashMap();
                reqParamMap.put(REQ_PARAM_KEY, requestParam);
                httpRequestData.setQueryParams(reqParamMap);
            }
        }else{
            httpRequestData.setQueryParams(HttpRequestData.jsonToMap(requestParam));
        }
        httpRequestData.setBodyJsonStr(body);
        if (null == chooseRequest) {
            httpRequestData.setModuleName("defaultModule");
        } else {
            httpRequestData.setModuleName(chooseRequest.getModuleName());
        }
    }

    /**
     * 填充界面值
     * @param httpRequestData
     */
    public void fillData(HttpRequestData httpRequestData){
        requestMethod.setSelectedItem(httpRequestData.getMethod());
        hostPortsCb.setSelectedItem(httpRequestData.fillBaseUrl());
        requestUrl.setText(httpRequestData.getPath());
    }
}
