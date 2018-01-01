package koolpos.cn.goodsdisplayer.ui.activity;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import net.gtr.framework.app.activity.RxBaseActivity;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
import koolpos.cn.goodsdisplayer.constans.ImageEnum;
import koolpos.cn.goodsdisplayer.rxjava.ActivityObserver;
import koolpos.cn.goodsdisplayer.util.Loger;

/**
 * Created by Administrator on 2017/5/13.
 */

public class BaseActivity extends RxBaseActivity {


    List<View> listNeedKill = new ArrayList<>();
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private Disposable bugSubscribe;
    private boolean fullSet = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.MODEL.contains("kool")) {
            fullSet = false;
        }
        setRequestedOrientation(ActivityInfo
//     .SCREEN_ORIENTATION_LANDSCAPE);// 横屏
        .SCREEN_ORIENTATION_PORTRAIT);//竖屏
        startBug();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopBug();
        killerImage();
    }

    private void killerImage() {
        if (listNeedKill.size() == 0) {
            return;
        }
        for (View view : listNeedKill) {
            if (view instanceof ImageView) {
                ((ImageView) view).setImageResource(0);
            }
            view.setBackgroundResource(0);
        }
        listNeedKill.clear();
        System.gc();
    }

    protected void reBug() {
        stopBug();
        startBug();
    }

    private void startBug() {
        if (!fullSet) {
            return;
        }
        bugSubscribe = Observable.interval(0, 1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        Window window = getWindow();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                            window.getDecorView().setSystemUiVisibility(
                                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                            | View.SYSTEM_UI_FLAG_IMMERSIVE);
                            window.setStatusBarColor(Color.TRANSPARENT);
                            window.setNavigationBarColor(Color.TRANSPARENT);
                        } else {
                            window.getDecorView().setSystemUiVisibility(
                                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                            | View.SYSTEM_UI_FLAG_IMMERSIVE);
                        }


                    }
                });
    }

    private void stopBug() {
        if (!fullSet) {
            return;
        }
        if (bugSubscribe == null) {
            return;
        }
        bugSubscribe.dispose();
    }


    protected final void setImageDrawableFromSD(final ImageView view, final ImageEnum imageEnum) {
        listNeedKill.add(view);
        Observable.just(MyApplication.PATHJson)
                .filter(new Predicate<JSONObject>() {
                    @Override
                    public boolean test(@NonNull JSONObject pathJson) throws Exception {
                        if (pathJson == null) {
                            return false;
                        }
                        String path = pathJson.optString(imageEnum.name());
                        if (TextUtils.isEmpty(path)) {
                            return false;
                        }
                        return true;
                    }
                }).map(new Function<JSONObject, File>() {
            @Override
            public File apply(@NonNull JSONObject pathJson) throws Exception {
                String path = pathJson.optString(imageEnum.name());
                File file = new File(path);
                Loger.d("load from path " + path + "-" + file.length());
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
                                .into(view);
                    }
                });
    }

    protected final void setBackgroundDrawableFromSD(final View view, final ImageEnum imageEnum) {
        listNeedKill.add(view);
        Observable.just(MyApplication.PATHJson)
                .filter(new Predicate<JSONObject>() {
                    @Override
                    public boolean test(@NonNull JSONObject pathJson) throws Exception {
                        if (pathJson == null) {
                            return false;
                        }
                        String path = pathJson.optString(imageEnum.name());
                        if (TextUtils.isEmpty(path)) {
                            return false;
                        }
                        return true;
                    }
                }).map(new Function<JSONObject, File>() {
            @Override
            public File apply(@NonNull JSONObject pathJson) throws Exception {
                String path = pathJson.optString(imageEnum.name());
                File file = new File(path);
                Loger.d("load from path " + path + "-" + file.length());
                return file;
            }
        }).map(new Function<File, Drawable>() {
            @Override
            public Drawable apply(@NonNull File file) throws Exception {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                Drawable drawable = new BitmapDrawable(bitmap);
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

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
//        setPortOrLand();
    }


    private void setPortOrLand() {
//        if (Device.isPAD()) {
//            setLand();
//        } else {
//            setLand();
//        }
    }

    private void setPort() {
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    private void setLand() {
        /**
         * 设置为横屏
         */
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    /**
     * onDestroy时调用此方法
     * 切断此Activity中的观察者容器中包含的观察者
     */
    private void clearWorkOnDestroy() {
        //disposable clear
        compositeDisposable.clear();
    }

    /**
     * 添加disposable到Activity生命周期，Activity销毁时候，disposable执行dispose
     *
     * @param disposable
     */
    public void addDisposable(Disposable disposable) {
        compositeDisposable.add(disposable);
    }

    /**
     * 删除disposable到Activity生命周期
     *
     * @param disposable
     */
    public void removeDisposable(Disposable disposable) {
        compositeDisposable.remove(disposable);
    }

}
