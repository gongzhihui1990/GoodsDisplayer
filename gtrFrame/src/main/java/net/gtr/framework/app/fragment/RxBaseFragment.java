/*
 * Copyright (c) 2017. heisenberg.gong
 */

package net.gtr.framework.app.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.gtr.framework.app.activity.RxBaseActivity;
import net.gtr.framework.rx.ApplicationObserverHolder;
import net.gtr.framework.util.Loger;

import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class RxBaseFragment extends Fragment implements ApplicationObserverHolder {
    protected RxBaseActivity mActivity;
    protected List<Subscription> compositeSubscription = new ArrayList<>();
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public FragmentManager getSupportFragmentManager() {
        return getFragmentManager();
    }

    @Override
    public void showDialog(Object o) {
        //TODO
    }

    protected RxBaseActivity getBaseActivity() {
        return mActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected View findViewById(@IdRes int id) {
        return getView().findViewById(id);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container != null) {
            ButterKnife.bind(this, container);
        }
        return container;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = (RxBaseActivity) activity;
    }


    public void addFragment(int layout, Fragment fragment) {
        FragmentManager manager = getChildFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(layout, fragment).commitAllowingStateLoss();
    }

    public void addFragment(int layout, Fragment fragment, boolean isSave) {
        FragmentManager manager = getChildFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(layout, fragment);
        if (isSave) {
            transaction.addToBackStack(null);
        }
        transaction.commitAllowingStateLoss();
    }

    public void addSupportFragment(int layout, Fragment fragment) {
        FragmentManager manager = mActivity.getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(layout, fragment).commitAllowingStateLoss();
    }

    @Override
    public void onDestroy() {
        clearWorkOnDestroy();
        super.onDestroy();
    }

    /**
     * onDestroy时调用此方法
     * 切断此Activity中的观察者容器中包含的观察者
     */
    public void clearWorkOnDestroy() {
        //disposable clear
        compositeDisposable.clear();

        for (Subscription subscription : compositeSubscription) {
            if (subscription != null){
                subscription.cancel();
            }
        }
        compositeSubscription.clear();
    }

    /**
     * 添加disposable到Activity生命周期，Activity销毁时候，disposable执行dispose
     *
     * @param disposable
     */
    public void addDisposable(Disposable disposable) {
        if (compositeDisposable == null) {
            return;
        }
        compositeDisposable.add(disposable);
    }

    /**
     * 类似 addSubscription(Disposable disposable)
     *
     * @param subscription
     */
    public void addSubscription(Subscription subscription) {
        compositeSubscription.add(subscription);
    }

    public void removeDisposable(Disposable disposable) {
        if (compositeDisposable == null) {
            return;
        }
        compositeDisposable.remove(disposable);
    }

    public void removeSubscription(Subscription subscription) {
        if (compositeSubscription == null) {
            return;
        }
        compositeSubscription.remove(subscription);
    }

    /**
     * 得到根Fragment
     *
     * @return
     */
    private Fragment getRootFragment() {
        Fragment fragment = this;
        while (fragment.getParentFragment() != null) {
            fragment = fragment.getParentFragment();
        }
        return fragment;

    }
}
