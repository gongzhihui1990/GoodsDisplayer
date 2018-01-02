/*
 * Copyright (c) 2017. heisenberg.gong
 */

package net.gtr.framework.util;

import android.util.Log;

import com.tencent.bugly.crashreport.BuglyLog;

import net.gtr.framework.app.BaseApp;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * 封装默认的Log类
 */
public class Loger {
    static {
        logInFile = new ArrayList<>();
        initLogSetting();
    }

    private static List<AppLogLine> logInFile;
    /**
     * 单个日志文件上限大小
     * 如系统空间 256kb
     * 如私有空间 5*1024kb
     */
    private static long KB_LogFileSize = 15 * 1024 * 1024;

    /**
     * @param fileName 日志文件的文件名称（简称）
     * @return 返回（新建或找到的）的日志文件
     * @throws IOException
     */
    private static File getFileStreamPath(String fileName) throws IOException {
        File rootFile = getLogRootFile();
        if (!rootFile.exists()) {
            if (!rootFile.mkdirs()) {
                throw new IOException("root file getInstance failed");
            }
        }
        File logFile = new File(rootFile.getPath() + "/" + fileName);
        if (!logFile.exists()) {
            //需要创建新日志文件
            if (!logFile.createNewFile()) {
                //新日志文件创建失败
                throw new IOException("log file getInstance failed");
//            } else {
//                //新日志文件加入文件头
//                FileWriter writer = new FileWriter(logFile, true);
//                BufferedWriter bw = new BufferedWriter(writer);
//                bw.append(AppLogLine.LogFileHead);
//                bw.close();
//                writer.close();
            }
        }
        System.out.println(" getFileStreamPath " + logFile.getAbsolutePath());
        return logFile;
    }

    private static File rootFile;

    /**
     * 获取日志文件的夹
     *
     * @return
     * @throws IOException
     */
    public static File getLogRootFile() throws IOException {
        if (rootFile != null && rootFile.exists()) {
            return rootFile;
        }
        String rootPath;
        String processName = BaseApp.getProcessName();

        rootPath = FileUtils.getLogAppPath();
        rootFile = new File(rootPath + processName + "/");
        if (!rootFile.exists()) {
            System.out.println("mkdirs " + rootFile.mkdirs());
        }
        return rootFile;
    }

    private static boolean CLOSE = false;
    //大于等于FileLogLevelLimit的才会打印
    private static Level FileLogLevelLimit = Level.Close;

    public static void setFileLogLevelLimit(@android.support.annotation.NonNull Level levelLimit) {
        FileLogLevelLimit = levelLimit;
    }

    /**
     * 日志等级枚举
     */
    public enum Level {
        Verbose(0, "详细-0"), Debug(1, "测试-1"), Info(2, "消息-2"), Warn(3, "警告-3"), Error(4, "错误-4"), Special(5, "特殊-5"), Close(10, "关闭-10");
        private int level;
        private String levelName;

        public String getLevelName() {
            return levelName;
        }

        /**
         * DAS系统取值
         * "DEBUG"
         * "INFO"
         * "WARNING"
         * "ERROR"
         *
         * @return 返回适配DAS记录的日志枚举类型字符串
         */
        public String getLevelDasSysName() {

            switch (this) {
                case Verbose:
                    return "DEBUG";
                case Debug:
                    return "DEBUG";
                case Info:
                    return "INFO";
                case Warn:
                    return "WARNING";
                case Error:
                    return "ERROR";
                case Special:
                    return "SPECIAL";
                case Close:
                    return "CLOSE";
                default:
                    return "DEBUG";
            }
        }

        /**
         * 判断级别是否满足
         */
        public boolean satisfy(Level level) {
            // System.out.println("FileLogLevelLimit:"+FileLogLevelLimit.levelName);
            return this.level >= level.level;
        }

        Level(int level, String levelName) {
            this.level = level;
            this.levelName = levelName;
        }
    }

    private static class LogFileIndex implements Serializable {

        private static final long serialVersionUID = 5797075748688777846L;
        int index = 0;

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        private String getFileNameByIndex(int index) {
            return "logFile_" + index + ".docx";
        }

        private String getFileName() {
            return getFileNameByIndex(index);
        }


    }

    public static File getSimpleLogFile() throws IOException {
        return getFileStreamPath("HS_logFile.txt");
    }

