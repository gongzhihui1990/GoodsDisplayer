package koolpos.cn.goodsdisplayer.ui.activity;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import android.widget.VideoView;

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
import koolpos.cn.goodsdisplayer.R;
import koolpos.cn.goodsdisplayer.util.Loger;

/**
 * Created by Administrator on 2017/6/3.
 */

public class AdVideoDisplayActivity extends BaseActivity {
    @BindView(R.id.videoView)
    VideoView videoView;
    @BindView(R.id.tvTimeRemain)
    TextView tvTimeRemain;
    private Disposable sub;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adv_play);
        String uri = "android.resource://" + getPackageName() + "/" + R.raw.ad1;
        videoView.setVideoURI(Uri.parse((uri)));
        //videoView.setMediaController(new MediaController(LoginLogoActivity.this));  ／／添加控制台
        videoView.requestFocus();
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                finish();
            }
        });
        videoView.start();
        sub = Observable.interval(0, 1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .map(new Function<Long, Integer>() {
                    @Override
                    public Integer apply(@NonNull Long aLong) throws Exception {
                        int ms = videoView.getDuration();
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
                        int timeMs = videoView.getCurrentPosition();
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
