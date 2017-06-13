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
import io.reactivex.schedulers.Schedulers;
import koolpos.cn.goodsdisplayer.MyApplication;
import koolpos.cn.goodsdisplayer.R;
import koolpos.cn.goodsdisplayer.api.AidlApi;
import koolpos.cn.goodsdisplayer.mvcModel.AIDLSetting;
import koolpos.cn.goodsdisplayer.rxjava.ActivityObserver;
import koolpos.cn.goodsdisplayer.util.Loger;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.ACCESS_WIFI_STATE;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by Administrator on 2017/5/15.
 */

public class SplashActivity extends BaseActivity {
    @BindView(R.id.tv_hint_common)
    TextView tv_hint_common;

    private boolean initFlag=false;
    BroadcastReceiver receiver =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("service.state.ok".equals(intent.getAction())){
                initDataFromAIDL();
            }
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiver(receiver,new IntentFilter("service.state.ok"));
        setContentView(R.layout.activity_splash);
        MyApplication.getInstance().initApp();
        Observable.timer(5, TimeUnit.SECONDS)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull Long aLong) throws Exception {
                        //"service.state.ok"
                        if (MyApplication.AIDLApi==null){
                            finish();
                            return;
                        }
                        initDataFromAIDL();
                    }
                });
    }

    private void initDataFromAIDL() {
        if (initFlag){
            return;
        }
        initFlag=true;
        Observable<Boolean> ob1= Observable.just(MyApplication.AIDLApi)
                .map(new Function<AidlApi, JSONObject>() {
                    @Override
                    public JSONObject apply(@io.reactivex.annotations.NonNull AidlApi aidlApi) throws Exception {
                        return aidlApi.getImageSrcPaths();
                    }
                }).map(new Function<JSONObject, Boolean>() {
                    @Override
                    public Boolean apply(@io.reactivex.annotations.NonNull JSONObject pathJson) throws Exception {
                        MyApplication.PATHJson=pathJson;
                        return true;
                    }
                });
        Observable<Boolean> ob2= Observable.just( MyApplication.AIDLApi)
                .map(new Function<AidlApi, AIDLSetting>() {
                    @Override
                    public AIDLSetting apply(@io.reactivex.annotations.NonNull AidlApi aidlApi) throws Exception {
                        return aidlApi.getAIDLSetting();
                    }
                }).map(new Function<AIDLSetting, Boolean>() {
                    @Override
                    public Boolean apply(@io.reactivex.annotations.NonNull AIDLSetting setting) throws Exception {
                        MyApplication.AIDLSet =setting;
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
                        Toast.makeText(getBaseContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                        Observable.just(e)
                                .delay(2,TimeUnit.SECONDS)
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_PERMISSIONS = 0;

    private boolean mayRequestInternet() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        final String[] permissions = {INTERNET, WRITE_EXTERNAL_STORAGE, ACCESS_NETWORK_STATE, ACCESS_WIFI_STATE, READ_PHONE_STATE};
        if (shouldShowRequestPermissionRationale(READ_PHONE_STATE)) {
            Snackbar.make(tv_hint_common, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(permissions, REQUEST_PERMISSIONS);
                        }
                    });
        } else {
            requestPermissions(permissions, REQUEST_PERMISSIONS);
        }
        return false;
    }

    private void populateAutoComplete() {
        Loger.d("populateAutoComplete " + mayRequestInternet());
        if (!mayRequestInternet()) {
            return;
        }
        toMainPage();
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }

    private void toMainPage() {
        startActivity(new Intent(getBaseContext(), MainActivity.class));
        finish();
    }
}
