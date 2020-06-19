package com.mmall.common;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.util.ISO8601Utils;
import org.w3c.dom.ls.LSOutput;

import java.io.Serializable;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)//保证Json序列化对象，如果是null对象，key会消失
public class serverResponse<T> implements Serializable {
    private int status;
    private String msg;
    private T data;

    private serverResponse(int status) {
        this.status = status;
    }

    private serverResponse(int status,T data){
        this.status = status;
        this.data = data;
    }

    private serverResponse(int status,String msg,T data) {
        this.status = status;
        this.data = data;
        this.msg = msg;
    }

    private serverResponse(int status,String msg) {
        this.status = status;
        this.msg = msg;
    }

    @JsonIgnore//使之不在Json序列化
    public boolean isSuccess(){
        return this.status == responseCode.SUCCESS.getCode();
    }

    public int getStatus(){
        return status;
    }

    public T getData(){
        return data;
    }

    public String getMsg(){
        return msg;
    }

    public static <T> serverResponse<T> createBySuccess(){
        return new serverResponse<T>(responseCode.SUCCESS.getCode());
    }

    public static <T> serverResponse<T> createBySuccessMessage(String msg){
        return new serverResponse<T>(responseCode.SUCCESS.getCode(),msg);
    }

    public static <T> serverResponse<T> createBySuccess(T data){
        return new serverResponse<T>(responseCode.SUCCESS.getCode(),data);
    }

    public static <T> serverResponse<T> createBySuccess(String msg,T data){
        return new serverResponse<T>(responseCode.SUCCESS.getCode(),msg,data);
    }

    public static <T> serverResponse<T> createByError(){
        return new serverResponse<T>(responseCode.ERROR.getCode(),responseCode.ERROR.getDesc());
    }

    public static <T> serverResponse<T> createByErrorMessage(String errorMessage){
        return new serverResponse<T>(responseCode.ERROR.getCode(),errorMessage);
    }

    public static <T> serverResponse<T> createByErrorCodeMessage(int errorCode,String errorMessage){
        return new serverResponse<T>(errorCode,errorMessage);
    }
}
