package koolpos.cn.goodsdisplayer;

import android.app.Application;
import android.content.Context;

/**
 * Created by Administrator on 2017/5/13.
 */

public class MyApplication extends Application{

    private static MyApplication instance;
    public static Context getContext() {
      return instance.getBaseContext();
    }
    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
    }
}
