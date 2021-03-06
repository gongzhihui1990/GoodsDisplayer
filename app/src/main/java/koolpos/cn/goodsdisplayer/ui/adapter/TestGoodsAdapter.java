package koolpos.cn.goodsdisplayer.ui.adapter;

import android.support.v7.widget.RecyclerView;
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
import koolpos.cn.goodsdisplayer.R;
import koolpos.cn.goodsdisplayer.api.AidlApi;
import koolpos.cn.goodsdisplayer.api.TestTypeManger;
import koolpos.cn.goodsdisplayer.mvcModel.Goods;
import koolpos.cn.goodsdisplayer.rxjava.ActivityObserver;
import koolpos.cn.goodsdisplayer.ui.activity.BaseActivity;
import koolpos.cn.goodsdisplayer.util.FileUtil;
import koolpos.cn.goodsdisplayer.util.Loger;

/**
 * Created by caroline on 2017/6/1.
 */

public class TestGoodsAdapter extends RecyclerView.Adapter<TestGoodsAdapter.ItemViewHolder> implements TestTypeManger {
    private boolean fillMode = true;//填满格子
    private Goods[] fillModeItems;

    class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_a_big)
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

        public ItemViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            Observable.just(itemView)
                    .map(new Function<View, Integer>() {
                        @Override
                        public Integer apply(@NonNull View view) throws Exception {
                            int width =view.getHeight();
                            return width;
                        }
                    }).subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Integer>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(Integer width) {
                            ViewGroup.LayoutParams lp=itemView.getLayoutParams();
                            lp.width=width;
                            itemView.setLayoutParams(lp);
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
//            WindowManager wm = (WindowManager) itemView.getContext()
//                    .getSystemService(Context.WINDOW_SERVICE);
//
//            int width = wm.getDefaultDisplay().getWidth();
//            int height = wm.getDefaultDisplay().getHeight();
//            Loger.d("h-w:"+height+"-"+width);
//            ViewGroup.LayoutParams lp = itemView.getLayoutParams();
//            lp.height= (int) (height*0.35);
//            lp.width = lp.height*51/25;
//            itemView.setLayoutParams(lp);
        }
    }

    private List<Goods[]> goodGroups = new ArrayList<>();
    private final int GroupSize = 10;

    private AidlApi aidlApi;

    private BaseActivity mActivity;
    public TestGoodsAdapter(AidlApi aidlApi, BaseActivity baseActivity) {
        this.aidlApi = aidlApi;
        this.mActivity=baseActivity;
    }

    @Override
    public void setType(String type) {
        Loger.d("setCategory");
        Observable.just(type)
                .map(new Function<String, List<Goods>>() {
                    @Override
                    public List<Goods> apply(@NonNull String type) throws Exception {
                        List<Goods> dataList = aidlApi.getTestListByType(type);
                        fillModeItems = null;
                        return dataList;
                    }
                })
                .map(new Function<List<Goods>, List<Goods[]>>() {
                    @Override
                    public List<Goods[]> apply(@NonNull List<Goods> dataList) throws Exception {
                        List<Goods[]> goodsGroupTmp = new ArrayList<Goods[]>();
                        Loger.d("size:" + dataList.size());
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
                                if (fillModeItems == null){
                                    fillModeItems = group.clone();
                                }
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
                .subscribeOn(Schedulers.io()).subscribe(new ActivityObserver<List<Goods[]>>(mActivity) {

            @Override
            public void onNext(List<Goods[]> goodsGroupTmp) {
                goodGroups.clear();
                goodGroups.addAll(goodsGroupTmp);
            }

            @Override
            public void onComplete() {
                super.onComplete();
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_goods_grid, parent, false);
        return new ItemViewHolder(item);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        Goods[] goodsTmp = goodGroups.get(position);
        Goods[] goods = new Goods[10];
        Loger.d("goodsTmp len="+goodsTmp.length);
        for (int i=0;i<goods.length;i++){
            if (i<goodsTmp.length){
                goods[i] = goodsTmp[i];
            }else {
                if (fillMode&&fillModeItems!=null&&fillModeItems.length>i){
                    //填满格子
                    Loger.d("填满格子");
                    goods[i] = fillModeItems[i];
                }else {
                    Loger.e("不填满格子");
                    goods[i] = null;
                }
            }
        }
        renderGood(holder.ab, goods[0]);
        renderGood(holder.a1, goods[1]);
        renderGood(holder.a2, goods[2]);
        renderGood(holder.a3, goods[3]);
        renderGood(holder.a4, goods[4]);
        renderGood(holder.bb, goods[5]);
        renderGood(holder.b1, goods[6]);
        renderGood(holder.b2, goods[7]);
        renderGood(holder.b3, goods[8]);
        renderGood(holder.b4, goods[9]);

//        if (goods.length <= 0) {
//            return;
//        }
//        renderGood(holder.ab, goods[0]);
//        if (goods.length <= 1) {
//            return;
//        }
//        renderGood(holder.a1, goods[1]);
//        if (goods.length <= 2) {
//            return;
//        }
//        renderGood(holder.a2, goods[2]);
//        if (goods.length <= 3) {
//            return;
//        }
//        renderGood(holder.a3, goods[3]);
//        if (goods.length <= 4) {
//            return;
//        }
//        renderGood(holder.a4, goods[4]);
//        if (goods.length <= 5) {
//            return;
//        }
//        renderGood(holder.bb, goods[5]);
//        if (goods.length <= 6) {
//            return;
//        }
//        renderGood(holder.b1, goods[6]);
//        if (goods.length <= 7) {
//            return;
//        }
//        renderGood(holder.b2, goods[7]);
//        if (goods.length <= 8) {
//            return;
//        }
//        renderGood(holder.b3, goods[8]);
//        if (goods.length <= 9) {
//            return;
//        }
//        renderGood(holder.b4, goods[9]);
    }

    public interface SkuDisplayDetailCall {
        public void show(Goods good);
    }

    SkuDisplayDetailCall call;

    public void setSkuDisplayCallBack(SkuDisplayDetailCall call) {
        this.call = call;
    }

    private void renderGood(final View view, final Goods good) {
        if (good == null) {
            view.setVisibility(View.INVISIBLE);
            return;
        }
        view.setVisibility(View.VISIBLE);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (call != null) {
                    call.show(good);
                }
            }
        });
        ImageView ivGood = (ImageView) view.findViewById(R.id.good_img);
        int idRoot = view.getId();
        switch (idRoot) {
            case R.id.item_a_big:
            case R.id.item_b_big:
                ivGood.setPadding(40, 40, 40, 40);
                break;
            default:
                ivGood.setPadding(20, 20, 20, 20);
                break;
        }
        try {
            Glide.with(view.getContext())
                    .load(FileUtil.getImageCashFile(good.getImage_url()))
                    //               .load(itemGood.getImage_url())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.mipmap.downloading)
                    .animate(R.anim.zoom_in)
                    .fitCenter()
                    .error(R.mipmap.download_error)
                    .into(ivGood);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        TextView tvGood = (TextView) view.findViewById(R.id.good_name);
        tvGood.setText(good.getGoods_name().substring(0,5));
    }

    @Override
    public int getItemCount() {
        return goodGroups.size();
    }

    private final static int TYPE_ALL = 2;

    @Override
    public int getItemViewType(int position) {
        return TYPE_ALL;
    }


}
