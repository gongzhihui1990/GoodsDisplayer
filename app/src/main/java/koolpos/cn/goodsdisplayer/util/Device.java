package koolpos.cn.goodsdisplayer.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import koolpos.cn.goodsdisplayer.MyApplication;

/**
 * Created by caroline on 2017/4/17.
 */

public class Device {

    /**
     * 是否手持收银机
     * @return
     */
    public static boolean isPOSMobile() {
        return isP8000()||isN900()||isA8()||isI9000s()||isP2000();
    }

    /**
     * 是否平板收银机
     *
     * @return
     */
    public static boolean isPOSPAD() {
        return isKool10() || isKool11() || isKoolDesk();
    }
    public static boolean isP8000() {
        return Build.MODEL.equals("P8000") || Build.MODEL.equals("P8000S");
    }

    public static boolean isP2000() {
        return Build.MODEL.equals("P2000");
    }

    public static boolean isI9000s() {
        return Build.MODEL.equals("SQ27");
    }

    public static boolean isN900() {
        return Build.MODEL.equals("N900");
    }

    public static boolean isA8() {
        return Build.MODEL.equals("APOS A8");
    }

    public static boolean isMI() {
        return Build.MODEL.contains("APOS A8");
    }
    /**
     * 是否平板
     *
     * @return
     */
    public static boolean isPAD() {
        return isPOSPAD() || isPADDevice();
    }

    private static boolean isPADDevice() {
        if (MyApplication.getContext() != null) {
            double screenInches = getScreenSizeOfDevice();
            if (screenInches <= 6) {
                return false;
            }
        }
        return isTablet();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static double getScreenSizeOfDevice() {
        Point point = new Point();
        WindowManager wm = (WindowManager) (MyApplication.getContext().getSystemService(Context.WINDOW_SERVICE));
        wm.getDefaultDisplay().getRealSize(point);
        DisplayMetrics dm = MyApplication.getContext().getResources().getDisplayMetrics();
        double x = Math.pow(point.x / dm.xdpi, 2);
        double y = Math.pow(point.y / dm.ydpi, 2);
        double screenInches = Math.sqrt(x + y);
        return screenInches;
    }

    private static boolean isTablet() {
        return (MyApplication.getContext().getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static boolean isSupportDeviceCheck() {
        return false;//isKool10() || isKool11();
    }

    /**
     * 是否收银机（收银机必须有：支持打印外设）
     */
    public static boolean isPOS() {
        return isPOSPAD();
    }
    /**
     * @return 是否金融收银机（支持银行卡支付）
     */
    public static boolean isPOS_SV() {
        return isKool10() || isKool11();
    }

    /**
     * 酷客显
     */
    public static boolean isKoolDesk() {
        return Build.MODEL.contains("kool-desktop");
    }

    /**
     * 酷10
     */
    public static boolean isKool10() {
        return Build.MODEL.contains("koolpos-par10");
    }

    /**
     * 酷11
     */
    public static boolean isKool11() {
        return Build.MODEL.contains("KoolRegister") || Build.MODEL.contains("Kool11");
    }

    public static final String PINPADTYPE_SHENGBEN = "09";
    public static final String PINPADTYPE_NEWLAND = "02";
    public static final String N20 = "06";
    public static final String SP10 = "02";
    public static final String PINPADTYPE_LANDI = "08";
    public static final String PINPADTYPE_I9000S = "10";
    public static boolean IS_N20 = true;

    public static String getPinpadType() {
        if (isKool10()) {
            if (IS_N20) {
                return N20;
            } else {
                return SP10;
            }
        }
        return PINPADTYPE_SHENGBEN;
    }
    /**
     * 山寨手机
     */
    public static boolean isPicasso(){
        return Build.MODEL.equals("Picasso");
    }

}
