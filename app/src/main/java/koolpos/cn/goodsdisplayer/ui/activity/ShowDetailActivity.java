package koolpos.cn.goodsdisplayer.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import koolpos.cn.goodsdisplayer.MyApplication;
import koolpos.cn.goodsdisplayer.R;
import koolpos.cn.goodsdisplayer.mvcModel.Product;
import koolpos.cn.goodsdisplayer.util.AndroidUtils;
import koolpos.cn.goodsdisplayer.util.CodeBitmap;
import koolpos.cn.goodsdisplayer.util.Loger;
import koolpos.cn.goodsdisplayer.util.ViewUtil;

/**
 * Created by caroline on 2017/5/30.
 */

public class ShowDetailActivity extends BaseActivity {
    @BindView(R.id.good_img)
    ImageView ivGoodImage;
    @BindView(R.id.root_view)
    View rootView;
    @BindView(R.id.rl_left)
    View leftView;
    @BindView(R.id.rl_right)
    View rightView;
    @BindView(R.id.good_name)
    TextView tvGoodName;
    @BindView(R.id.iv_bg)
    ImageView ivBackGround;
    @BindView(R.id.back_area_1)
    View back_area_1;
    @BindView(R.id.back_area_2)
    View back_area_2;
    @BindView(R.id.iv_pay_code)
    ImageView iv_pay_code;
    @BindView(R.id.good_price)
    TextView good_price;
    @BindView(R.id.good_description)
    TextView good_description;
    @BindView(R.id.iv_pay_code_ll)
    View iv_pay_code_ll;
    @BindView(R.id.iv_bar_cat)
    View iv_bar_cat;
    @BindView(R.id.tv_hint_bar)
    TextView tv_hint_bar;
    BroadcastReceiver cacheBitmapOkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Product productSrc = (Product) getIntent().getSerializableExtra(Product.class.getName());
            Loger.d("productSrc :" + productSrc.getTitle() + "-" + MyApplication.CacheBitmap);
            Observable.just(productSrc)
                    .filter(new Predicate<Product>() {
                        @Override
                        public boolean test(@NonNull Product productSrc) throws Exception {
                            return productSrc != null && productSrc.equals(product);
                        }
                    }).map(new Function<Product, Bitmap>() {
                @Override
                public Bitmap apply(@NonNull Product product) throws Exception {
                    return MyApplication.CacheBitmap;
                }
            }).filter(new Predicate<Bitmap>() {
                @Override
                public boolean test(@NonNull Bitmap bitmap) throws Exception {
                    Loger.e("MyApplication.CacheBitmap is null");
                    return bitmap != null;
                }
            }).map(new Function<Bitmap, Bitmap>() {
                @Override
                public Bitmap apply(@NonNull Bitmap bitmap) throws Exception {
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        Loger.i("blur start");
                        Bitmap mTempBitmap = MyApplication.CacheBitmap.copy(Bitmap.Config.ARGB_8888, true);//把当前bitmap赋值给待滤镜处理的bitmap
                        Bitmap blurBitmap = ViewUtil.blurBitmap(mTempBitmap, getBaseContext());
                        mTempBitmap.recycle();
                        mTempBitmap=null;
                        Loger.i("blur end");
                        return blurBitmap;
                    }
                    return null;
                }
            }).filter(new Predicate<Bitmap>() {
                @Override
                public boolean test(@NonNull Bitmap bitmap) throws Exception {
                    Loger.e("blurBitmap is null");
                    return bitmap != null;
                }
            })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(new Observer<Bitmap>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onNext(Bitmap blurBitmap) {
                            ivBackGround.setBackgroundDrawable(new BitmapDrawable(blurBitmap));
                            ivBackGround.setAlpha(0.3f);
                            final float fromAlpha = 0f;
                            final float toAlpha = 0.3f;
                            final AccelerateInterpolator accelerateInterpolator = new AccelerateInterpolator();
                            Animation animation = new AlphaAnimation(fromAlpha, toAlpha);
                            animation.setRepeatCount(0);
                            animation.setDuration(300);
                            animation.setInterpolator(accelerateInterpolator);
                            ivBackGround.startAnimation(animation);
                            ivBackGround.requestFocus();
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        }
    };
    private Product product;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_detail);
        registerReceiver(cacheBitmapOkReceiver, new IntentFilter("CacheBitmapOk"));
        product = (Product) getIntent().getSerializableExtra(Product.class.getName());
        back_area_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        back_area_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        renderView(product);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(cacheBitmapOkReceiver);
        if ( MyApplication.CacheBitmap!=null){
            MyApplication.CacheBitmap.recycle();
            MyApplication.CacheBitmap=null;
        }

    }

    private void showQrCode(ImageView imageView, String url) {
        if (TextUtils.isEmpty(url)) {
            iv_pay_code_ll.setVisibility(View.INVISIBLE);
            iv_bar_cat.setVisibility(View.INVISIBLE);
            tv_hint_bar.setVisibility(View.INVISIBLE);
            return;
        }
        Bitmap bitmapShow = CodeBitmap.Create2DCode(url);
        imageView.setImageBitmap(bitmapShow);
    }

    private void renderView(Product product) {
        showQrCode(iv_pay_code, product.getQrCodeUrl());
        rightView.setVisibility(View.INVISIBLE);
        leftView.setVisibility(View.INVISIBLE);
        tvGoodName.setText(product.getTitle());
        good_price.setText(product.getPrice());
        good_description.setText(product.getTitle());

        good_description.setTypeface(MyApplication.FZ_GBK);
        tvGoodName.setTypeface(MyApplication.FZ_GBK);
        good_price.setTypeface(MyApplication.FZ_GBK);
        tv_hint_bar.setTypeface(MyApplication.FZ_GBK);
//        Glide.with(this)
//                .load(product.getPicUrl())
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .placeholder(R.mipmap.downloading)
//                .animate(R.anim.zoom_in)
//                .fitCenter()
//                .error(R.mipmap.download_error)
//                .into(ivGoodImage);
        AndroidUtils.loadImage(product.getPicUrl(), ivGoodImage,false);
        Animation animatorLeft = AnimationUtils.loadAnimation(getBaseContext(), R.anim.show_enter_left_anim);
        animatorLeft.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                leftView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        Animation animatorRight = AnimationUtils.loadAnimation(getBaseContext(), R.anim.show_enter_right_anim);
        animatorRight.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                rightView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        leftView.startAnimation(animatorLeft);
        rightView.startAnimation(animatorRight);

    }

    @Override
    public void onBackPressed() {
        if (true) {
            super.onBackPressed();
            return;
        }
//        Animation animatorLeft = AnimationUtils.loadAnimation(getBaseContext(), R.anim.show_enter_left_anim);
//        animatorLeft.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                leftView.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//            }
//        });
//        Animation animatorRight = AnimationUtils.loadAnimation(getBaseContext(), R.anim.show_enter_right_anim);
//        animatorRight.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                rightView.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//            }
//        });
//        leftView.startAnimation(animatorLeft);
//        rightView.startAnimation(animatorRight);
//
//        Observable.timer(1100, TimeUnit.MILLISECONDS)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<Long>() {
//                    @Override
//                    public void accept(@NonNull Long aLong) throws Exception {
//                        finish();
//                    }
//                });
    }
}
