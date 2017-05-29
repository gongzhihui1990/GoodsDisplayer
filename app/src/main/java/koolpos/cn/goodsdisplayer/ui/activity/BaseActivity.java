package koolpos.cn.goodsdisplayer.ui.activity;

import android.content.pm.ActivityInfo;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import koolpos.cn.goodsdisplayer.util.Device;

/**
 * Created by Administrator on 2017/5/13.
 */

public class BaseActivity extends AppCompatActivity {
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
}
