package com.example.healthcare.LoginModule;

import com.example.healthcare.ResponseData.Data;
import com.google.gson.annotations.SerializedName;

// LoginResponse.java

public class LoginResponse {
    private String status;
    private Data data;
    private Object message;


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }
}


