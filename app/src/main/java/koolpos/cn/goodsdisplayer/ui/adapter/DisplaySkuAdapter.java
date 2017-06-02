package koolpos.cn.goodsdisplayer.ui.adapter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import koolpos.cn.goodsdisplayer.MyApplication;
import koolpos.cn.goodsdisplayer.R;
import koolpos.cn.goodsdisplayer.api.AidlApi;
import koolpos.cn.goodsdisplayer.api.SkuTypeManger;
import koolpos.cn.goodsdisplayer.mvcModel.Goods;
import koolpos.cn.goodsdisplayer.ui.activity.ShowDetailActivity;
import koolpos.cn.goodsdisplayer.util.FileUtil;
import koolpos.cn.goodsdisplayer.util.Loger;

/**
 * Created by caroline on 2017/6/1.
 */

public class DisplaySkuAdapter extends RecyclerView.Adapter<DisplaySkuAdapter.ItemViewHolder> implements SkuTypeManger{
     class ItemViewHolder extends RecyclerView.ViewHolder{
         @BindView(R.id.item_a_a_big)
         View ab;
         @BindView(R.id.item_a_small_1)
         View a1;
         @BindView(R.id.item_a_small_2)
         View a2;
         @BindView(R.id.item_a_small_3)
         View a3;
         @BindView(R.id.item_a_small_4)
         View a4;
         @BindView(R.id.item_b_big)
         View bb;
         @BindView(R.id.item_b_small_1)
         View b1;
         @BindView(R.id.item_b_small_2)
         View b2;
         @BindView(R.id.item_b_small_3)
         View b3;
         @BindView(R.id.item_b_small_4)
         View b4;
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
//        if (viewType== TYPE_BIG_LEFT){
//            View item= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_goods_grid_bigleft,parent,false);
//            return new ItemViewHolder(item);
//        }
//        if (viewType == TYPE_BIG_RIGHT){
//            View item= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_goods_grid_bigright,parent,false);
//            return new ItemViewHolder(item);
//        }
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_goods_grid,parent,false);
        return new ItemViewHolder(item);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        Goods[] goods = goodGroups.get(position);
        if (goods.length<=0){
            return;
        }
        renderGood(holder.ab,goods[0]);
        if (goods.length<=1){
            return;
        }
        renderGood(holder.a1,goods[1]);
        if (goods.length<=2){
            return;
        }
        renderGood(holder.a2,goods[2]);
        if (goods.length<=3){
            return;
        }
        renderGood(holder.a3,goods[3]);
        if (goods.length<=4){
            return;
        }
        renderGood(holder.a4,goods[4]);
        if (goods.length<=5){
            return;
        }
        renderGood(holder.bb,goods[5]);
        if (goods.length<=6){
            return;
        }
        renderGood(holder.b1,goods[6]);
        if (goods.length<=7){
            return;
        }
        renderGood(holder.b2,goods[7]);
        if (goods.length<=8){
            return;
        }
        renderGood(holder.b3,goods[8]);
        if (goods.length<=9){
            return;
        }
        renderGood(holder.b4,goods[9]);
    }
    public interface SkuDisplayDetail{
        public void show(Goods good);
    }
    SkuDisplayDetail call;
    public void setDetailCall(SkuDisplayDetail call){
        this.call=call;
    }
    private void renderGood(final View view,final Goods good){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(call!=null){
                    call.show(good);
                }
            }
        });
        ImageView ivGood= (ImageView) view.findViewById(R.id.good_img);
        try {
            Glide.with(view.getContext())
                    .load(FileUtil.getImageCashFile(good.getImage_url()))
                    //               .load(itemGood.getImage_url())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.mipmap.downloading)
                    .animate(R.anim.zoom_in)
                    .error(R.mipmap.download_error)
                    .into(ivGood);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        TextView tvGood= (TextView) view.findViewById(R.id.good_name);
        tvGood.setText(good.getGoods_name());
    }

    @Override
    public int getItemCount() {
        return goodGroups.size();
    }

//    private final static int TYPE_BIG_LEFT = 0;
//    private final static int TYPE_BIG_RIGHT =1;
    private final static int TYPE_ALL =2;

    @Override
    public int getItemViewType(int position) {
        return TYPE_ALL;
//        if (position%2==0){
//            return TYPE_BIG_LEFT;
//        }else {
//            return TYPE_BIG_RIGHT;
//        }
    }


}
