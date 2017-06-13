package koolpos.cn.goodsdisplayer.api;

import android.os.RemoteException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import koolpos.cn.goodproviderservice.service.aidl.IGPService;
import koolpos.cn.goodsdisplayer.mvcModel.AIDLSetting;
import koolpos.cn.goodsdisplayer.mvcModel.AdBean;
import koolpos.cn.goodsdisplayer.mvcModel.AidlResponse;
import koolpos.cn.goodsdisplayer.mvcModel.Goods;
import koolpos.cn.goodsdisplayer.mvcModel.Product;
import koolpos.cn.goodsdisplayer.mvcModel.ProductCategory;
import koolpos.cn.goodsdisplayer.mvcModel.ProductTestType;
import koolpos.cn.goodsdisplayer.util.Loger;

/**
 * Created by Administrator on 2017/5/14.
 */

public class AidlApi {
    private IGPService service;

    public AidlApi(IGPService service) {
        this.service = service;
    }

    public List<ProductTestType> getTypeList() throws Exception {
        JSONObject request = new JSONObject();
        request.put("action", "local/get/getTypeList");
        AidlResponse response = proxyPost(request.toString());
        if (response.getCode() != 0) {
            Loger.d("throw exception");
            throw new Exception(response.getMessage());
        }
        Loger.d("response code  : " + response.getCode());

        String data = response.getData();
        List<String> typeList = new Gson().fromJson(data,
                new TypeToken<List<String>>() {
                }.getType());
        List<ProductTestType> ptList = new ArrayList<>();
        for (String type : typeList) {
            ptList.add(new ProductTestType(type));
        }
        return ptList;
    }

    public JSONObject getImageSrcPaths() throws Exception {
        JSONObject request = new JSONObject();
        request.put("action", "local/getImageSrcPaths");
        AidlResponse response = proxyPost(request.toString());
        if (response.getCode() != 0) {
            throw new Exception(response.getMessage());
        }
        JSONObject pathSettings = new JSONObject(response.getData());
        return pathSettings;
    }

    public AIDLSetting getAIDLSetting() throws Exception {
        JSONObject request = new JSONObject();
        request.put("action", "local/getSetting");
        AidlResponse response = proxyPost(request.toString());
        if (response.getCode() != 0) {
            throw new Exception(response.getMessage());
        }
        AIDLSetting pathSettings = new Gson().fromJson(response.getData(), AIDLSetting.class);
        return pathSettings;
    }

    public List<AdBean> getAdList() throws Exception {
        JSONObject request = new JSONObject();
        request.put("action", "local/get/ad");
        AidlResponse response = proxyPost(request.toString());
        if (response.getCode() != 0) {
            throw new Exception(response.getMessage());
        }
        List<AdBean> adList = new Gson().fromJson(response.getData(),
                new TypeToken<List<AdBean>>() {
                }.getType());
        return adList;
    }

    //
    public List<Goods> getTestListByType(String type) throws JSONException {
        JSONObject request = new JSONObject();
        request.put("action", "local/get/getTestListByType");
        request.put("type", type);
        AidlResponse response = proxyPost(request.toString());
        List<Goods> goodsList = new Gson().fromJson(response.getData(),
                new TypeToken<List<Goods>>() {
                }.getType());
        return goodsList;
    }

    /**
     * @return
     * @throws Exception
     */
    public List<ProductCategory> getCategoryList() throws Exception {
        JSONObject request = new JSONObject();
        request.put("action", "local/get/category");
        AidlResponse response = proxyPost(request.toString());
        List<ProductCategory> categories = new Gson().fromJson(response.getData(),
                new TypeToken<List<ProductCategory>>() {
                }.getType());
        return categories;
    }

    public List<Product> getProductList(ProductCategory category) throws JSONException {
        JSONObject request = new JSONObject();
        request.put("action", "local/get/products");
        request.put("categoryId", category.getCategoryId());
        AidlResponse response = proxyPost(request.toString());
        List<Product> goodsList = new Gson().fromJson(response.getData(),
                new TypeToken<List<Product>>() {
                }.getType());
        return goodsList;
    }

    private AidlResponse proxyPost(String request) {
        Loger.d("AidlRequset:" + request);
        String response = "";
        try {
            response = service.proxyPost(request);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Loger.d("AidlResponse:" + response.toString());
        return new Gson().fromJson(response, AidlResponse.class);
    }
}
