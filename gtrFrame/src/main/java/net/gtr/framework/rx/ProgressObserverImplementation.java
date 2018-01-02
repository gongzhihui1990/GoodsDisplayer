/*
 * Copyright (c) 2017. heisenberg.gong
 */

package net.gtr.framework.rx;

import android.content.Context;
import android.support.annotation.StringRes;
import android.view.KeyEvent;
import android.view.View;

import net.gtr.framework.app.BaseApp;

import io.reactivex.annotations.NonNull;

/**
 * AbstractProgressObserver 的基础实现
 *
 * @author heisenberg
 * @date 2017/4/18
 */
public class ProgressObserverImplementation<T> extends AbstractProgressObserver<T> {

    private MessageDialog msgDialog;
    private LoadingDialog pDialog;
    private boolean mCancelable;
    private boolean mShow = true;
    private CharSequence pMessage;
    private Context context;

    /**
     * @param applicationObserverHolder ApplicationObserverHolder
     */
    public ProgressObserverImplementation(@NonNull ApplicationObserverHolder applicationObserverHolder) {
        if (applicationObserverHolder != null) {
            setObserverHolder(applicationObserverHolder);
            context = applicationObserverHolder.getContext();
            pDialog = new LoadingDialog(applicationObserverHolder.getContext());
            pDialog.setTitle("提示");
            pMessage = "加载中...";
            pDialog.setCancelable(mCancelable);
        }
    }

    @Deprecated
    public ProgressObserverImplementation() {
    }

    /**
     * Sets whether this dialog is cancelable with the
     * {@link KeyEvent#KEYCODE_BACK BACK} key.
     */
    public ProgressObserverImplementation<T> setCancelable(boolean flag) {
        mCancelable = flag;
        return this;
    }

    public ProgressObserverImplementation<T> setMessage(CharSequence message) {
        pMessage = message;
        return this;
    }

    public ProgressObserverImplementation<T> setMessage(@StringRes int messageID) {
        pMessage = BaseApp.getContext().getString(messageID);
        return this;
    }

    public ProgressObserverImplementation<T> setShow(boolean show) {
        mShow = show;
        return this;
    }

    private boolean checkDialog(){
        if (getContext() == null){
            return false;
        }
        if (msgDialog != null) {
            msgDialog.dismiss();
        }else {
            msgDialog = new MessageDialog(getContext());
        }
        return true;

    }
    public void setDialogConfirmBtn(CharSequence btnText, View.OnClickListener onClickListener){
        if (checkDialog()){
            msgDialog.setConfirmButtonText(btnText);
            msgDialog.setConfirmButtonOnClickListener(onClickListener);
        }
    }
    public void showDialogByMessage(CharSequence message) {
        if (checkDialog()){
            msgDialog.setTitle("提示");
            msgDialog.setMessage(message);
            msgDialog.show();
        }
    }
    @Override
    protected void showProgress() {
        if (pDialog != null && mShow) {
            pDialog.setCancelable(mCancelable);
            pDialog.setMessage(pMessage);
            pDialog.show();
        }
    }

    @Override
    protected void dismissProgress() {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }

    public Context getContext() {
        return context;
    }
}
