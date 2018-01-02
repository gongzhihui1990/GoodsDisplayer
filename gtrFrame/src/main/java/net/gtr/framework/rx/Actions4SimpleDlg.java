/*
 * Copyright (c) 2017. heisenberg.gong
 */

package net.gtr.framework.rx;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.SpannableString;
import android.view.View;
import android.view.View.OnClickListener;

import java.io.Serializable;

/**
 * 对话框展示参数
 * heisenberg
 */
public class Actions4SimpleDlg implements Serializable ,Parcelable{
	private static final long serialVersionUID = 3889542938169910637L;
	public transient boolean cancelable =true;
	public transient View layoutView=null;
	public transient String title=null;
	public transient String message=null;
	public transient int textColorRes=android.R.color.black;
	public transient OnClickListener confirmListener;
	public transient OnClickListener cancelListener;
	public transient OnClickListener moreListener;
	public transient String confirmBtnText = null;
	public transient String cancelBtnText = null;
	public transient String moreText=null;
	public transient String isNeedCanceBtn;
	public transient SpannableString spannableString;
	
	public Actions4SimpleDlg(){
		
	}
	
	public Actions4SimpleDlg(Parcel in){
		
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		
	}
	
	public static final Creator<Actions4SimpleDlg> CREATOR = new Creator<Actions4SimpleDlg>() {
	    @Override
	    public Actions4SimpleDlg[] newArray(int size) {
	        // TODO Auto-generated method stub
	        return new Actions4SimpleDlg[size];
	    }

	    @Override
	    public Actions4SimpleDlg createFromParcel(Parcel source) {
	        // TODO Auto-generated method stub
	        return new Actions4SimpleDlg(source);
	    }
	};
}