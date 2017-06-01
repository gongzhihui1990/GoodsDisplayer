package koolpos.cn.goodsdisplayer.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import koolpos.cn.goodsdisplayer.R;
import koolpos.cn.goodsdisplayer.api.AidlApi;
import koolpos.cn.goodsdisplayer.api.SkuTypeManger;
import koolpos.cn.goodsdisplayer.mvcModel.Goods;
import koolpos.cn.goodsdisplayer.util.Loger;

/**
 * Created by caroline on 2017/6/1.
 */

public class DisplaySkuAdapter extends RecyclerView.Adapter<DisplaySkuAdapter.ItemViewHolder> implements SkuTypeManger{
     class ItemViewHolder extends RecyclerView.ViewHolder{

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
    private List<Goods[]> goodGroups=new ArrayList<>();
    private final  int GroupSize= 10;

    private AidlApi aidlApi;
    public DisplaySkuAdapter(AidlApi aidlApi){
        this.aidlApi=aidlApi;
    }
    @Override
    public void setType(String type){
        Loger.d("setType");
        Observable.just(type)
                .map(new Function<String, List<Goods>>() {
                    @Override
                    public List<Goods> apply(@NonNull String type) throws Exception {
                        List<Goods> dataList = aidlApi.getListByType(type);
                        return dataList;
                    }
                })
                .map(new Function<List<Goods>, List<Goods[]>>() {
                    @Override
                    public List<Goods[]> apply(@NonNull List<Goods> dataList) throws Exception {
                        List<Goods[]> goodsGroupTmp = new ArrayList<Goods[]>();
                        Loger.d("dataList size:" + dataList.size());
                        int dataListSizeNow = dataList.size();
                        int index = 0;
                        //新建数组的大小
                        int itemSize = dataListSizeNow < GroupSize ? dataListSizeNow : GroupSize;
                        Loger.d("初始单页的大小:" + itemSize);
                        Goods[] group = new Goods[itemSize];
                        for (Goods item : dataList) {
                            //赋值项到单页数组
                            group[index] = item;
                            if (index == group.length - 1) {//到达单页数组最后一项
                                //添加单页数组
                                goodsGroupTmp.add(group);
                                //新建单页的大小
                                itemSize = dataListSizeNow < GroupSize ? dataListSizeNow - 1 : GroupSize;
                                Loger.d("新建单页的大小:" + itemSize);
                                group = new Goods[itemSize];
                                index = 0;
                            } else {
                                index++;
                            }
                            dataListSizeNow--;
                        }
                        return goodsGroupTmp;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe(new Observer<List<Goods[]>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List<Goods[]> goodsGroupTmp) {
                goodGroups.clear();
                goodGroups.addAll(goodsGroupTmp);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                notifyDataSetChanged();
            }
        });

    }
    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType== TYPE_BIG_LEFT){
            View item= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_goods_grid_bigleft,parent,false);
            return new ItemViewHolder(item);
        }else {
            View item= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_goods_grid_bigright,parent,false);
            return new ItemViewHolder(item);
        }
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return goodGroups.size();
    }

    private final static int TYPE_BIG_LEFT = 0;
    private final static int TYPE_BIG_RIGHT =1;

    @Override
    public int getItemViewType(int position) {
        if (position%2==0){
            return TYPE_BIG_LEFT;
        }else {
            return TYPE_BIG_RIGHT;
        }
    }


}
