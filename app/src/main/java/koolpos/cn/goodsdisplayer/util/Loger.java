package koolpos.cn.goodsdisplayer.util;

import android.util.Log;

/**
 * 封装默认的Log类
 */
public class Loger {
    private static boolean CLOSE = false;

    public static void setLogClose(boolean logClose) {
        CLOSE = logClose;
    }

    public static boolean isLogCLose() {
        return CLOSE;
    }

    /**
     * 对应Log.v
     *
     * @param msg
     */
    public static void v(String msg) {
        if (CLOSE)
            return;

        final StackTraceElement[] stack = new Throwable().getStackTrace();
        final int i = 1;
        final StackTraceElement ste = stack[i];

        Log.v(ste.getClassName(),
                String.format("[%s][%d]%s", ste.getMethodName(),
                        ste.getLineNumber(), msg));
    }

    /**
     * 对应Log.d
     *
     * @param msg
     */
    public static void d(String msg) {
        if (CLOSE)
            return;

        final StackTraceElement[] stack = new Throwable().getStackTrace();
        final int i = 1;
        final StackTraceElement ste = stack[i];

        Log.d(ste.getClassName(),
                String.format("[%s][%d]%s", ste.getMethodName(),
                        ste.getLineNumber(), msg));
    }

    /**
     * 对应Log.i
     *
     * @param msg
     */
    public static void i(String msg) {
        if (CLOSE)
            return;

        final StackTraceElement[] stack = new Throwable().getStackTrace();
        final int i = 1;
        final StackTraceElement ste = stack[i];

        Log.i(ste.getClassName(),
                String.format("[%s][%d]%s", ste.getMethodName(),
                        ste.getLineNumber(), msg));
    }

    /**
     * 对应Log.w
     *
     * @param msg
     */
    public static void w(String msg) {
        if (CLOSE)
            return;

        final StackTraceElement[] stack = new Throwable().getStackTrace();
        final int i = 1;
        final StackTraceElement ste = stack[i];

        Log.w(ste.getClassName(),
                String.format("[%s][%d]%s", ste.getMethodName(),
                        ste.getLineNumber(), msg));
    }

    /**
     * 对应Log.e
     *
     * @param msg
     */
    public static void e(String msg) {
        if (CLOSE)
            return;

        final StackTraceElement[] stack = new Throwable().getStackTrace();
        final int i = 1;
        final StackTraceElement ste = stack[i];

        Log.e(ste.getClassName(),
                String.format("[%s][%d]%s", ste.getMethodName(),
                        ste.getLineNumber(), msg));
    }

}
