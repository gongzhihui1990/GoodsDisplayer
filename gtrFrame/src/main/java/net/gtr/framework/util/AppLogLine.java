/*
 * Copyright (c) 2017. heisenberg.gong
 */

package net.gtr.framework.util;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by Administrator on 2017/6/6.
 */

public class AppLogLine implements Serializable{
    private static final long serialVersionUID = 6941000524175154843L;
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE);
    private static SimpleDateFormat formatDetail = new SimpleDateFormat("MM-dd HH:mm:ss:SSS", Locale.CHINESE);
//    "log":"log msg...",
//            "level":"INFO",
//            "tag":"a",
//            "time":"2015-12-17 14:06:28",
//            "rowNo":"2015121700000008"

    String log;
    String level;
    String tag;
    String time;
    String rowNo;

    private static Gson gson ;
    private static Gson getGsonInstance(){
        if (gson == null){
            gson = new GsonBuilder().disableHtmlEscaping().create();
        }
        return gson;
    }
    @Override
    public String toString() {
        return getGsonInstance().toJson(this);
    }

    private static long row = 0;

    private AppLogLine(){}

    public static AppLogLine createNow(String message, Loger.Level level,String tag){
        AppLogLine line =new AppLogLine();
        String time = format.format(Calendar.getInstance().getTime());
        String timeDetail = formatDetail.format(Calendar.getInstance().getTime());
        line.setTime(time);
        line.setLevel(level.getLevelDasSysName());
        line.setTag(tag);
        line.setLog(timeDetail+" " +message);
        return line;
    }
    /**
     * 向文件写日志列表
     *
     * @param logLines
     * @return
     */
    public static boolean writeToFile(List<AppLogLine> logLines, File file) throws IOException {
        if (logLines != null && logLines.size() != 0 && file !=null && file.exists()) {
            FileWriter writer =new FileWriter(file, true);
            BufferedWriter bw =new BufferedWriter(writer);
            for (AppLogLine logLine : logLines) {
                String data = logLine.getLog()+"\n";
                bw.append(data);
//                if (logLine.getLevel().equals(Loger.Level.Special.getLevelDasSysName())){
//                    String data = logLine.getLog()+"\n";
//                    bw.append(data);
//                }else{
//                    String data =logLine+"\n";
//                    bw.append(data);
//                }

            }
            bw.close();
            writer.close();
            return true;
        }
        return false;
    }
//            mFileTypes.put("504B0304", "docx");

    public  static  String LogFileHead ="504B0304";
    /**
     * 从文件读日志列表
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static List<AppLogLine> readFromFile(File file) throws IOException {
        List<AppLogLine> logLines = new ArrayList<>();
        if (file.exists()) {
            FileReader reader = new FileReader(file);
            BufferedReader br = new BufferedReader(reader);
            String strLine = null;
            br.skip(LogFileHead.length());
            while ((strLine = br.readLine()) != null) {
                String data = strLine;
                AppLogLine logLine = generalLogByStrLing(data);
                if (logLine != null) {
                    logLines.add(logLine);
                }
            }
            br.close();
            reader.close();
        }
        return logLines;
    }

    private static AppLogLine generalLogByStrLing(String strLine) {
        if (TextUtils.isEmpty(strLine)) {
            return null;
        }
        try {
            return new Gson().fromJson(strLine,AppLogLine.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRowNo() {
        return rowNo;
    }

    public void setRowNo(String rowNo) {
        this.rowNo = rowNo;
    }
}
