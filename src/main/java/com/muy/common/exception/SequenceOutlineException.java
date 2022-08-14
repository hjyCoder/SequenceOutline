package com.muy.common.exception;

/**
 * @Author jiyanghuang
 * @Date 2022/8/7 13:50
 */
public class SequenceOutlineException extends RuntimeException{
    private ResponseCodeEnum responseCode;

    public SequenceOutlineException(){
        super();
    }

    public SequenceOutlineException(String message){
        super(message);
    }

    public SequenceOutlineException(String message, Throwable cause){
        super(message, cause);
    }

    public SequenceOutlineException(ResponseCodeEnum responseCode){
        super(responseCode.getCode() + "_" + responseCode.getDesc());
        this.responseCode = responseCode;
    }

    public SequenceOutlineException(ResponseCodeEnum responseCode, String message){
        super(message);
        this.responseCode = responseCode;
    }

    public ResponseCodeEnum getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(ResponseCodeEnum responseCode) {
        this.responseCode = responseCode;
    }
}
