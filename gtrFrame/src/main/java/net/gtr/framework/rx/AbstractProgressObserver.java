/*
 * Copyright (c) 2017. heisenberg.gong
 */

package net.gtr.framework.rx;


import com.tencent.bugly.crashreport.CrashReport;

import net.gtr.framework.util.Loger;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by heisenberg on 2017/4/18.
 * 实现了Observer和Subscriber的观察者
 * 保护不让外部包的类构造使用ProgressObserver
 */

abstract class AbstractProgressObserver<T> implements Observer<T>, Subscriber<T> {

    /**
     * 显示进度条对话框
     */
    protected abstract void showProgress();
    /**
     * 隐藏进度条对话框
     */
    protected abstract void dismissProgress();

    private static final boolean DEBUG_TAG = true;
    private Disposable disposable;
    private Subscription subscription;
    private ObserverHolder mHolder;
    private long requestVar1 = 1;

    /**
     * {@link AbstractProgressObserver#onSubscribe(Subscription)}
     * set var1 used for : {@link Subscription#request(long)} 's param
     *
     * @param var1 请求次数
     * @return AbstractProgressObserver
     */
    public AbstractProgressObserver<T> setSubscribeRequest(long var1) {
        requestVar1 = var1;
        return this;
    }

    @Override
    public void onSubscribe(Disposable d) {
        if (DEBUG_TAG){
            Loger.d("---");
        }
        disposable = d;
        onBeginInternal();
    }

    @Override
    public void onSubscribe(Subscription s) {
        if (DEBUG_TAG){
            Loger.d("---");
        }
        subscription = s;
        s.request(requestVar1);
        onBeginInternal();
    }

    @Override
    public void onNext(T t) {
        if (DEBUG_TAG){
            Loger.d("---");
        }
    }

    @Override
    public void onError(Throwable t) {
        if (DEBUG_TAG){
            Loger.d("---");
            t.printStackTrace();
        }
        CrashReport.postCatchedException(t);
        onReleaseInternal();
    }

    @Override
    public void onComplete() {
        if (DEBUG_TAG){
            Loger.d("---");
        }
        onReleaseInternal();
    }

    final void setObserverHolder(ObserverHolder observerHolder) {
        mHolder = observerHolder;
    }

    private void onBeginInternal() {
        onBegin();
        showProgress();
        if (mHolder == null) {
            return;
        }
        if (disposable != null) {
            mHolder.addDisposable(disposable);
        }
        if (subscription != null) {
            mHolder.addSubscription(subscription);
        }
    }

    private void onReleaseInternal() {
        onRelease();
        dismissProgress();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
        if (subscription != null) {
            subscription.cancel();
        }
        if (mHolder == null) {
            return;
        }
        if (disposable != null) {
            mHolder.removeDisposable(disposable);
        }
        if (subscription != null) {
            mHolder.removeSubscription(subscription);
        }
    }

    /**
     * 开始执行任务，可以在此执行想要的操作
     * onSubscribe
     */

    protected void onBegin() {
        if (DEBUG_TAG){
            Loger.d("---");
        }
    }

    /**
     * 结束执行任务，可以在结束显示
     * onError onComplete
     */
    protected void onRelease() {
        if (DEBUG_TAG){
            Loger.d("---");
        }
    }
}
