package com.muy.so.agent.wrap.core.model.reflectinvoke;

/**
 * @Author jiyanghuang
 * @Date 2022/10/10 21:25
 */
public class AgentResult<T> {

    private boolean success;

    private T data;

    private String message;

    private Object[] reqParam;

    public static <T> AgentResult of(T data){
        AgentResult agentResult = new AgentResult();
        agentResult.setData(data);
        agentResult.setSuccess(true);
        return agentResult;
    }

    public static <T> AgentResult ofFail(String message){
        AgentResult agentResult = new AgentResult();
        agentResult.setMessage(message);
        agentResult.setSuccess(false);
        return agentResult;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object[] getReqParam() {
        return reqParam;
    }

    public void setReqParam(Object[] reqParam) {
        this.reqParam = reqParam;
    }
}
