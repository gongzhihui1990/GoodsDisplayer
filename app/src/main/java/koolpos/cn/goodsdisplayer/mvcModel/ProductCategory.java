package koolpos.cn.goodsdisplayer.mvcModel;


import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by caroline on 2017/6/6.
 */
public class ProductCategory implements Serializable {

    private static final long serialVersionUID = 2879859948096190549L;
    private String categoryCode;
    private int categoryId;
    private String name;
    private int parentCategoryId;
    private String parentCategoryCode;
    private String groupId;
    private String imageUrl;
    private String iconUrl;
    private boolean isLocal;
    private boolean isSpecial;
    private String fromType;
    public String getCategoryCode() {
        return this.categoryCode;
    }
    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getGroupId() {
        return this.groupId;
    }
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    public String getImageUrl() {
        return this.imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public String getIconUrl() {
        return this.iconUrl;
    }
    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
    public boolean getIsLocal() {
        return this.isLocal;
    }
    public void setIsLocal(boolean isLocal) {
        this.isLocal = isLocal;
    }
    public boolean getIsSpecial() {
        return this.isSpecial;
    }
    public void setIsSpecial(boolean isSpecial) {
        this.isSpecial = isSpecial;
    }
    public String getFromType() {
        return this.fromType;
    }
    public void setFromType(String fromType) {
        this.fromType = fromType;
    }
    public int getCategoryId() {
        return this.categoryId;
    }
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
    public String getParentCategoryCode() {
        return this.parentCategoryCode;
    }
    public void setParentCategoryCode(String parentCategoryCode) {
        this.parentCategoryCode = parentCategoryCode;
    }
    public int getParentCategoryId() {
        return this.parentCategoryId;
    }
    public void setParentCategoryId(int parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
