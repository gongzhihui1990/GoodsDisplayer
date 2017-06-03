package koolpos.cn.goodsdisplayer.ui.activity;

import android.animation.Animator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.security.NoSuchAlgorithmException;

import butterknife.BindView;
import koolpos.cn.goodsdisplayer.MyApplication;
import koolpos.cn.goodsdisplayer.R;
import koolpos.cn.goodsdisplayer.mvcModel.Goods;
import koolpos.cn.goodsdisplayer.util.CodeBitmap;
import koolpos.cn.goodsdisplayer.util.FileUtil;
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
    @BindView(R.id.back)
    View back;
    @BindView(R.id.iv_pay_code)
    ImageView iv_pay_code;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_detail);
        Loger.d("onCreate 1");
        Goods good= (Goods) getIntent().getSerializableExtra(Goods.class.getName());
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//        if (MyApplication.CacheBitmap!=null){
//            ivBackGround.setBackgroundDrawable(new BitmapDrawable(MyApplication.CacheBitmap));
//            if (MyApplication.CacheBitmap!=null){
//                if (android.os.Build.VERSION.SDK_INT>= Build.VERSION_CODES.JELLY_BEAN_MR1){
//                    Bitmap mTempBitmap = MyApplication.CacheBitmap.copy(Bitmap.Config.ARGB_8888, true);//把当前bitmap赋值给待滤镜处理的bitmap
//                    Bitmap cacheBmp = ViewUtil.blurBitmap(mTempBitmap,getBaseContext());
//                    rootView.setBackgroundDrawable(new BitmapDrawable(cacheBmp));
//                    Animation animationFade=AnimationUtils.loadAnimation(getBaseContext(),R.anim.fade_anim);
//                    animationFade.setAnimationListener(new Animation.AnimationListener() {
//                        @Override
//                        public void onAnimationStart(Animation animation) {
//
//                        }
//
//                        @Override
//                        public void onAnimationEnd(Animation animation) {
//                            ivBackGround.setVisibility(View.GONE);
//                        }
//
//                        @Override
//                        public void onAnimationRepeat(Animation animation) {
//
//                        }
//                    });
//                    ivBackGround.startAnimation(animationFade);
//                }
//            }
//            renderView(good);
//        }
        renderView(good);

    }
    private void showQrcode(ImageView ivQrcode, String qrcodeUrl) {
        if (TextUtils.isEmpty(qrcodeUrl)) {
            return;
        }
        Bitmap bitmapShow =CodeBitmap.Create2DCode(qrcodeUrl);
        Loger.d("qrcodeUrl:"+qrcodeUrl);
        ivQrcode.setImageBitmap(bitmapShow);
    }
    private void renderView(Goods itemGood){
        showQrcode(iv_pay_code,itemGood.getImage_url());
        rightView.setVisibility(View.INVISIBLE);
        leftView.setVisibility(View.INVISIBLE);
        tvGoodName.setText(itemGood.getGoods_name());
        try {
            Glide.with(this)
                    .load(FileUtil.getImageCashFile(itemGood.getImage_url()))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.mipmap.downloading)
                    .animate(R.anim.zoom_in)
                    .error(R.mipmap.download_error)
                    .into(ivGoodImage);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
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
}