    private static synchronized void executeWrite() {
        System.out.println("executeWrite");
        List<AppLogLine> logTmp = new ArrayList<>();
        logTmp.addAll(logInFile);
        logInFile.clear();
        //System.out.println("interval 开始写日志条数 " + logTmp.size());
        //实现写文件
        Observable.just(logTmp)
                .map(new Function<List<AppLogLine>, String>() {
                    @Override
                    public String apply(@NonNull List<AppLogLine> logs) throws Exception {
                        File file = getSimpleLogFile();
                        //当前文件容量了超过预定值，
                        if (file.length() > KB_LogFileSize) {
                            //TODO
                        }
                        AppLogLine.writeToFile(logs, getSimpleLogFile());
                        logs.clear();
                        return file.getName();
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Observer<String>() {
                    Disposable disposable;

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(String fileName) {
                        try {
                            File file = getFileStreamPath(fileName);
                            long fileSize = file.length();
                            System.out.println(file.getName() + " interval logSize=" + file.length());
                            String sizeStr;
                            if (fileSize >= 1024 * 1024) {
                                float size = (float) fileSize / (1024 * 1024);
                                sizeStr = String.format("%.2f", size) + "MB";
                            } else if (fileSize >= 1024) {
                                float size = (float) fileSize / 1024;
                                sizeStr = String.format("%.2f", size) + "KB";
                            } else {
                                sizeStr = fileSize + "B";
                            }
                            //System.out.println("interval logSize=" + sizeStr);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        if (disposable != null && !disposable.isDisposed()) {
                            disposable.dispose();
                        }
                    }

                });
    }


    private static void initLogSetting() {
        //每1秒回写内存变量到文件文件数据
        System.out.println("interval initLogSetting");
        Observable.timer(3, TimeUnit.SECONDS)
                .filter(new Predicate<Long>() {
                    @Override
                    public boolean test(@NonNull Long aLong) throws Exception {
                        //删除历史日志
                        if (FileLogLevelLimit == Level.Close) {
                            FileUtils.deleteDir(getLogRootFile());
                        }
                        return true;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
//                        FileLogLevelLimit = Level.Close;
                    }
                });
        Observable.interval(5, 1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .filter(new Predicate<Long>() {
                    @Override
                    public boolean test(@NonNull Long aLong) throws Exception {
                        return true;
                    }
                })
                .map(new Function<Long, Integer>() {
                    @Override
                    public Integer apply(@NonNull Long size) throws Exception {
                        return logInFile.size();
                    }
                })
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(@NonNull Integer size) throws Exception {
                        return logInFile.size() != 0;
                    }
                })
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer size) throws Exception {
                        executeWrite();
                    }
                });
    }


    private static void addTerminalLog(Level logLevel, String tag, String log) {
        System.out.println("add"+logLevel+":"+tag+":"+log);
        switch (logLevel) {
            case Verbose:
                Log.v(tag,log);
                BuglyLog.v(tag, log);
                break;
            case Info:
                Log.i(tag,log);
                BuglyLog.i(tag, log);
                break;
            case Debug:
                Log.d(tag,log);
                BuglyLog.d(tag, log);
                break;
            case Warn:
                Log.w(tag,log);
                BuglyLog.w(tag, log);
                break;
            case Error:
            case Special:
                Log.e(tag,log);
                BuglyLog.e(tag,log);
                break;
        }

    }

    public static void addFileLog(Level logLevel, String className, String formatMsg) {
        System.out.println("addFileLog");
        if (logLevel.satisfy(FileLogLevelLimit)) {
            //写入文件
            //Observable<String> log = Observable.just(tag + ":" + msg);
            AppLogLine logLine = AppLogLine.createNow(formatMsg, logLevel, className);
            logInFile.add(logLine);
            if (logInFile.size() > 1) {
                //满30条写一次文件
                executeWrite();
            }
        }
    }

    public static void setLogClose(boolean logClose) {
        CLOSE = logClose;
    }

    /**
     * 对应Log.v
     *
     * @param msg
     */
    public static void v(String msg) {
        final StackTraceElement[] stack = new Throwable().getStackTrace();
        final int i = 1;
        final StackTraceElement ste = stack[i];

        addLog(Level.Verbose, ste.getClassName(),
                String.format("[%s][%d]%s", ste.getMethodName(),
                        ste.getLineNumber(), msg));

    }

    /**
     * 对应Log.s
     *
     * @param msg
     */
    public static void s(String msg) {
        addLog(Level.Special, "", msg);
    }

    /**
     * 对应Log.Special
     *
     * @param msg
     */
    public static void u(String msg) {
        final StackTraceElement[] stack = new Throwable().getStackTrace();
        final int i = 1;
        final StackTraceElement ste = stack[i];

        addLog(Level.Special, ste.getClassName(),
                String.format("[%s][%d]%s", ste.getMethodName(),
                        ste.getLineNumber(), msg));

    }

    private static void addLog(Level verbose, String className, String format) {
        if (!CLOSE) {
            addTerminalLog(verbose, className, format);
        }
        addFileLog(verbose, className, format);
    }

    /**
     * 对应Log.d
     *
     * @param msg msg
     */
    public static void d(String msg) {
        final StackTraceElement[] stack = new Throwable().getStackTrace();
        final int i = 1;
        final StackTraceElement ste = stack[i];

        addLog(Level.Debug, ste.getClassName(),
                String.format("[%s][%d]%s", ste.getMethodName(),
                        ste.getLineNumber(), msg));
    }

    /**
     * 对应Log.i
     *
     * @param msg
     */
    public static void i(String msg) {
        final StackTraceElement[] stack = new Throwable().getStackTrace();
        final int i = 1;
        final StackTraceElement ste = stack[i];

        addLog(Level.Info, ste.getClassName(),
                String.format("[%s][%d]%s", ste.getMethodName(),
                        ste.getLineNumber(), msg));
    }

    /**
     * 对应Log.w
     *
     * @param msg
     */
    public static void w(String msg) {
        final StackTraceElement[] stack = new Throwable().getStackTrace();
        final int i = 1;
        final StackTraceElement ste = stack[i];

        addLog(Level.Warn, ste.getClassName(),
                String.format("[%s][%d]%s", ste.getMethodName(),
                        ste.getLineNumber(), msg));
    }


    /**
     * 对应Log.e
     *
     * @param msg
     */
    public static void e(String msg) {
        final StackTraceElement[] stack = new Throwable().getStackTrace();
        final int i = 1;
        final StackTraceElement ste = stack[i];

        addLog(Level.Error, ste.getClassName(),
                String.format("[%s][%d]%s", ste.getMethodName(),
                        ste.getLineNumber(), msg));
    }

}
