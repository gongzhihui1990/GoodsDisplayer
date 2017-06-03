package koolpos.cn.goodsdisplayer.ui.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONException;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import koolpos.cn.goodproviderservice.service.aidl.IGPService;
import koolpos.cn.goodsdisplayer.MyApplication;
import koolpos.cn.goodsdisplayer.R;
import koolpos.cn.goodsdisplayer.api.AidlApi;
import koolpos.cn.goodsdisplayer.mvcModel.ProductType;
import koolpos.cn.goodsdisplayer.mvcModel.Goods;
import koolpos.cn.goodsdisplayer.ui.adapter.DisplaySkuAdapter;
import koolpos.cn.goodsdisplayer.ui.fragment.DisplayGoodGroupFragment;
import koolpos.cn.goodsdisplayer.ui.widget.SpacesItemDecoration;
import koolpos.cn.goodsdisplayer.ui.widget.TypePopupWindow;
import koolpos.cn.goodsdisplayer.util.AndroidUtils;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class DisplayActivity extends BaseActivity implements DisplayGoodGroupFragment.OnFragmentInteractionListener {

    @BindView(R.id.grid_content)
    RecyclerView gridContentView;
    @BindView(R.id.select_type)
    TextView tvSelectType;
    /**
     * 当前分类标记位
     */
    private int selectedIndex = -1;
    /**
     * 分类列表
     */
    private List<ProductType> types;
    ServiceConnection connection = new ServiceConnection() {
        private AidlApi aidlApi;
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            IGPService gpService = IGPService.Stub.asInterface(iBinder);
            aidlApi = new AidlApi(gpService);
            final DisplaySkuAdapter gridAdapter = new DisplaySkuAdapter(aidlApi);
            gridAdapter.setSkuDisplayCallBack(new DisplaySkuAdapter.SkuDisplayDetailCall() {
                @Override
                public void show(Goods good) {
                    stop();
                    Intent intent = new Intent(getBaseContext(), ShowDetailActivity.class);
                    intent.putExtra(Goods.class.getName(), good);
                    startActivityForResult(intent, showSku);
                }
            });
            SpacesItemDecoration decoration = new SpacesItemDecoration(20);
            gridContentView.addItemDecoration(decoration);
            gridContentView.setAdapter(gridAdapter);
            try {
                types = aidlApi.getTypeList();
                if (selectedIndex == -1) {
                    selectedIndex = 0;
                    gridAdapter.setType(types.get(0).getTypeName());
                }
                tvSelectType.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TypePopupWindow.OnSPUSelectedListener spuSelectedListener = new TypePopupWindow.OnSPUSelectedListener() {
                            @Override
                            public void onSelected(/*GoodType type,*/int index) {
                                selectedIndex = index;
                                ProductType type = types.get(selectedIndex);
                                gridAdapter.setType(type.getTypeName());
                            }
                        };
                        showPopFormBottom(types, selectedIndex, spuSelectedListener);
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };

    private WindowManager.LayoutParams params;

    private void showPopFormBottom(List<ProductType> data, int selectedIndex, TypePopupWindow.OnSPUSelectedListener spuSelectedListener) {
        TypePopupWindow popupWindow = new TypePopupWindow(getBaseContext(), selectedIndex, data, spuSelectedListener);
        popupWindow.showAtLocation(findViewById(R.id.main_view), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        params = getWindow().getAttributes();
        //当弹出Popupwindow时，背景变半透明
        params.alpha = 0.7f;
        getWindow().setAttributes(params);
        //设置Popupwindow关闭监听，当Popupwindow关闭，背景恢复1f
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                params = getWindow().getAttributes();
                params.alpha = 1f;
                getWindow().setAttributes(params);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        Glide.with(MyApplication.getContext()).resumeRequests();
        gridContentView.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.HORIZONTAL, false));
        Intent serviceIntent = new Intent(IGPService.class.getName());
        serviceIntent = AndroidUtils.getExplicitIntent(getBaseContext(), serviceIntent);
        boolean bindService = bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
        startDisplay();
        startCountingAd();
        gridContentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //监听ViewPager的触摸事件，当用户按下的时候取消注册，当用户手抬起的时候再注册
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        stop();
                        break;
                    case MotionEvent.ACTION_UP:
                        stop();
                        reStartDisplay();
                        startCountingAd();
                        break;
                }
                return false;
            }
        });
    }

    private final int showSku = 10;
    private final int showAd = 11;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == showSku) {
            startDisplay();
        }
        if (requestCode == showAd){
            startCountingAd();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        unbindService(connection);
        stop();
        super.onDestroy();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private Disposable mRoundSubscribe;

    private Disposable mAdSubscribe;

    private int speedPeriod = 100;
    private int startPeriod = 1000;
    private int replayPeriod = 3000;
    private boolean inverse = false;

    //开始轮播
    public void startDisplay() {
        mRoundSubscribe = Observable.interval(startPeriod, speedPeriod, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        if (inverse) {
                            if (gridContentView.canScrollHorizontally(-1)) {
                                gridContentView.scrollBy(-1, 0);
                            } else {
                                inverse = false;
                            }
                        } else {
                            if (gridContentView.canScrollHorizontally(1)) {
                                gridContentView.scrollBy(1, 0);
                            } else {
                                inverse = true;
                            }
                        }
                    }
                });
    }

    //重新轮播
    public void reStartDisplay() {
        mRoundSubscribe = Observable.interval(replayPeriod, speedPeriod, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        if (inverse) {
                            if (gridContentView.canScrollHorizontally(-1)) {
                                gridContentView.scrollBy(-1, 0);
                            } else {
                                inverse = false;
                            }
                        } else {
                            if (gridContentView.canScrollHorizontally(1)) {
                                gridContentView.scrollBy(1, 0);
                            } else {
                                inverse = true;
                            }
                        }
                    }
                });
    }

    //结束、暂停轮播
    public void stop() {
        mRoundSubscribe.dispose();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCountingAd();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAdSubscribe!=null){
            mAdSubscribe.dispose();
        }
    }

    private int adWaitingPeriod = 30 ;
    //adWaitingPeriod d秒后 播放广播
    private void startCountingAd(){
        if (mAdSubscribe!=null){
            mAdSubscribe.dispose();
        }
        mAdSubscribe = Observable.timer(adWaitingPeriod, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        Intent intent =new Intent(getBaseContext(),AdVideoDisplayActivity.class);
                        startActivityForResult(intent,showAd);
                        mAdSubscribe.dispose();
                    }
                });
    }

}
