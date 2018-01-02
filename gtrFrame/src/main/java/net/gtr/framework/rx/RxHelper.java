/*
 * Copyright (c) 2017. heisenberg.gong
 */

package net.gtr.framework.rx;

import android.view.View;

import com.jakewharton.rxbinding2.view.RxView;

import org.reactivestreams.Subscriber;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 线程切换工具类
 * Created by heisenberg on 2017/6/23.
 */

public final class RxHelper {

    //bind Observable io
    private static <T> Observable<T> bindSameUI(@android.support.annotation.NonNull Observable<T> observable) {
        return observable.subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .onTerminateDetach();
    }

    //bind Observable io
    private static <T> Observable<T> bindNewThread(@android.support.annotation.NonNull Observable<T> observable) {
        return observable.subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .onTerminateDetach();
    }

    //bind Flowable io
    private static <T> Flowable<T> bindNewThread(@android.support.annotation.NonNull Flowable<T> flowable) {
        return flowable.subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .onTerminateDetach();
    }

    //bind Observable ui
    private static <T> Observable<T> bindUI(@android.support.annotation.NonNull Observable<T> observable) {
        return observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onTerminateDetach();
    }

    //bind Flowable ui
    private static <T> Flowable<T> bindUI(@android.support.annotation.NonNull Flowable<T> flowable) {
        return flowable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onTerminateDetach();
    }

    /**
     * 调用此方法：
     * 将被观察者给观察者于IO线程观察
     * 防止遗忘onTerminateDetach
     * 并简化代码
     *
     * @param observable the observable
     * @param <T>        the input type
     */
    public static <T> void bindOnNull(@android.support.annotation.NonNull Observable<T> observable) {
        bindNewThread(observable).subscribe(new ProgressObserverImplementation<T>());
    }

    /**
     * 调用此方法：
     * 将被观察者给观察者于IO线程观察
     * 防止遗忘onTerminateDetach
     * 并简化代码
     *
     * @param flowable the flowable
     * @param <T>      the input type
     */
    public static <T> void bindOnNull(@android.support.annotation.NonNull Flowable<T> flowable) {
        bindNewThread(flowable).subscribe(new ProgressObserverImplementation<T>());
    }

    /**
     * 调用此方法：
     * 将被观察者给观察者于UI线程观察
     * 防止遗忘onTerminateDetach
     * 并简化代码
     *
     * @param observable the observable
     * @param observer   the observer
     * @param <T>        the input type
     */
    public static <T> void bindOnUI(@android.support.annotation.NonNull Observable<T> observable, @android.support.annotation.NonNull Observer<T> observer) {
        bindUI(observable).subscribe(observer);
    }

    /**
     * 调用此方法：
     * 将被观察者给观察者于UI线程观察
     * 防止遗忘onTerminateDetach
     * 并简化代码
     *
     * @param observable the observable
     * @param observer   the observer
     * @param <T>        the input type
     */
    public static <T> void bindSameUI(@android.support.annotation.NonNull Observable<T> observable, @android.support.annotation.NonNull Observer<T> observer) {
        bindSameUI(observable).subscribe(observer);
    }

    /**
     * 调用此方法
     * 将被观察者给观察者于UI线程观察
     * 防止遗忘onTerminateDetach
     * 并简化代码
     *
     * @param flowable   the flowable
     * @param subscriber the subscriber
     * @param <T>        the input type
     */
    public static <T> void bindOnUI(@android.support.annotation.NonNull Flowable<T> flowable, @android.support.annotation.NonNull Subscriber<T> subscriber) {
        bindUI(flowable).subscribe(subscriber);
    }

    /**
     * 调用此方法
     * 按钮点击1秒内只能执行一次，防止连续点击
     *
     * @param view       view on click
     * @param observable the observable
     * @param observer   the observer
     * @param <T>        the input type
     */
    public static <T> void onClickOne(View view, final Observable<T> observable, final Observer<T> observer) {
        RxView.clicks(view).throttleFirst(1, TimeUnit.SECONDS).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                bindOnUI(observable, observer);
            }
        });
    }

    /**
     * 调用此方法
     * 按钮点击1秒内只能执行一次，防止连续点击
     *
     * @param view            view on click
     * @param onClickListener the onClickListener
     */
    public static void onClickOne(final View view, final View.OnClickListener onClickListener) {
        RxView.clicks(view).throttleFirst(1, TimeUnit.SECONDS).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                onClickListener.onClick(view);
            }
        });
    }

    /**
     * 调用此方法
     * 按钮点击1秒内只能执行一次，防止连续点击
     *
     * @param view       view on click
     * @param flowable   the flowable
     * @param subscriber the subscriber
     * @param <T>        the input type
     */
    public static <T> void onClickOne(View view, final Flowable<T> flowable, final Subscriber<T> subscriber) {
        RxView.clicks(view).throttleFirst(1, TimeUnit.SECONDS).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception {
                bindOnUI(flowable, subscriber);
            }
        });
    }

    /**
     * 倒计时
     *
     * @param time
     * @return
     */
    public static Observable<Integer> countdown(int time, long period, TimeUnit unit) {
        if (time < 0) time = 0;
        final int countTime = time;
        return Observable.interval(0, period, unit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<Long, Integer>() {//通过map()将0、1、2、3...的计数变为...3、2、1、0倒计时
                    @Override
                    public Integer apply(@NonNull Long increaseTime) throws Exception {
                        return countTime - increaseTime.intValue();
                    }
                }).take(countTime + 1);//通过take()取>=0的Observable
    }

    private static <T> Observable<T> bindSameUINotSchedule(@android.support.annotation.NonNull Observable<T> observable) {
        return observable.onTerminateDetach();
    }

    /**
     * 调用此方法：
     * 将被观察者给观察者于UI线程观察,不切换线程进度，使用默认线程
     * 防止遗忘onTerminateDetach
     * 并简化代码
     *
     * @param observable the observable
     * @param observer   the observer
     * @param <T>        the input type
     */
    public static <T> void bindSameUINotSchedule(@android.support.annotation.NonNull Observable<T> observable, @android.support.annotation.NonNull Observer<T> observer) {
        bindSameUINotSchedule(observable).subscribe(observer);
    }

}
