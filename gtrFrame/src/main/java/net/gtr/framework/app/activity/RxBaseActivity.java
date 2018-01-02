/*
 * Copyright (c) 2017. heisenberg.gong
 */

package net.gtr.framework.app.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import net.gtr.framework.rx.ApplicationObserverHolder;

import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

@SuppressLint({"InflateParams", "HandlerLeak"})
public abstract class RxBaseActivity extends BaseFragmentActivity implements ApplicationObserverHolder {

    //observer 观察者管理
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private List<Subscription> compositeSubscription = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    public void addFragment(int layout, Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        try {
            transaction.replace(layout, fragment).commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addFragment(int layout, Fragment fragment, boolean isSave) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(layout, fragment);
        if (isSave) {
            transaction.addToBackStack(null);
        }
        transaction.commitAllowingStateLoss();
    }

    /**
     * 清除FragmentManager内的Fragment层级到指定个数
     *
     * @param popLevel popLevel
     */

    protected void popFragmentToLevel(int popLevel) {
        FragmentManager manager = getSupportFragmentManager();
        while (manager.getBackStackEntryCount() >= popLevel) {
            manager.popBackStackImmediate();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        clearWorkOnDestroy();
        super.onDestroy();
    }

    /**
     * onDestroy时调用此方法
     * 切断此Activity中的观察者容器中包含的观察者
     */
    private void clearWorkOnDestroy() {
        //disposable clear
        compositeDisposable.clear();
        //subscription clear
        for (Subscription subscription : compositeSubscription) {
            if (subscription != null)
                subscription.cancel();
            subscription = null;
        }
        compositeSubscription.clear();
    }

    /**
     * 添加disposable到Activity生命周期，Activity销毁时候，disposable执行dispose
     *
     * @param disposable disposable
     */
    @Override
    public void addDisposable(Disposable disposable) {
        if (compositeDisposable == null) {
            return;
        }
        compositeDisposable.add(disposable);
    }

    /**
     * 类似 addSubscription(Disposable disposable)
     *
     * @param subscription subscription
     */
    @Override
    public void addSubscription(Subscription subscription) {
        if (compositeSubscription == null) {
            return;
        }
        compositeSubscription.add(subscription);
    }

    @Override
    public void removeDisposable(Disposable disposable) {
        if (compositeDisposable == null) {
            return;
        }
        compositeDisposable.remove(disposable);
    }

    @Override
    public void removeSubscription(Subscription subscription) {
        if (compositeSubscription == null) {
            return;
        }
        compositeSubscription.remove(subscription);
    }


    @Override
    public void showDialog(Object o) {

    }

    @Override
    public Context getContext() {
        return this;
    }
}