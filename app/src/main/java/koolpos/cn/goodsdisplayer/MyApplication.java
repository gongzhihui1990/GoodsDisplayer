package koolpos.cn.goodsdisplayer;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.IBinder;

import koolpos.cn.goodproviderservice.service.aidl.IGPService;
import koolpos.cn.goodsdisplayer.api.AidlApi;
import koolpos.cn.goodsdisplayer.util.AndroidUtils;
import koolpos.cn.goodsdisplayer.util.Loger;

/**
 * Created by Administrator on 2017/5/13.
 */

public class MyApplication extends Application{

    private static MyApplication instance;
    public static Bitmap CacheBitmap;
    public static Context getContext() {
      return instance.getBaseContext();
    }
    public static AidlApi AIDLApi;

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            IGPService gpService = IGPService.Stub.asInterface(iBinder);
            AIDLApi = new AidlApi(gpService);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
        Intent serviceIntent = new Intent(IGPService.class.getName());
        serviceIntent = AndroidUtils.getExplicitIntent(getBaseContext(), serviceIntent);
        boolean bindService = bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (connection!=null){
            unbindService(connection);
        }
    }
}
