package com.miaxis.btlockcommanderdemo.model.net;

public class ResponseEntity<T> {
    public String code;
    public String error;
    public T data;

    public ResponseEntity() {
    }

    public ResponseEntity(String code, String error, T data) {
        this.code = code;
        this.error = error;
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResponseEntity{" +
                "code='" + code + '\'' +
                ", error='" + error + '\'' +
                ", data=" + data +
                '}';
    }

}
