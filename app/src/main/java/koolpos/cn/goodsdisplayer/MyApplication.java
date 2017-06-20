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

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import org.json.JSONObject;

import java.text.SimpleDateFormat;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import koolpos.cn.goodproviderservice.service.aidl.IGPService;
import koolpos.cn.goodsdisplayer.api.AidlApi;
import koolpos.cn.goodsdisplayer.constans.Action;
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
    private void checkServerState(){
        try {
            if (AIDLApi==null){
               throw new Exception("服务程序连接失败");
            }
            if (AIDLApi.isServerStateOk()) {
                getContext().sendBroadcast(new Intent(Action.State_Ok));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Observable.just(e).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Exception>() {
                        @Override
                        public void accept(@NonNull Exception e) throws Exception {
                            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            IGPService gpService = IGPService.Stub.asInterface(iBinder);
            AIDLApi = new AidlApi(gpService);
            checkServerState();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Toast.makeText(getContext(), "服务程序连接断开,请重启应用", Toast.LENGTH_LONG).show();
            canInit = true;
        }
    };
    public static Typeface FZ_GBK;
    private RefWatcher refWatcher;
    public static RefWatcher getRefWatcher(Context context) {
        MyApplication application = (MyApplication) context.getApplicationContext();
        return application.refWatcher;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        //refWatcher=LeakCanary.install(MyApplication.this);
        //LeakCanary.enableDisplayLeakActivity(this);

//        initApp();
    }

    private boolean canInit = true;

    public void initApp() {
        Loger.d("initApp canInit " + canInit);
        if (!canInit) {
            checkServerState();
            return;
        }
        canInit = false;
        Intent serviceIntent = new Intent(IGPService.class.getName());
        serviceIntent = AndroidUtils.getExplicitIntent(getBaseContext(), serviceIntent);
        try {
            bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
        } catch (Exception e) {
            Toast.makeText(this, "请安装服务程序后再使用", Toast.LENGTH_LONG).show();
            canInit = true;
        }
        FZ_GBK = Typeface.createFromAsset(getAssets(), "fonts/FZ_GBK.TTF");
    }

    public void killQuit() {
        ActivityManager activityMgr = (ActivityManager) getContext().getSystemService(ACTIVITY_SERVICE);
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
