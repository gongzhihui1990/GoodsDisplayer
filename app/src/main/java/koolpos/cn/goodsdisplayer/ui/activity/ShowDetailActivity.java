package koolpos.cn.goodsdisplayer.ui.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import koolpos.cn.goodsdisplayer.R;
import koolpos.cn.goodsdisplayer.mvcModel.Product;
import koolpos.cn.goodsdisplayer.util.CodeBitmap;

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
    @BindView(R.id.back)
    View back;
    @BindView(R.id.iv_pay_code)
    ImageView iv_pay_code;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_detail);
        Product product = (Product) getIntent().getSerializableExtra(Product.class.getName());
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        renderView(product);
    }

    private void showQrCode(ImageView imageView, String url) {
        if (TextUtils.isEmpty(url)) {
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
        Glide.with(this)
                .load(product.getPicUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.mipmap.downloading)
                .animate(R.anim.zoom_in)
                .error(R.mipmap.download_error)
                .into(ivGoodImage);
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
        if (true){
            onBackPressed();
            return;
        }
        Animation animatorLeft = AnimationUtils.loadAnimation(getBaseContext(), R.anim.show_enter_left_anim);
        animatorLeft.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                leftView.setVisibility(View.GONE);
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
                rightView.setVisibility(View.GONE);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        leftView.startAnimation(animatorLeft);
        rightView.startAnimation(animatorRight);

        Observable.timer(1100, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        finish();
                    }
                });
    }
}
