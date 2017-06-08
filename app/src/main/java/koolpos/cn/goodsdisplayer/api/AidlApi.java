package koolpos.cn.goodsdisplayer.api;

import android.app.AlertDialog;
import android.os.RemoteException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import koolpos.cn.goodproviderservice.service.aidl.IGPService;
import koolpos.cn.goodsdisplayer.mvcModel.AidlResponse;
import koolpos.cn.goodsdisplayer.mvcModel.ProductType;
import koolpos.cn.goodsdisplayer.mvcModel.Goods;
import koolpos.cn.goodsdisplayer.util.Loger;

/**
 * Created by Administrator on 2017/5/14.
 */

public class AidlApi {
    private IGPService service;
    public AidlApi(IGPService service){
        this.service=service;
    }
    public List<ProductType> getTypeList() throws Exception{
        JSONObject request=new JSONObject();
        request.put("action","local/get/getTypeList");
        AidlResponse response =proxyPost(request.toString());
        if (response.getCode()!=0){
            throw new Exception(response.getMessage());
        }
        String data=response.getData();
        List<String> typeList =  new Gson().fromJson(data,
                new TypeToken<List<String>>() {
                }.getType());
        List<ProductType> ptList=new ArrayList<>();
        for (String type:typeList) {
            ptList.add(new ProductType(type));
        }
        return ptList;
    }
    public List<Goods> getListByType(String type) throws JSONException {
        JSONObject request=new JSONObject();
        request.put("action","local/get/getListByType");
        request.put("type",type);
        AidlResponse response = proxyPost(request.toString());
        List<Goods> goodsList =  new Gson().fromJson(response.getData(),
                new TypeToken<List<Goods>>() {
                }.getType());
        return goodsList;
    }
    public String getCategories() throws JSONException {
        JSONObject request=new JSONObject();
        request.put("action","local/get/category");
        AidlResponse response = proxyPost(request.toString());
        return response.toString();
    }
    public String getProduct(String categoryId) throws JSONException {
        JSONObject request=new JSONObject();
        request.put("action","local/get/products");
        request.put("categoryId",categoryId);
        AidlResponse response = proxyPost(request.toString());
        return response.toString();
    }
    private AidlResponse proxyPost(String request){
        String response = "";
        try {
            response = service.proxyPost(request);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Loger.d("AidlResponse:"+response.toString());
        return new Gson().fromJson(response,AidlResponse.class);
    }
}
