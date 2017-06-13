package koolpos.cn.goodsdisplayer.mvcModel;

import com.google.gson.Gson;

import java.io.Serializable;


/**
 * Created by Administrator on 2017/6/12.
 */

public class AdBean  implements Serializable{
    private static final long serialVersionUID = 2595999092971504694L;
    private int id;
    private String deviceName;
    private String name;
    private String size;
    private String resourceName;
    private String content;
    private String deviceId;
    private String groupId;
    private String adsId;
    private String auditStatus;
    private String pixel;
    private String created;
    private String resourType;
    private boolean isFromBrand;
    private String fileurl;
    private String thumbnailUrl;
    private String startedTime;
    private String endTime;
    private String duration;
    private String transition;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getAdsId() {
        return adsId;
    }

    public void setAdsId(String adsId) {
        this.adsId = adsId;
    }

    public String getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
    }

    public String getPixel() {
        return pixel;
    }

    public void setPixel(String pixel) {
        this.pixel = pixel;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getResourType() {
        return resourType;
    }

    public void setResourType(String resourType) {
        this.resourType = resourType;
    }

    public boolean isFromBrand() {
        return isFromBrand;
    }

    public void setFromBrand(boolean fromBrand) {
        isFromBrand = fromBrand;
    }

    public String getFileurl() {
        return fileurl;
    }

    public void setFileurl(String fileurl) {
        this.fileurl = fileurl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getStartedTime() {
        return startedTime;
    }

    public void setStartedTime(String startedTime) {
        this.startedTime = startedTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getTransition() {
        return transition;
    }

    public void setTransition(String transition) {
        this.transition = transition;
    }

    @Override
    public String toString() {
        return  new Gson().toJson(this);
    }
}
