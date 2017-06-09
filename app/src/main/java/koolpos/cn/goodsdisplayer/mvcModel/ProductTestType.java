package koolpos.cn.goodsdisplayer.mvcModel;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/5/13.
 *  ProductType是商品下的一个分类属性
 */

public class ProductTestType implements Serializable{

    private String typeName;
    private boolean isChecked;

    public ProductTestType(String typeName){
        this.typeName=typeName;
    }
    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean equals(ProductTestType obj) {
        return obj.getTypeName().equals(this.getTypeName());
    }
}
