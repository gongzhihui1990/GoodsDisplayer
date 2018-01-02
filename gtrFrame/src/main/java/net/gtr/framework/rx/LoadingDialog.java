/*
 * Copyright (c) 2017. heisenberg.gong
 */

package net.gtr.framework.rx;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import hsbank.com.gtrframe.R;

/**
 * Created by heisenberg on 2017/10/20.
 * heisenberg.gong@koolpos.com
 */

class LoadingDialog {
    private final TextView title_tip;
    private final TextView message_tip;
    private final Dialog loadingDialog;
    private View mDialogView;

    private boolean mCancelable = false;
    private CharSequence mMessage;
    private CharSequence mTitle;

    LoadingDialog(Context context) {
        // 首先得到整个View
        mDialogView = LayoutInflater.from(context).inflate(
                R.layout.loading_dialog_view, null);
        // 获取整个布局
        LinearLayout layout = (LinearLayout) mDialogView.findViewById(R.id.dialog_view);
        // 页面中显示文本
        title_tip = (TextView) mDialogView.findViewById(R.id.title_tip);
        message_tip = (TextView) mDialogView.findViewById(R.id.message_tip);
        // 创建自定义样式的Dialog
        loadingDialog = new Dialog(context, R.style.loading_dialog);
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
    }

    public void setCancelable(boolean mCancelable) {
        this.mCancelable = mCancelable;
    }

    public void setMessage(CharSequence mMessage) {
        this.mMessage = mMessage;
    }

    public void setTitle(CharSequence mTitle) {
        this.mTitle = mTitle;
    }

    boolean isShowing() {
        return loadingDialog.isShowing();
    }

    public void show() {
        title_tip.setText(mTitle);
        message_tip.setText(mMessage);
        loadingDialog.setCancelable(mCancelable);
        loadingDialog.show();
    }

    public void dismiss() {
        loadingDialog.dismiss();
    }

}
