package koolpos.cn.goodsdisplayer.ui.activity;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import koolpos.cn.goodsdisplayer.BuildConfig;
import koolpos.cn.goodsdisplayer.MyApplication;
import koolpos.cn.goodsdisplayer.R;
import koolpos.cn.goodsdisplayer.api.AidlApi;
import koolpos.cn.goodsdisplayer.constans.Action;
import koolpos.cn.goodsdisplayer.mvcModel.AIDLSetting;
import koolpos.cn.goodsdisplayer.rxjava.ActivityObserver;
import koolpos.cn.goodsdisplayer.util.Loger;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.ACCESS_WIFI_STATE;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by Administrator on 2017/5/15.
 */

public class SplashActivity extends BaseActivity {
    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_PERMISSIONS = 0;
    @BindView(R.id.tv_hint_common)
    TextView tv_hint_common;
    @BindView(R.id.tv_version)
    TextView tv_version;
    int timeMs = 0;
    boolean inRequestPermission = false;
    private boolean initFlag = false;
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Action.State_Ok.equals(intent.getAction())) {
                //initByOnCreate();
                Loger.d("initApplication");
                initApplication();
            }
        }
    };

    private void initApplication() {
        if (initFlag) {
            return;
        }
        initFlag = true;
        if (goRequestPermission()) {
            //need request permission,after that can do
            Loger.d("need request permission,after that can do");
            return;
        }
        Loger.d("permission get success");
        Observable<Boolean> ob1 = Observable.just(MyApplication.AIDLApi)
                .map(new Function<AidlApi, JSONObject>() {
                    @Override
                    public JSONObject apply(@io.reactivex.annotations.NonNull AidlApi aidlApi) throws Exception {
                        return aidlApi.getImageSrcPaths();
                    }
                }).map(new Function<JSONObject, Boolean>() {
                    @Override
                    public Boolean apply(@io.reactivex.annotations.NonNull JSONObject pathJson) throws Exception {
                        MyApplication.PATHJson = pathJson;
                        return true;
                    }
                });
        Observable<Boolean> ob2 = Observable.just(MyApplication.AIDLApi)
                .map(new Function<AidlApi, AIDLSetting>() {
                    @Override
                    public AIDLSetting apply(@io.reactivex.annotations.NonNull AidlApi aidlApi) throws Exception {
                        return aidlApi.getAIDLSetting();
                    }
                }).map(new Function<AIDLSetting, Boolean>() {
                    @Override
                    public Boolean apply(@io.reactivex.annotations.NonNull AIDLSetting setting) throws Exception {
                        MyApplication.AIDLSet = setting;
                        return true;
                    }
                });
        Observable.zip(ob1, ob2, new BiFunction<Boolean, Boolean, Boolean>() {
            @Override
            public Boolean apply(@io.reactivex.annotations.NonNull Boolean aBoolean, @io.reactivex.annotations.NonNull Boolean aBoolean2) throws Exception {
                return true;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ActivityObserver<Boolean>(SplashActivity.this) {
                    @Override
                    public void onNext(Boolean pathJson) {
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        Observable.just(e)
                                .delay(2, TimeUnit.SECONDS)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Consumer<Throwable>() {
                                    @Override
                                    public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception {
                                        finish();
                                    }
                                });
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                        populateAutoComplete();
                    }
                });
    }

    private void initByOnCreate() {
        if (goRequestPermission()) {
            //need request permission,after that can do
            Loger.d("need request permission,after that can do");
            return;
        }
        Observable.timer(1, TimeUnit.SECONDS)
                .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(@io.reactivex.annotations.NonNull Long aLong) throws Exception {
                        MyApplication.getInstance().initApp();
                        return aLong;
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe();
        Observable.interval(0, 1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .map(new Function<Long, Integer>() {
                    @Override
                    public Integer apply(@io.reactivex.annotations.NonNull Long aLong) throws Exception {
                        return 45;
                    }
                }).map(new Function<Integer, Integer>() {
            @Override
            public Integer apply(@io.reactivex.annotations.NonNull Integer duration) throws Exception {
                int totalSeconds = duration;
                int curSeconds = timeMs++;
                int remainSeconds = totalSeconds - curSeconds;
                return remainSeconds;
            }
        }).filter(new Predicate<Integer>() {
            @Override
            public boolean test(@io.reactivex.annotations.NonNull Integer remainSeconds) throws Exception {
                if (remainSeconds >= 0) {
                    return true;
                }
                return false;
            }
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(@io.reactivex.annotations.NonNull Integer remainSeconds) throws Exception {
                Loger.e("remainSeconds:" + remainSeconds);
                if (remainSeconds == 0) {
                    if (MyApplication.AIDLApi == null) {
                        finish();
                        return;
                    }
                    initApplication();
                } else {
                    tv_hint_common.setText("服务初始化中，请稍等" + remainSeconds + "...");
                }

            }
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiver(receiver, new IntentFilter(Action.State_Ok));
        setContentView(R.layout.activity_splash);
        tv_version.setText(BuildConfig.Release_Info);
        initByOnCreate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean goRequestPermission_SDK_M() {
        final String[] permissions = {
                INTERNET,
                WRITE_EXTERNAL_STORAGE,
                ACCESS_NETWORK_STATE,
                ACCESS_WIFI_STATE,
                /*READ_PHONE_STATE*/};

        boolean needRequest = false;
        for (String permission : permissions) {
            boolean needRequestTmp = checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED;
            needRequest |= needRequestTmp;
            Loger.d("need " + permission + ": " + needRequestTmp);
            Loger.d("permissions: " + needRequest);
        }
        if (!needRequest) {
            return false;
        }
        Loger.d("needRequest permissions");

        boolean shouldShowRequest = false;
        for (String permission : permissions) {
            if (shouldShowRequestPermissionRationale(permission)) {
                shouldShowRequest = true;
                break;
            }
        }
        if (shouldShowRequest) {
            Loger.d("shouldShowRequestPermissionRationale");
            Snackbar.make(tv_hint_common, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            Loger.d("requestPermissions");
                            requestPermissions(permissions, REQUEST_PERMISSIONS);
                        }
                    }).show();
        } else {
            Loger.d("requestPermissions");
            requestPermissions(permissions, REQUEST_PERMISSIONS);
        }
        return true;
    }

    private boolean goRequestPermission() {
        if (inRequestPermission) {
            return true;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Loger.d("checkSelfPermission success");
            return false;
        }
        return goRequestPermission_SDK_M();

    }

    private void populateAutoComplete() {
        Loger.d("populateAutoComplete " + goRequestPermission());
        toMainPage();
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Loger.d("onRequestPermissionsResult:" + permissions.length);
        if (requestCode == REQUEST_PERMISSIONS) {
            inRequestPermission = false;
            boolean grantsSuccess = true;
            int i = 0;
            Loger.d("grantsSuccess:" + grantsSuccess);
            for (int grantResult : grantResults) {
                boolean grantSuccess = grantResult == PackageManager.PERMISSION_GRANTED;
                Loger.d(permissions[i] + ":" + grantSuccess);
                grantsSuccess &= grantSuccess;
                Loger.d("grantsSuccess:" + grantsSuccess);
                i++;
            }
            if (grantsSuccess) {
                initByOnCreate();
            } else {
                finish();
            }
        }
    }

    private void toMainPage() {
        startActivity(new Intent(getBaseContext(), MainActivity.class));
        finish();
    }
}
