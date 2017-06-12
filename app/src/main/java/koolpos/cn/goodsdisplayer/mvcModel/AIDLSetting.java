package koolpos.cn.goodsdisplayer.mvcModel;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2017/6/12.
 */

public class AIDLSetting implements Serializable {
    private static final long serialVersionUID = -8044448709556111004L;
    private int intervalAd;
    private int playLongAd;
    private String deviceSn;
    private String deviceKey;
    private Date lastUpdateTime;
    private boolean loadCacheFirst;

    public int getIntervalAd() {
        return intervalAd;
    }

    public void setIntervalAd(int intervalAd) {
        this.intervalAd = intervalAd;
    }

    public int getPlayLongAd() {
        return playLongAd;
    }

    public void setPlayLongAd(int playLongAd) {
        this.playLongAd = playLongAd;
    }

    public String getDeviceSn() {
        return deviceSn;
    }

    public void setDeviceSn(String deviceSn) {
        this.deviceSn = deviceSn;
    }

    public String getDeviceKey() {
        return deviceKey;
    }

    public void setDeviceKey(String deviceKey) {
        this.deviceKey = deviceKey;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public boolean isLoadCacheFirst() {
        return loadCacheFirst;
    }

    public void setLoadCacheFirst(boolean loadCacheFirst) {
        this.loadCacheFirst = loadCacheFirst;
    }
}
