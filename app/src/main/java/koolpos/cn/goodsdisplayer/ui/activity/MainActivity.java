package koolpos.cn.goodsdisplayer.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import koolpos.cn.goodsdisplayer.MyApplication;
import koolpos.cn.goodsdisplayer.R;
import koolpos.cn.goodsdisplayer.api.AidlApi;
import koolpos.cn.goodsdisplayer.constans.ImageEnum;
import koolpos.cn.goodsdisplayer.mvcModel.AdBean;
import koolpos.cn.goodsdisplayer.mvcModel.Product;
import koolpos.cn.goodsdisplayer.mvcModel.ProductCategory;
import koolpos.cn.goodsdisplayer.rxjava.ActivityObserver;
import koolpos.cn.goodsdisplayer.ui.adapter.ProductAdapter;
import koolpos.cn.goodsdisplayer.ui.fragment.DisplayGoodGroupFragment;
import koolpos.cn.goodsdisplayer.ui.widget.CategoryPop;
import koolpos.cn.goodsdisplayer.ui.widget.GridSpacingItemDecoration;
import koolpos.cn.goodsdisplayer.util.Loger;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MainActivity extends BaseActivity implements DisplayGoodGroupFragment.OnFragmentInteractionListener {

    @BindView(R.id.grid_content)
    RecyclerView gridContentView;
    @BindView(R.id.select_type)
    ImageView viewSelectType;
    @BindView(R.id.select_all)
    ImageView viewSelectAll;
    @BindView(R.id.main_bg)
    View main_bg;
    @BindView(R.id.image_title_bar)
    ImageView imageTitleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        Glide.with(MyApplication.getContext()).resumeRequests();
        gridContentView.setLayoutManager(new LinearLayoutManager(getBaseContext(), LinearLayoutManager.HORIZONTAL, false));
        initUI(MyApplication.AIDLApi);
        initAd();
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
        setImageDrawableFromSD(imageTitleBar, ImageEnum.TITLE_BAR);
        setBackgroundDrawableFromSD(main_bg, ImageEnum.MAIN_BG);
        setBackgroundDrawableFromSD(viewSelectAll, ImageEnum.HOME_BTN);
        setBackgroundDrawableFromSD(viewSelectType, ImageEnum.SEARCH_BTN);
    }

    private List<AdBean> adBeanList = new ArrayList<>();

    private void initAd() {

        Observable.just(MyApplication.AIDLApi)
                .map(new Function<AidlApi, List<AdBean>>() {
                    @Override
                    public List<AdBean> apply(@NonNull AidlApi aidlApi) throws Exception {
                        return aidlApi.getAdList();
                    }
                }).filter(new Predicate<List<AdBean>>() {
            @Override
            public boolean test(@NonNull List<AdBean> adBeen) throws Exception {
                return adBeen != null && adBeen.size() > 0;
            }
        }).observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(new ActivityObserver<List<AdBean>>(MainActivity.this) {
                    @Override
                    public void onNext(List<AdBean> adBeans) {
                        adBeanList.addAll(adBeans);
                    }
                });
    }

    private final int showProduct = 10;
    private final int showAd = 11;

    private ProductAdapter productAdapter;

    private void setGridAdapter(final AidlApi aidlApi, ProductCategory categorySelect) {
        if (productAdapter == null) {
            productAdapter = new ProductAdapter(aidlApi, this);
            GridSpacingItemDecoration dividerGridItemDecoration = new GridSpacingItemDecoration(Integer.MAX_VALUE, 20, true);
            gridContentView.addItemDecoration(dividerGridItemDecoration);
            gridContentView.setAdapter(productAdapter);
            productAdapter.setSkuDisplayCallBack(new ProductAdapter.SkuDisplayDetailCall() {
                private boolean isBlock = false;

                @Override
                public void show(final Product product) {
                    //防止连续点击
                    if (isBlock) {
                        return;
                    } else {
                        isBlock = true;
                        Observable.timer(1000, TimeUnit.MILLISECONDS)
                                .subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.io()).subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(@NonNull Long aLong) throws Exception {
                                isBlock = false;
                            }
                        });
                    }
                    stop();
                    //发射背景图片
                    Loger.i("背景图片");
                    View view = getWindow().getDecorView();
                    Loger.i("背景生成：" + view);
                    Bitmap cacheBmp = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(cacheBmp);
                    view.draw(canvas);
                    MyApplication.CacheBitmap = cacheBmp;
                    Loger.i("背景生成ok");
                    Observable.just(MyApplication.CacheBitmap)
                            .delay(200, TimeUnit.MILLISECONDS)
                            .map(new Function<Bitmap, Intent>() {
                                @Override
                                public Intent apply(@NonNull Bitmap bitmap) throws Exception {
                                    Intent intent = new Intent("CacheBitmapOk");
                                    intent.putExtra(Product.class.getName(), product);
                                    Loger.i("背景生成intent");
                                    return intent;
                                }
                            }).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<Intent>() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onNext(Intent intent) {
                                    Loger.i("背景生成发射");
                                    MyApplication.getContext().sendBroadcast(intent);
                                }

                                @Override
                                public void onError(Throwable e) {
                                    e.printStackTrace();
                                }

                                @Override
                                public void onComplete() {

                                }
                            });
                    Intent intent = new Intent(getBaseContext(), ShowDetailActivity.class);
                    intent.putExtra(Product.class.getName(), product);
                    startActivityForResult(intent, showProduct);
