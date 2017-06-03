package koolpos.cn.goodsdisplayer.ui.activity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.VideoView;

import java.io.IOException;

import butterknife.BindView;
import koolpos.cn.goodsdisplayer.R;

/**
 * Created by Administrator on 2017/6/3.
 */

public class AdVideoDisplayActivity extends BaseActivity {
    @BindView(R.id.videoView)
    VideoView videoView;
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
        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                finish();
                return false;
            }
        });
    }
}
