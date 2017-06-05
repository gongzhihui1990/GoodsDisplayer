package koolpos.cn.goodsdisplayer.ui.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import koolpos.cn.goodproviderservice.service.aidl.IGPService;
import koolpos.cn.goodsdisplayer.R;
import koolpos.cn.goodsdisplayer.api.AidlApi;
import koolpos.cn.goodsdisplayer.mvcModel.ProductType;
import koolpos.cn.goodsdisplayer.mvcModel.Goods;
import koolpos.cn.goodsdisplayer.util.AndroidUtils;
import koolpos.cn.goodsdisplayer.util.Loger;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivityOld extends BaseActivity {

    @BindView(R.id.grid_content)
    RecyclerView gridContent;
    @BindView(R.id.list_content_title)
    RecyclerView listContentTitle;
    private GoodContentAdapter gridAdapter;
    private GoodTypeAdapter typeAdapter;

    private AidlApi aidlApi;
    private IGPService gpService;
    ServiceConnection connection =new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            gpService= IGPService.Stub.asInterface(iBinder);
            aidlApi =new AidlApi(gpService);
            try {
//                JSONObject request=new JSONObject();
//                request.put("action","local/get/all");
//                String response =gpService.proxyPost(request.toString());
//                Loger.d("response:"+response);
                JSONObject request=new JSONObject();
                request.put("action","local/get/getTypeList");
                String response =gpService.proxyPost(request.toString());
                Loger.d("response:"+response);

                typeAdapter.setData(aidlApi.getTypeList());
//                JSONObject request=new JSONObject();
//                request.put("action","local/get/all");
//                String response =gpService.proxyPost(request.toString());
//                Loger.d("response:"+response);

//                case "local/get/getListByType":
//                    String type = reqJson.optString("type");
//                    response.setData(getListByType(type));
//                    break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        gridAdapter =new GoodContentAdapter();
        typeAdapter =new GoodTypeAdapter(gridAdapter);
        //Content布局
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        gridLayoutManager.setOrientation(GridLayoutManager.HORIZONTAL);
        gridContent.setLayoutManager(gridLayoutManager);
        gridContent.setAdapter(gridAdapter);
        //Title布局
        GridLayoutManager titleLayoutManager = new GridLayoutManager(this,1);
        titleLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        listContentTitle.setLayoutManager(titleLayoutManager);
        listContentTitle.setAdapter(typeAdapter);
        Intent serviceIntent=new Intent(IGPService.class.getName());
        serviceIntent= AndroidUtils.getExplicitIntent(getBaseContext(),serviceIntent);
        //serviceIntent.setPackage("koolpos.cn.goodproviderservice");//这里你需要设置你应用的包名
        boolean bindService=bindService(serviceIntent,connection, Context.BIND_AUTO_CREATE);
        Loger.d("bindService "+bindService);
    }

    @Override
    protected void onDestroy() {
        unbindService(connection);
        super.onDestroy();
    }

    public class GoodContentAdapter extends RecyclerView.Adapter<GoodContentAdapter.GoodViewHolder>{

        private List<Goods> data=new ArrayList<>();

        public void setData(ArrayList<Goods> data) {
            this.data = data;
            notifyDataSetChanged();
        }
        public void setDataByType(String type) {
            data.clear();
            try {
                data = aidlApi.getListByType(type);
                notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public GoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_good,parent,false);
            GoodViewHolder holder = new GoodViewHolder(itemView);
            return holder;
        }

        @Override
        public void onBindViewHolder(GoodViewHolder holder, int position) {
            holder.good_name.setText(data.get(position).getGoods_name());
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public class GoodViewHolder extends RecyclerView.ViewHolder{
            TextView good_name;
            public GoodViewHolder(View itemView){
                super(itemView);
                good_name= (TextView) itemView.findViewById(R.id.good_name);
            }
        }
    }
    public class GoodTypeAdapter extends RecyclerView.Adapter<GoodTypeAdapter.GoodTypeViewHolder>{

        private List<ProductType> data=new ArrayList<>();
        private int curIndex = 0;
        private GoodContentAdapter gridAdapter;
        public GoodTypeAdapter(GoodContentAdapter gridAdapter) {
            this.gridAdapter=gridAdapter;
        }

        public void setData(List<ProductType> data) {
            this.data = data;
            myNotifyDataSetChanged();
        }

        public void myNotifyDataSetChanged(){
            gridAdapter.setDataByType(data.get(curIndex).getTypeName());
            notifyDataSetChanged();
        }
        @Override
        public GoodTypeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_good_title,parent,false);
            GoodTypeViewHolder holder=new GoodTypeViewHolder(itemView);
            return holder;
        }

        @Override
        public void onBindViewHolder(GoodTypeViewHolder holder, final int position) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (curIndex!=position){
                        curIndex =position;
                        myNotifyDataSetChanged();
                    }
                }
            });
            holder.good_type.setSelected(curIndex == position);
            holder.good_type.setText(data.get(position).getTypeName());
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public class GoodTypeViewHolder extends RecyclerView.ViewHolder{
            TextView good_type;
            public GoodTypeViewHolder(View itemView){
                super(itemView);
                good_type= (TextView) itemView.findViewById(R.id.good_type);
            }
        }
    }
}
