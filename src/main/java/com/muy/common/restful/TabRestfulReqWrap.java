package com.muy.common.restful;

import com.intellij.openapi.project.Project;
import com.muy.common.tab.MRTabWrap;
import com.muy.view.window.rest.bean.HttpRequestData;

import javax.swing.*;

/**
 * @Author jiyanghuang
 * @Date 2022/9/26 20:32
 */
public class TabRestfulReqWrap extends MRTabWrap {

    private RestfulReqForm restfulReqForm;

    public TabRestfulReqWrap(Project project, int index, String title, JTabbedPane tabbedPane, boolean jbScrollPaneWrap){
        super(project, index, title, tabbedPane, null, false);
        restfulReqForm = new RestfulReqForm(project);
        updateJComponent(restfulReqForm, jbScrollPaneWrap);
    }

    public void fillReqBody(String reqBody){
        restfulReqForm.getRequestBody().fillSource(reqBody);
    }

    public String fetchReqBody(){
        return restfulReqForm.getRequestBody().text();
    }

    public void fillReqParams(String reqBody){
        restfulReqForm.getRequestParams().fillSourceSelect(reqBody);
    }

    public String fetchReqParams(){
        return restfulReqForm.getRequestParams().text();
    }

    public void fillDefaultData(){
        HttpRequestData httpRequestData = new HttpRequestData(restfulReqForm);
        httpRequestData.fillDataDefault();
    }

}
