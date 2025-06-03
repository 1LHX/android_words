package com.ncusoft.myapplication.model;

public class ApiResponse {
    private int code;
    private String msg;
    private EnglishWords data;
    private String request_id;

    // Getter 方法
    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public EnglishWords getData() {
        return data;
    }

    public String getRequest_id() {
        return request_id;
    }

    // Setter 方法
    public void setCode(int code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setData(EnglishWords data) {
        this.data = data;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }
}
