package com.mall.common;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import javax.xml.ws.Response;
import java.io.Serializable;

/**
 * Created by wxhong on 2018/5/1.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
//如果在json序列化中，null对象key也会消失
public class ServerResponse<T> implements Serializable {

    private  int status;
    private  String msg;
    private  T data;

    private ServerResponse(int status){
        this.status = status;
    }

    private ServerResponse(int status, T data){
        this.status = status;
        this.data = data;
    }

    private ServerResponse(int status, String msg){
        this.status = status;
        this.msg = msg;
    }
    private ServerResponse(int status, String msg, T data){
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    @JsonIgnore
    //不在序列化结果当中
    public boolean isSuccess(){
        return this.status == ResponseCode.SUCCESS.getCode();
    }

    public int getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }

    public String getMsg() {
        return msg;
    }

    public static <T> ServerResponse<T> createBySuccess(){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode());
    }

    public static <T> ServerResponse<T> createBySuccessMessage(String msg){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(), msg);
    }

    public static <T> ServerResponse<T> createBySuccess(T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(), data);
    }

    public static <T> ServerResponse<T> createBySuccess(String msg, T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(), msg, data);
    }

    public static <T> ServerResponse<T> createByError(){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(), ResponseCode.ERROR.getDesc());
    }

    public static <T> ServerResponse<T> createByErrorMessage(String errmsg){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(), errmsg);
    }

    public static <T> ServerResponse<T> createByErrorCodeMessage(int errcode, String errmsg){
        return new ServerResponse<T>(errcode, errmsg);
    }
}
