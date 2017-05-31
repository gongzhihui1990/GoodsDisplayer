package koolpos.cn.goodsdisplayer;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by Administrator on 2017/5/13.
 */

public class MyApplication extends Application{

    private static MyApplication instance;
    public static Bitmap CacheBitmap;
    public static Context getContext() {
      return instance.getBaseContext();
    }
    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
    }
}
