package koolpos.cn.goodsdisplayer.util;

import android.graphics.Bitmap;
import android.util.DisplayMetrics;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.Hashtable;

import koolpos.cn.goodsdisplayer.MyApplication;


public class CodeBitmap {
    private final static int rate = 128;
//    private final static int rate = 128;

    private static BitMatrix deleteWhite(BitMatrix matrix) {
        int[] rec = matrix.getEnclosingRectangle();
        int resWidth = rec[2] + 1;
        int resHeight = rec[3] + 1;

        BitMatrix resMatrix = new BitMatrix(resWidth, resHeight);
        resMatrix.clear();
        for (int i = 0; i < resWidth; i++) {
            for (int j = 0; j < resHeight; j++) {
                if (matrix.get(i + rec[0], j + rec[1])) {
                    resMatrix.set(i, j);
                }
            }
        }
        return resMatrix;
    }

    public static Bitmap Create2DCode(String str) {
        //生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
        BitMatrix matrix = null;
        try {
            Loger.d("DENSITY:" + AndroidUtils.DENSITY);
            matrix = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, (int) (rate * AndroidUtils.DENSITY),
                    (int) (rate * AndroidUtils.DENSITY));
            matrix = deleteWhite(matrix);//删除白边
        } catch (WriterException e) {
            e.printStackTrace();
        }
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        //二维矩阵转为一维像素数组,也就是一直横着排了
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = 0xff000000;
                }

            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        //通过像素数组生成bitmap,具体参考api
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    public static Bitmap createQrBitmap(String content, int width, int height) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = MyApplication.getContext().getResources().getDisplayMetrics();
        //int screenWidth = dm.widthPixels;
        //int screenHeight = dm.heightPixels;
        float density = dm.density;

        return create(content, (int) (width * density), (int) (height * density), BarcodeFormat.QR_CODE);
    }

    public static Bitmap create128Bitmap(String content, int width, int height) {
        return create(content, width, height, BarcodeFormat.CODE_128);
    }

    public static Bitmap create39Bitmap(String content, int width, int height) {
        return create(content, width, height, BarcodeFormat.CODE_39);
    }

    public static Bitmap createEAN13Bitmap(String content, int width, int height) {
        return create(content, width, height, BarcodeFormat.EAN_13);
    }

    /**
     * 创建一维二维码图片
     */
    public static Bitmap create(String content, int width, int height, BarcodeFormat marcodeFormat) {

        // 用于设置QR二维码参数
        Hashtable<EncodeHintType, Object> qrParam = new Hashtable<EncodeHintType, Object>();
        // 设置QR二维码的纠错级别——这里选择最高H级别
        qrParam.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        // 设置编码方式
        qrParam.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        // 生成QR二维码数据——这里只是得到一个由true和false组成的数组
        // 参数顺序分别为：编码内容，编码类型，生成图片宽度，生成图片高度，设置参数
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(content,
                    marcodeFormat, width, height, qrParam);
            System.err.println("width" + width);
            // 开始利用二维码数据创建Bitmap图片，分别设为黑白两色
            int w = bitMatrix.getWidth();
            System.err.println("width getWidth" + w);
            int h = bitMatrix.getHeight();
            int[] data = new int[w * h];

            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    if (bitMatrix.get(x, y))
                        data[y * w + x] = 0xff000000;// 黑色
                    else
                        data[y * w + x] = 0x00000000;// -1 相当于0xffffffff 白色
                }
            }

            // 创建一张bitmap图片，采用最高的图片效果ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            // 将上面的二维码颜色数组传入，生成图片颜色
            bitmap.setPixels(data, 0, w, 0, 0, w, h);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }
}
