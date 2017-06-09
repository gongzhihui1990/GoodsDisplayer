package koolpos.cn.goodsdisplayer.mvcModel;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/6/7.
 */
public class Product implements Serializable {
    private static final long serialVersionUID = -8772065541192560934L;
    private String title;
    private String price;
    private String picUrl;
    private boolean hasSelfImage;
    private String qrCodeUrl;
    private String groupId;
    private List<Integer> productCategoryIDs;
    public String getPrice() {
        return this.price;
    }
    public void setPrice(String price) {
        this.price = price;
    }
    public String getQrCodeUrl() {
        return this.qrCodeUrl;
    }
    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }
    public String getGroupId() {
        return this.groupId;
    }
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    public List<Integer> getProductCategoryIDs() {
        return this.productCategoryIDs;
    }
    public void setProductCategoryIDs(List<Integer> productCategoryIDs) {
        this.productCategoryIDs = productCategoryIDs;
    }
    public String getPicUrl() {
        return this.picUrl;
    }
    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }
    public boolean getHasSelfImage() {
        return this.hasSelfImage;
    }
    public void setHasSelfImage(boolean hasSelfImage) {
        this.hasSelfImage = hasSelfImage;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
