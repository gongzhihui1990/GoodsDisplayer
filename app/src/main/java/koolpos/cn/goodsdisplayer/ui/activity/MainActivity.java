package koolpos.cn.goodsdisplayer.ui.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
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

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import koolpos.cn.goodproviderservice.service.aidl.IGPService;
import koolpos.cn.goodsdisplayer.MyApplication;
import koolpos.cn.goodsdisplayer.R;
import koolpos.cn.goodsdisplayer.api.AidlApi;
import koolpos.cn.goodsdisplayer.mvcModel.Product;
import koolpos.cn.goodsdisplayer.mvcModel.ProductCategory;
import koolpos.cn.goodsdisplayer.rxjava.ActivityObserver;
import koolpos.cn.goodsdisplayer.ui.adapter.ProductAdapter;
import koolpos.cn.goodsdisplayer.ui.fragment.DisplayGoodGroupFragment;
import koolpos.cn.goodsdisplayer.ui.widget.CategoryPop;
import koolpos.cn.goodsdisplayer.ui.widget.GridSpacingItemDecoration;
import koolpos.cn.goodsdisplayer.util.AndroidUtils;
import koolpos.cn.goodsdisplayer.util.Loger;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainActivity extends BaseActivity implements DisplayGoodGroupFragment.OnFragmentInteractionListener {

    @BindView(R.id.grid_content)
    RecyclerView gridContentView;
    @BindView(R.id.select_type)
    TextView tvSelectType;
    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            IGPService gpService = IGPService.Stub.asInterface(iBinder);
            AidlApi aidlApi = new AidlApi(gpService);
            initUI(aidlApi);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };


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

    private ProductAdapter productAdapter;
    private void setGridAdapter(final AidlApi aidlApi,ProductCategory categorySelect){
        if (productAdapter==null){
            productAdapter=new ProductAdapter(aidlApi,this);
            GridSpacingItemDecoration dividerGridItemDecoration =new GridSpacingItemDecoration(Integer.MAX_VALUE,20,true);
            gridContentView.addItemDecoration(dividerGridItemDecoration);
            gridContentView.setAdapter(productAdapter);
            productAdapter.setSkuDisplayCallBack(new ProductAdapter.SkuDisplayDetailCall() {
                @Override
                public void show(Product product) {
                    stop();
                    Intent intent = new Intent(getBaseContext(), ShowDetailActivity.class);
                    intent.putExtra(Product.class.getName(), product);
                    startActivityForResult(intent, showSku);
                }
            });
        }
        productAdapter.setCategory(categorySelect);
    }
    private void initUI(final AidlApi aidlApi) {
        Observable.just(aidlApi)
                .map(new Function<AidlApi, List<ProductCategory>>() {
                    @Override
                    public List<ProductCategory> apply(@NonNull AidlApi aidlApi) throws Exception {
                        return aidlApi.getCategoryList();
                    }
                }).map(new Function<List<ProductCategory>, CategoryPop>() {
            @Override
            public CategoryPop apply(@NonNull List<ProductCategory> productCategories) throws Exception {
                CategoryPop.OnSPUSelectedListener spuSelectedListener = new CategoryPop.OnSPUSelectedListener() {
                    @Override
                    public void onSelected(ProductCategory categorySelect) {
                        //TODO gridAdapter.setCategory(type.getTypeName());
                        Loger.i("categorySelect:" + categorySelect.toString());
                        //gridAdapter.setCategory(type.getTypeName());
                        setGridAdapter(aidlApi,categorySelect);
                    }
                };
                Observable.just( productCategories.get(0))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<ProductCategory>() {
                            @Override
                            public void accept(@NonNull ProductCategory category) throws Exception {
                                setGridAdapter(aidlApi,category);
                            }
                        });
                return new CategoryPop(getBaseContext(), productCategories, spuSelectedListener);
            }
        }).map(new Function<CategoryPop, View.OnClickListener>() {
            @Override
            public View.OnClickListener apply(@NonNull final CategoryPop pop) throws Exception {
                return new View.OnClickListener() {
                    /**
                     * 当弹出时，背景变半透明
                     * @param alpha
                     */
                    private void setBackgroundAlpha(float alpha) {
                        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
                        layoutParams.alpha = alpha;
                        getWindow().setAttributes(layoutParams);
                    }

                    private void showCategoryPop(CategoryPop popupWindow) {
                        popupWindow.showAtLocation(findViewById(R.id.main_view), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                        setBackgroundAlpha(0.6f);
                        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                setBackgroundAlpha(1f);
                            }
                        });
                    }

                    @Override
                    public void onClick(View v) {
                        showCategoryPop(pop);
                    }
                };
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ActivityObserver<View.OnClickListener>(MainActivity.this) {
                    @Override
                    public void onNext(final View.OnClickListener onClickListener) {
                        tvSelectType.setOnClickListener(onClickListener);
                        //TODO　setGridAdapter(aidlApi,categorySelect);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == showSku) {
            startDisplay();
        }
        if (requestCode == showAd) {
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
        if (mAdSubscribe != null) {
            mAdSubscribe.dispose();
        }
    }

    private int adWaitingPeriod = 30;

    //adWaitingPeriod d秒后 播放广播
    private void startCountingAd() {
        if (mAdSubscribe != null) {
            mAdSubscribe.dispose();
        }
        mAdSubscribe = Observable.timer(adWaitingPeriod, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        Intent intent = new Intent(getBaseContext(), AdVideoDisplayActivity.class);
                        startActivityForResult(intent, showAd);
                        mAdSubscribe.dispose();
                    }
                });
    }

}
