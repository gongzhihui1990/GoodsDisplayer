package koolpos.cn.goodsdisplayer.mvcModel;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/5/14.
 */

public class AidlResponse implements Serializable{
    private static final long serialVersionUID = 8781217388770698717L;
    private String message;
    private String data;
    private int code;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