//                    Observable.just()
                }
            });
        }
        productAdapter.setCategory(categorySelect);
    }

    private CategoryPop categoryPop;

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
                        setGridAdapter(aidlApi, categorySelect);
                    }
                };
                Observable.just(productCategories.get(0))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<ProductCategory>() {
                            @Override
                            public void accept(@NonNull ProductCategory category) throws Exception {
                                setGridAdapter(aidlApi, category);
                                startCountingAd();
                            }
                        });
                categoryPop = new CategoryPop(getBaseContext(), productCategories, spuSelectedListener);
                return categoryPop;
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
                        startCountingAd();
                    }
                };
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new ActivityObserver<View.OnClickListener>(MainActivity.this) {
                    @Override
                    public void onNext(final View.OnClickListener onClickListener) {
                        viewSelectType.setOnClickListener(onClickListener);
                        viewSelectAll.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //设置为全部
                                categoryPop.selectAll();
                            }
                        });
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == showProduct) {
            reStartDisplay();
        }
        if (requestCode == showAd) {
            startCountingAd();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        stop();
        super.onDestroy();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private Disposable mRoundSubscribe;
    private Disposable mAdSubscribe;
    private Disposable mBackAllSubscribe;

    private int speedPeriod = 100;
    private int startPeriod = 1000;
    private int replayPeriod = 3000;
    private boolean inverse = false;

    private void backAllFuture() {
        if (mBackAllSubscribe != null) {
            if (!mBackAllSubscribe.isDisposed()){
                return;
            }
        }
        mBackAllSubscribe = Observable.timer(reSetAllPeriod, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        categoryPop.selectAll();
                    }
                });
    }

    private void backAllCancel() {
        if (mBackAllSubscribe!=null&&!mBackAllSubscribe.isDisposed()){
            mBackAllSubscribe.dispose();
        }
    }

    //开始轮播
    public void startDisplay() {
        if (mRoundSubscribe != null) {
            mRoundSubscribe.dispose();
        }
        mRoundSubscribe = Observable.interval(startPeriod, speedPeriod, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {

                        if (!gridContentView.canScrollHorizontally(-1)
                                && !gridContentView.canScrollHorizontally(1)) {
                            backAllFuture();
                        } else {
                            backAllCancel();
                        }
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
        if (mRoundSubscribe != null) {
            mRoundSubscribe.dispose();
        }
        mRoundSubscribe = Observable.interval(replayPeriod, speedPeriod, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        if (!gridContentView.canScrollHorizontally(-1)
                                && !gridContentView.canScrollHorizontally(1)) {
                            backAllFuture();
                        } else {
                            backAllCancel();
                        }
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

    private int adWaitingPeriod = MyApplication.AIDLSet.getIntervalAd();
    private int reSetAllPeriod = MyApplication.AIDLSet.getIntervalReSetAll();
    private int adIndex = 0;
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
                        adIndex++;
                        if (adBeanList != null && adBeanList.size() != 0) {
                            int size = adBeanList.size();
                            int pos = adIndex % size;
                            AdBean adBean = adBeanList.get(pos);
                            if (adBean.getResourType() != null) {
                                switch (adBean.getResourType()) {
                                    case "Image":
                                        Intent intent = new Intent(getBaseContext(), AdImageDisplayActivity.class);
                                        intent.putExtra(AdBean.class.getName(), adBean);
                                        startActivityForResult(intent, showAd);
                                        break;
                                }
                            }

//                            Loger.d("play- "+pos+"/"+size+" : "+.toString());
                            //TODO 展示图片广告
//                            Intent intent = new Intent(getBaseContext(), AdVideoDisplayActivity.class);
//                            startActivityForResult(intent, showAd);
                        } else {
                            Loger.d("play- null");
                        }
                        mAdSubscribe.dispose();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        Intent intent = new Intent(Intent.ACTION_MAIN);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addCategory(Intent.CATEGORY_HOME);
//        startActivity(intent);
    }

}
