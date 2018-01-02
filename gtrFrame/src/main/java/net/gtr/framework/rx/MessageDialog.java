/*
 * Copyright (c) 2017. heisenberg.gong
 */

package net.gtr.framework.rx;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import hsbank.com.gtrframe.R;

/**
 * Created by heisenberg on 2017/10/20.
 * heisenberg.gong@koolpos.com
 */

class MessageDialog {
    private final TextView title_tip;
    private final TextView message_tip;
    private final Button confirm_btn;
    private final Button cancel_btn;
    private final Dialog messageDialog;
    private View mDialogView;

    private boolean mCancelable = false;
    private CharSequence mMessage;
    private CharSequence mTitle;
    private CharSequence mConfirm;
    private CharSequence mCancel;

    private View.OnClickListener confirmButtonOnClickListener;

    MessageDialog(Context context) {
        // 首先得到整个View
        mDialogView = LayoutInflater.from(context).inflate(
                R.layout.message_dialog_view, null);
        // 获取整个布局
        LinearLayout layout = (LinearLayout) mDialogView.findViewById(R.id.dialog_view);
        // 页面中显示文本
        title_tip = (TextView) mDialogView.findViewById(R.id.title_tip);
        message_tip = (TextView) mDialogView.findViewById(R.id.message_tip);
        confirm_btn = (Button) mDialogView.findViewById(R.id.message_btn_01);
        cancel_btn = (Button) mDialogView.findViewById(R.id.message_btn_02);
        // 创建自定义样式的Dialog
        messageDialog = new Dialog(context, R.style.loading_dialog);
        messageDialog.setContentView(layout, new LinearLayout.LayoutParams(
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

    public void setConfirmButtonText(CharSequence mConfirm) {
        this.mConfirm = mConfirm;
    }

    public void setConfirmButtonOnClickListener(View.OnClickListener onClickListener) {
        this.confirmButtonOnClickListener = onClickListener;
    }

    public void setCancelButtonText(CharSequence mCancel) {
        this.mCancel = mCancel;
    }


    boolean isShowing() {
        return messageDialog.isShowing();
    }

    public void show() {
        title_tip.setText(mTitle);
        message_tip.setText(mMessage);
        if (!TextUtils.isEmpty(mConfirm)) {
            confirm_btn.setText(mConfirm);
        }
        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (confirmButtonOnClickListener != null) {
                    confirmButtonOnClickListener.onClick(v);
                }
            }
        });
        cancel_btn.setVisibility(View.GONE);
        cancel_btn.setText(mCancel);
        messageDialog.setCancelable(mCancelable);
        messageDialog.show();
    }

    public void dismiss() {
        messageDialog.dismiss();
    }

}
