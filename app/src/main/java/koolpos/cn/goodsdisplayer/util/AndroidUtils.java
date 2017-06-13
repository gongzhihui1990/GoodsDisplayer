package koolpos.cn.goodsdisplayer.util;

import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.media.Image;
import android.provider.Settings;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import koolpos.cn.goodsdisplayer.MyApplication;
import koolpos.cn.goodsdisplayer.R;

public class AndroidUtils {

	public static void loadImageAnim(String url,ImageView imageView){
		loadImage(url,imageView,true);
	}
	public static void loadImage(String url,ImageView imageView,boolean withAnim){
		DrawableRequestBuilder<String> request;
		if (url.startsWith("http")||url.startsWith("/")){
			request=Glide.with(imageView.getContext())
					.load(url);
		}else {
			Loger.i("from warp "+"http://store.troncell.com/"+url);
			request= Glide.with(imageView.getContext())
					.load("http://store.troncell.com/"+url);
		}
		if (withAnim){
			request = request.animate(R.anim.zoom_in);
		}
		request.diskCacheStrategy(DiskCacheStrategy.ALL)
				.skipMemoryCache(false)
				.placeholder(R.drawable.rotate_loading)
				.error(R.mipmap.download_error)
				.fitCenter()
				.into(imageView);
	}
	public static Intent getExplicitIntent(Context context, Intent implicitIntent) {
        // Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);
        // Make sure only one match was found
        if (resolveInfo == null || resolveInfo.size() != 1) {
        	Loger.e("get implicitIntent");
            return implicitIntent;
		}
        // Get component info and create ComponentName
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);
        // Create a new intent. Use the old one for extras and such reuse
        Intent explicitIntent = new Intent(implicitIntent);
        // Set the component to be explicit
        explicitIntent.setComponent(component);
        Loger.e("get explicitIntent");
        return explicitIntent;
    }
	

	/**
	 * 获得当前屏幕亮度的模式 SCREEN_BRIGHTNESS_MODE_AUTOMATIC=1 为自动调节屏幕亮度
	 * SCREEN_BRIGHTNESS_MODE_MANUAL=0 为手动调节屏幕亮度
	 */
	private static int getScreenMode() {
		int screenMode = 0;
		try {
			screenMode = Settings.System.getInt(MyApplication.getContext().getContentResolver(),
					Settings.System.SCREEN_BRIGHTNESS_MODE);
		} catch (Exception localException) {

		}
		return screenMode;
	}

	/**
	 * 获得当前屏幕亮度值 0--255
	 */
	private static int getScreenBrightness() {
		int screenBrightness = 255;
		try {
			screenBrightness = Settings.System.getInt(MyApplication.getContext().getContentResolver(),
					Settings.System.SCREEN_BRIGHTNESS);
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		return screenBrightness;
	}

	/**
	 * 设置当前屏幕亮度的模式 SCREEN_BRIGHTNESS_MODE_AUTOMATIC=1 为自动调节屏幕亮度
	 * SCREEN_BRIGHTNESS_MODE_MANUAL=0 为手动调节屏幕亮度
	 */
	private static void setScreenMode(int paramInt) {
		try {
			Settings.System.putInt(MyApplication.getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE,
					paramInt);
		} catch (Exception localException) {
			localException.printStackTrace();
		}
	}

	/**
	 * 设置当前屏幕亮度值 0--255
	 */
	private static void saveScreenBrightness(int paramInt) {
		try {
			Settings.System.putInt(MyApplication.getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS,
					paramInt);
		} catch (Exception localException) {
			localException.printStackTrace();
		}
	}



    public static final float DENSITY = Resources.getSystem().getDisplayMetrics().density;

    public static int dp2px(int dp) {
        return Math.round(dp * DENSITY);
    }
    
    
    
    /**
     * 将彩色图转换为纯黑白二色
     * 
     * @param bmp
     * @return 返回转换好的位图
     */
     public static Bitmap convertToBlackWhite(Bitmap bmp) {
       int width = bmp.getWidth(); // 获取位图的宽
       int height = bmp.getHeight(); // 获取位图的高
       int[] pixels = new int[width * height]; // 通过位图的大小创建像素点数组

       bmp.getPixels(pixels, 0, width, 0, 0, width, height);
       for (int i = 0; i < height; i++) {
         for (int j = 0; j < width; j++) {
           int grey = pixels[width * i + j];

           int alpha =((grey & 0xFF000000) >> 24);
           //分离三原色
           int red = ((grey & 0x00FF0000) >> 16);
           int green = ((grey & 0x0000FF00) >> 8);
           int blue = (grey & 0x000000FF);
           
           //转化成灰度像素
           grey = (int) (red * 0.3 + green * 0.59 + blue * 0.11);
           grey = alpha | (grey << 16) | (grey << 8) | grey;
           pixels[width * i + j] = grey;
         }
       }
       //新建图片
       Bitmap newBmp = Bitmap.createBitmap(width, height, Config.RGB_565);
       //设置图片数据
       newBmp.setPixels(pixels, 0, width, 0, 0, width, height);
       return newBmp;
//       Bitmap resizeBmp = ThumbnailUtils.extractThumbnail(newBmp, width, height);
//       return resizeBmp;
     }

}
