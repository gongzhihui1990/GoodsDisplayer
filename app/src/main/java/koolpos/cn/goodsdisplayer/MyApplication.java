package koolpos.cn.goodsdisplayer;

import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.IBinder;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import koolpos.cn.goodproviderservice.service.aidl.IGPService;
import koolpos.cn.goodsdisplayer.api.AidlApi;
import koolpos.cn.goodsdisplayer.mvcModel.AIDLSetting;
import koolpos.cn.goodsdisplayer.util.AndroidUtils;
import koolpos.cn.goodsdisplayer.util.Loger;

/**
 * Created by Administrator on 2017/5/13.
 */

public class MyApplication extends Application {

    private static MyApplication instance;
    public static Bitmap CacheBitmap;

    public static Context getContext() {
        return instance.getBaseContext();
    }

    public static MyApplication getInstance() {
        return instance;
    }

    public static AidlApi AIDLApi;
    public static AIDLSetting AIDLSet;

    public static JSONObject PATHJson;
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            IGPService gpService = IGPService.Stub.asInterface(iBinder);
            AIDLApi = new AidlApi(gpService);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Toast.makeText(getContext(),"服务程序连接断开,请重启应用",Toast.LENGTH_LONG).show();
            canInit=true;
        }
    };
    public static Typeface FZ_GBK;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
//        initApp();
    }

    private boolean canInit =true;
    public void initApp() {
        Loger.d("initApp canInit "+canInit);
        if (!canInit){
            return;
        }
        canInit=false;
        Intent serviceIntent = new Intent(IGPService.class.getName());
        serviceIntent = AndroidUtils.getExplicitIntent(getBaseContext(), serviceIntent);
        try{
            bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
        }catch (Exception e){
            Toast.makeText(this,"请安装服务程序后再使用",Toast.LENGTH_LONG).show();
            canInit=true;
        }
        FZ_GBK = Typeface.createFromAsset(getAssets(),"fonts/FZ_GBK.TTF");
    }
    public void killQuit(){
        ActivityManager activityMgr =(ActivityManager)getContext().getSystemService(ACTIVITY_SERVICE);
        activityMgr.killBackgroundProcesses(getPackageName());
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);

    }
    @Override
    public void onTerminate() {
        super.onTerminate();
        if (connection != null) {
            unbindService(connection);
        }
    }
}
