package koolpos.cn.goodsdisplayer.mvcModel;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/5/13.
 */

public class GoodType implements Serializable{

    private String typeName;
    private boolean isChecked;

    public GoodType(String typeName){
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

    public boolean equals(GoodType obj) {
        return obj.getTypeName().equals(this.getTypeName());
    }
}
