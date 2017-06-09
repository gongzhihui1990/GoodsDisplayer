package koolpos.cn.goodsdisplayer.rxjava;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import koolpos.cn.goodsdisplayer.ui.activity.BaseActivity;

/**
 * Created by Administrator on 2017/6/9.
 */

public abstract class ActivityObserver<T> implements Observer<T> {
    private BaseActivity mBaseActivity;

    public ActivityObserver(BaseActivity activity) {
        mBaseActivity = activity;
    }

    private Disposable disposable;

    @Override
    public void onSubscribe(Disposable d) {
        if (d != null && mBaseActivity != null) {
            disposable = d;
            mBaseActivity.addDisposable(disposable);
        }
    }

    @Override
    public void onError(Throwable e) {
        if (disposable != null && mBaseActivity != null) {
            mBaseActivity.removeDisposable(disposable);
        }
    }

    @Override
    public void onComplete() {
        if (disposable != null && mBaseActivity != null) {
            mBaseActivity.removeDisposable(disposable);
        }
    }
}
