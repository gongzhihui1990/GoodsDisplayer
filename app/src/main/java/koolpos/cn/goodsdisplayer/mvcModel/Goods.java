package koolpos.cn.goodsdisplayer.mvcModel;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/5/13.
 */

public class Goods implements Serializable,Cloneable{
    private static final long serialVersionUID = 1097401162952403629L;
    private String goods_name;
    private String goods_id;
    private String image_url;
    private String goods_type;

    public String getGoods_name() {
        return goods_name;
    }

    public void setGoods_name(String goods_name) {
        this.goods_name = goods_name;
    }

    public String getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(String goods_id) {
        this.goods_id = goods_id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getGoods_type() {
        return goods_type;
    }

    public void setGoods_type(String goods_type) {
        this.goods_type = goods_type;
    }

    @Override
    public String toString() {
        return getGoods_name()+"-"+getGoods_id().split("-")[0];
    }

    @Override
    protected Goods clone() throws CloneNotSupportedException {
        return new Gson().fromJson(new Gson().toJson(this),Goods.class);
    }
}
