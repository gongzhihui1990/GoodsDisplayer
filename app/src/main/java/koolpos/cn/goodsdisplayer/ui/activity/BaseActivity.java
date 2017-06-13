package koolpos.cn.goodsdisplayer.ui.activity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONObject;
import org.reactivestreams.Subscription;

import java.io.File;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import koolpos.cn.goodsdisplayer.MyApplication;
import koolpos.cn.goodsdisplayer.R;
import koolpos.cn.goodsdisplayer.constans.ImageEnum;
import koolpos.cn.goodsdisplayer.rxjava.ActivityObserver;
import koolpos.cn.goodsdisplayer.util.Device;
import koolpos.cn.goodsdisplayer.util.Loger;

/**
 * Created by Administrator on 2017/5/13.
 */

public class BaseActivity extends AppCompatActivity {


    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private Disposable bugSubscribe;

    private boolean fullSet;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startBug();

    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void startBug(){
        if (!fullSet){
            return;
        }
        bugSubscribe = Observable.interval(1, 10, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        findViewById(android.R.id.content).setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
                                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                    }
                });
    }
    private void stopBug(){
        if (!fullSet){
            return;
        }
        if (bugSubscribe==null){
            return;
        }
        bugSubscribe.dispose();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopBug();
    }

    protected final void setImageDrawableFromSD(final ImageView view, final ImageEnum imageEnum){
        Observable.just(MyApplication.PATHJson)
                .filter(new Predicate<JSONObject>() {
                    @Override
                    public boolean test(@NonNull JSONObject pathJson) throws Exception {
                        if (pathJson==null){
                            return false;
                        }
                        String path =pathJson.optString(imageEnum.name());
                        if (TextUtils.isEmpty(path)){
                            return false;
                        }
                        return true;
                    }
                }).map(new Function<JSONObject, File>() {
            @Override
            public File apply(@NonNull JSONObject pathJson) throws Exception {
                String path =pathJson.optString(imageEnum.name());
                File file =new File(path);
                Loger.d("load from path "+ path+"-"+file.length());
                return file;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ActivityObserver<File>(BaseActivity.this) {
                    @Override
                    public void onNext(File file) {
                        Glide.with(BaseActivity.this)
                                .load(file)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .fitCenter()
                                //.placeholder(R.mipmap.downloading)
                                //.error(R.mipmap.download_error)
                                .into(view);
                    }
                });
    }
    protected final void setBackgroundDrawableFromSD(final View view,final ImageEnum imageEnum){
        Observable.just(MyApplication.PATHJson)
                .filter(new Predicate<JSONObject>() {
                    @Override
                    public boolean test(@NonNull JSONObject pathJson) throws Exception {
                        if (pathJson==null){
                            return false;
                        }
                        String path =pathJson.optString(imageEnum.name());
                        if (TextUtils.isEmpty(path)){
                            return false;
                        }
                        return true;
                    }
                }).map(new Function<JSONObject, File>() {
            @Override
            public File apply(@NonNull JSONObject pathJson) throws Exception {
                String path =pathJson.optString(imageEnum.name());
                File file =new File(path);
                Loger.d("load from path "+ path+"-"+file.length());
                return file;
            }
        }).map(new Function<File, Drawable>() {
            @Override
            public Drawable apply(@NonNull File file) throws Exception {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                Drawable drawable=new BitmapDrawable(bitmap);
                return drawable;
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ActivityObserver<Drawable>(BaseActivity.this) {
                    @Override
                    public void onNext(Drawable fileDrawable) {
                        view.setBackgroundDrawable(fileDrawable);
                    }
                });
    }
//    protected final void setBackground(final View view, final ImageEnum imageEnum){
//        Observable.just(MyApplication.PATHJson)
//                .filter(new Predicate<JSONObject>() {
//                    @Override
//                    public boolean test(@NonNull JSONObject pathJson) throws Exception {
//                        if (pathJson==null){
//                            return false;
//                        }
//                        String path =pathJson.optString(imageEnum.name());
//                        if (TextUtils.isEmpty(path)){
//                            return false;
//                        }
//                        return true;
//                    }
//                }).map(new Function<JSONObject, File>() {
//            @Override
//            public File apply(@NonNull JSONObject pathJson) throws Exception {
//                String path =pathJson.optString(imageEnum.name());
//                File fileSrc =new File(path);
//                return fileSrc;
//            }
//        }).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new ActivityObserver<File>(BaseActivity.this) {
//                    @Override
//                    public void onNext(File file) {
//                        Glide.with(BaseActivity.this)
//                                .load(file)
//                                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                                //.placeholder(R.mipmap.downloading)
//                                //.error(R.mipmap.download_error)
//                                .into(view);
//                    }
//                });
//    }
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
