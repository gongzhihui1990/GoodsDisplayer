/*
 * Copyright (c) 2017. heisenberg.gong
 */

package net.gtr.framework.rx;

import org.reactivestreams.Subscription;

import io.reactivex.disposables.Disposable;

/**
 * Created by heisenberg on 2017/7/21.
 * heisenberg.gong@koolpos.com
 * 观察者容器
 */

interface ObserverHolder {
    /**
     * 为容器添加disposable
     * @param disposable
     */
    void addDisposable(Disposable disposable);
    /**
     *为容器添加subscription
     * @param subscription
     */
    void addSubscription(Subscription subscription);
    /**
     *为容器移除disposable
     * @param disposable
     */
    void removeDisposable(Disposable disposable);
    /**
     *为容器移除subscription
     * @param subscription
     */
    void removeSubscription(Subscription subscription);

}
