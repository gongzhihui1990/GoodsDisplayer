package koolpos.cn.goodsdisplayer.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import koolpos.cn.goodsdisplayer.MyApplication;
import koolpos.cn.goodsdisplayer.R;
import koolpos.cn.goodsdisplayer.mvcModel.AdBean;
import koolpos.cn.goodsdisplayer.util.AndroidUtils;
import koolpos.cn.goodsdisplayer.util.Loger;

/**
 * Created by Administrator on 2017/6/3.
 */

public class AdImageDisplayActivity extends BaseActivity {
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.tvTimeRemain)
    TextView tvTimeRemain;
    @BindView(R.id.root_view)
    View root_view;
    private Disposable sub;


    int timeMs = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_image_play);

        final AdBean adBean = (AdBean) getIntent().getSerializableExtra(AdBean.class.getName());
        AndroidUtils.loadImageAnim(adBean.getFileurl(),imageView);
        sub = Observable.interval(0, 1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .map(new Function<Long, Integer>() {
                    @Override
                    public Integer apply(@NonNull Long aLong) throws Exception {
                        int ms = MyApplication.AIDLSet.getPlayLongAd()*1000;
                        return ms;
                    }
                }).filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(@NonNull Integer duration) throws Exception {
                        if (duration > 1000) {
                            return true;
                        }
                        return false;
                    }
                }).map(new Function<Integer, Integer>() {
                    @Override
                    public Integer apply(@NonNull Integer duration) throws Exception {
                        timeMs += 1000;
                        int totalSeconds = duration / 1000;
                        int curSeconds = timeMs / 1000;
                        int remainSeconds = totalSeconds - curSeconds;
                        return remainSeconds;
                    }
                }).filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(@NonNull Integer remainSeconds) throws Exception {
                        if (remainSeconds >= 0) {
                            return true;
                        }
                        return false;
                    }
                }).subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer remainSeconds) throws Exception {
                        Loger.e("remainSeconds:" + remainSeconds);
                        tvTimeRemain.setText("广告剩余：" + remainSeconds + "秒（点击关闭）");
                        if (remainSeconds==0){
                            finish();
                        }
                        root_view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                            }
                        });
                        tvTimeRemain.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                            }
                        });
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sub.dispose();
    }
}
