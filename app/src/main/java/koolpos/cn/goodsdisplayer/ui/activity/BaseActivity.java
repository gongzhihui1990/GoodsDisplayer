package koolpos.cn.goodsdisplayer.ui.activity;

import android.content.pm.ActivityInfo;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;

import org.reactivestreams.Subscription;

import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import koolpos.cn.goodsdisplayer.util.Device;

/**
 * Created by Administrator on 2017/5/13.
 */

public class BaseActivity extends AppCompatActivity {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
        setPortOrLand();
    }



    private void setPortOrLand(){
        if (Device.isPAD()){
            setLand();
        }else {
            setLand();
//            setPort();
        }
    }

    private void setPort(){
        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    private void setLand(){
        /**
         * 设置为横屏
         */
        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    /**
     * onDestroy时调用此方法
     *  切断此Activity中的观察者容器中包含的观察者
     */
    private void clearWorkOnDestroy() {
        //disposable clear
        compositeDisposable.clear();
    }

    /**
     * 添加disposable到Activity生命周期，Activity销毁时候，disposable执行dispose
     * @param disposable
     */
    public void addDisposable(Disposable disposable) {
        compositeDisposable.add(disposable);
    }
    /**
     * 删除disposable到Activity生命周期
     * @param disposable
     */
    public void removeDisposable(Disposable disposable) {
        compositeDisposable.remove(disposable);
    }

}
