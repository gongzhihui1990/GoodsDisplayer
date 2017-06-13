package koolpos.cn.goodsdisplayer;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.IBinder;

import org.json.JSONObject;

import koolpos.cn.goodproviderservice.service.aidl.IGPService;
import koolpos.cn.goodsdisplayer.api.AidlApi;
import koolpos.cn.goodsdisplayer.mvcModel.AIDLSetting;
import koolpos.cn.goodsdisplayer.util.AndroidUtils;

/**
 * Created by Administrator on 2017/5/13.
 */

public class MyApplication extends Application {

    private static MyApplication instance;
    public static Bitmap CacheBitmap;

    public static Context getContext() {
        return instance.getBaseContext();
    }

    public static AidlApi AIDLApi;
    public static AIDLSetting AIDLSettting;

    public static JSONObject PATHJson;
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
    public static Typeface FZ_GBK;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Intent serviceIntent = new Intent(IGPService.class.getName());
        serviceIntent = AndroidUtils.getExplicitIntent(getBaseContext(), serviceIntent);
        boolean bindService = bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);

               /*
                     * 必须事先在assets底下创建一fonts文件夹 并放入要使用的字体文件(.ttf)
                     * 并提供相对路径给creatFromAsset()来创建Typeface对象
                     */
        FZ_GBK = Typeface.createFromAsset(getAssets(),
                "fonts/FZ_GBK.TTF");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (connection != null) {
            unbindService(connection);
        }
    }
}
