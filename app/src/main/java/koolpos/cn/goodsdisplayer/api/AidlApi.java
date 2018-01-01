package koolpos.cn.goodsdisplayer.api;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.RemoteException;
import android.os.TransactionTooLargeException;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import koolpos.cn.goodproviderservice.service.aidl.IGPService;
import koolpos.cn.goodsdisplayer.MyApplication;
import koolpos.cn.goodsdisplayer.constans.ImageEnum;
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
@TargetApi(Build.VERSION_CODES.M)
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

    public boolean isServerStateOk() throws Exception {
        JSONObject request = new JSONObject();
        request.put("action", "local/get/appState");
        AidlResponse response = proxyPost(request.toString());
        if (response == null || response.getData() == null) {
            return false;
        }
        JSONObject data = new JSONObject(response.getData());
        if ("Ok".equals(data.optString("stateEnum"))) {
            return true;
        }
        return false;
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

    public List<Goods> getTestListByType(String type) throws JSONException, RemoteException {
        JSONObject request = new JSONObject();
        request.put("action", "local/get/getTestListByType");
        request.put("type", type);
        AidlResponse response = proxyPost(request.toString());
        List<Goods> goodsList = new Gson().fromJson(response.getData(),
                new TypeToken<List<Goods>>() {
                }.getType());
        return goodsList;
    }

    private boolean isUnique(@NonNull ProductCategory category, @NonNull List<ProductCategory> categoriesUnique) {
        for (ProductCategory unique : categoriesUnique) {
            Loger.d(unique.getName() + "/" + category.getName());
            if (unique.getName().equals(category.getName())) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return
     * @throws Exception
     */
    public List<ProductCategory> getCategoryList() throws Exception {
        JSONObject request = new JSONObject();
        request.put("action", "local/get/category");
        AidlResponse response = proxyPost(request.toString());
        final List<ProductCategory> categoriesSrc = new Gson().fromJson(response.getData(),
                new TypeToken<List<ProductCategory>>() {
                }.getType());
        List<ProductCategory> categoriesUnique = new ArrayList<>();
        List<ProductCategory> warpedCategories = new ArrayList<>();
        if (categoriesSrc != null && categoriesSrc.size() != 0) {
            for (ProductCategory category : categoriesSrc) {
                // 添加不重复的分类
                if (isUnique(category, categoriesUnique)) {
                    categoriesUnique.add(category);
                }
            }
            //添加‘全部’分类
            String path = MyApplication.PATHJson.optString(ImageEnum.HOME_BTN.name());
            Loger.d("path all:" + path);
            ProductCategory categoryAll = new ProductCategory();
            categoryAll.setCategoryId(-1);
            categoryAll.setName("全部");
            categoryAll.setIconUrl(path);
            categoryAll.setImageUrl(path);
            warpedCategories.add(categoryAll);
            warpedCategories.addAll(categoriesUnique);
        }
        return warpedCategories;
    }

    public List<Product> getProductList(ProductCategory category) throws JSONException, RemoteException {
        JSONObject request = new JSONObject();
        request.put("action", "local/get/products");
        request.put("categoryId", category.getCategoryId());
        AidlResponse response = proxyPost(request.toString());
        List<Product> goodsList = new Gson().fromJson(response.getData(),
                new TypeToken<List<Product>>() {
                }.getType());
        return goodsList;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private AidlResponse proxyPost(String request) throws RemoteException {
        Loger.d("AidlRequset:" + request);
        String responseFile = "";
        try {
            responseFile = service.proxyPost(request);
        } catch (RemoteException e) {
            e.printStackTrace();
            if (e instanceof TransactionTooLargeException) {
                throw e;
            }
        }
        Loger.d("AidlResponse file:" + responseFile);
        String data = getResponseFromFile(responseFile);
        Loger.d("AidlResponse data:" + data);
        return new Gson().fromJson(data, AidlResponse.class);
    }

    private String getJsonFileToString(String jsonFileName) throws IOException {
        File jsonFile = new File(jsonFileName);
        InputStream is = new FileInputStream(jsonFile.getAbsolutePath());
        String line; // 用来保存每行读取的内容
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        line = reader.readLine(); // 读取第一行
        StringBuilder buffer = new StringBuilder();
        while (line != null) { // 如果 line 为空说明读完了
            buffer.append(line); // 将读到的内容添加到 buffer 中
            buffer.append("\n"); // 添加换行符
            line = reader.readLine(); // 读取下一行
        }
        reader.close();
        is.close();
        return buffer.toString();
    }

    private String getResponseFromFile(String filePath) {
        AidlResponse defError = new AidlResponse();
        String contentResponse = "";
        try {
            contentResponse = getJsonFileToString(filePath);
        } catch (Exception e) {
            e.printStackTrace();
            defError.setMessage("请求解析失败:" + e.getMessage());
            defError.setCode(-1);
            contentResponse = defError.toString();
        }
        return contentResponse;
    }
}
