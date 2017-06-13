package koolpos.cn.goodsdisplayer.rxjava;

import android.os.TransactionTooLargeException;
import android.widget.Toast;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import koolpos.cn.goodsdisplayer.MyApplication;
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
        e.printStackTrace();
        if (e instanceof TransactionTooLargeException){
            Toast.makeText(MyApplication.getContext(),"数据过大,请重试",Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(MyApplication.getContext(),"内部错误："+e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onComplete() {
        if (disposable != null && mBaseActivity != null) {
            mBaseActivity.removeDisposable(disposable);
        }
    }
}
