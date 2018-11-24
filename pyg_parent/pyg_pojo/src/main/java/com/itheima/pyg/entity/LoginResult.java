package com.itheima.pyg.entity;

import java.io.Serializable;

public class LoginResult    implements Serializable {
    private boolean success;
    private String  loginName;
    private Object  data;


    public LoginResult() {
    }

    public LoginResult(boolean success, String loginName, Object data) {

        this.success = success;
        this.loginName = loginName;
        this.data = data;
    }

    public boolean isSuccess() {

        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
