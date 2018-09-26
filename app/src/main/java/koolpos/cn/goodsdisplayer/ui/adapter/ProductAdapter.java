package koolpos.cn.goodsdisplayer.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
import koolpos.cn.goodsdisplayer.api.CategoryManger;
import koolpos.cn.goodsdisplayer.mvcModel.Product;
import koolpos.cn.goodsdisplayer.mvcModel.ProductCategory;
import koolpos.cn.goodsdisplayer.rxjava.ActivityObserver;
import koolpos.cn.goodsdisplayer.ui.activity.BaseActivity;
import koolpos.cn.goodsdisplayer.util.AndroidUtils;
import koolpos.cn.goodsdisplayer.util.Loger;

/**
 * Created by caroline on 2017/6/1.
 */

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ItemViewHolder> implements CategoryManger {
    private final static int TYPE_ALL = 2;
    private final int GroupSize = AndroidUtils.isScreenOriatationPortrait() ? 15 : 10;
    SkuDisplayDetailCall call;
    private boolean fillMode = true;//填满格子
    private Product[] fillModeItems;
    private List<Product[]> goodGroups = new ArrayList<>();
    private AidlApi aidlApi;
    private BaseActivity mActivity;

    public ProductAdapter(AidlApi aidlApi, BaseActivity baseActivity) {
        this.aidlApi = aidlApi;
        this.mActivity = baseActivity;
    }

    @Override
    public void setCategory(ProductCategory category) {
        Loger.d("setCategory");
        Observable.just(category).map(new Function<ProductCategory, List<Product>>() {
            @Override
            public List<Product> apply(@NonNull ProductCategory category) throws Exception {
                List<Product> dataList = aidlApi.getProductList(category);
                fillModeItems = null;
                return dataList;
            }
        }).map(new Function<List<Product>, List<Product[]>>() {
            @Override
            public List<Product[]> apply(@NonNull List<Product> allData) throws Exception {
                Loger.w("生成页面");
                List<Product[]> goodsGroupTmp = new ArrayList<>();
                Loger.d("展示元素size:" + allData.size());
                int dataListSizeNow = allData.size();
                int index = 0;
                //新建数组的大小
                Product[] group = null;
                boolean hasNext = false;
                for (Product item : allData) {
                    //赋值项到单页数组
                    if (index == 0) {
                        //新建单页的大小
                        int pageSize = dataListSizeNow < GroupSize ? dataListSizeNow : GroupSize;
                        group = new Product[pageSize];
                        Loger.d("新建单页的大小:" + pageSize);
                    }
                    group[index] = item;
                    if (index == group.length - 1) {
                        //到达单页数组最后一项,添加单页数组
                        hasNext = false;
                        goodsGroupTmp.add(group);
                        if (fillModeItems == null) {
                            fillModeItems = group.clone();
                        }

                        index = 0;
                    } else {
                        index++;
                        hasNext = true;
                    }
                    dataListSizeNow--;
                }
                if (hasNext) {
                    goodsGroupTmp.add(group);
                }

                Loger.w("页面大小 " + goodsGroupTmp.size());
                return goodsGroupTmp;
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new ActivityObserver<List<Product[]>>(mActivity) {

            @Override
            public void onNext(List<Product[]> goodsGroupTmp) {
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
        if (AndroidUtils.isScreenOriatationPortrait()) {
            View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_goods_grid_port, parent, false);
            item.findViewById(R.id.port).setVisibility(View.VISIBLE);
            return new ItemViewHolder(item);
        } else {
            View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_goods_grid_port, parent, false);
            item.findViewById(R.id.port).setVisibility(View.GONE);
            return new ItemViewHolder(item);

        }
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        Product[] goodsTmp = goodGroups.get(position);
        Product[] goods = new Product[GroupSize];
        Loger.d("goodsTmp len=" + goodsTmp.length);
        for (int i = 0; i < goods.length; i++) {
            if (i < goodsTmp.length) {
                goods[i] = goodsTmp[i];
            } else {
                if (fillMode && fillModeItems != null && fillModeItems.length > i) {
                    //填满格子
                    Loger.d("填满格子");
                    goods[i] = fillModeItems[i];
                } else {
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

        if (GroupSize == 15) {
            renderGood(holder.cb, goods[10]);
            renderGood(holder.c1, goods[11]);
            renderGood(holder.c2, goods[12]);
            renderGood(holder.c3, goods[13]);
            renderGood(holder.c4, goods[14]);
        }
    }

    public void setSkuDisplayCallBack(SkuDisplayDetailCall call) {
        this.call = call;
    }

    private void renderGood(final View view, final Product good) {
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
            case R.id.item_c_big:
                ivGood.setPadding(3, 3, 3, 3);
                break;
            default:
                ivGood.setPadding(3, 3, 3, 3);
                break;
        }
        AndroidUtils.loadImageAnim(good.getPicUrl(), ivGood);
//            Glide.with(view.getContext())
//                    .load(good.getPicUrl())
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .placeholder(R.mipmap.downloading)
//                    .animate(R.anim.zoom_in)
//                    .fitCenter()
//                    .error(R.mipmap.download_error)
//                    .into(ivGood);
        TextView tvGood = (TextView) view.findViewById(R.id.good_name);
        tvGood.setText("");
//        tvGood.setText(good.getTitle());
    }

    @Override
    public int getItemCount() {
        return goodGroups.size();
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_ALL;
    }

    public interface SkuDisplayDetailCall {
        public void show(Product good);
    }

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

        @BindView(R.id.item_c_big)
        View cb;
        @BindView(R.id.item_c_small_1)
        View c1;
        @BindView(R.id.item_c_small_2)
        View c2;
        @BindView(R.id.item_c_small_3)
        View c3;
        @BindView(R.id.item_c_small_4)
        View c4;

        public ItemViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            Observable.just(itemView).map(new Function<View, Integer>() {
                @Override
                public Integer apply(@NonNull View view) throws Exception {
                    int width = view.getHeight();
                    if (AndroidUtils.isScreenOriatationPortrait()) {
                        width = width * 2 / 3;
                    }
                    return width;
                }
            }).subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Integer>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(Integer width) {
                    ViewGroup.LayoutParams lp = itemView.getLayoutParams();
                    lp.width = width;
                    itemView.setLayoutParams(lp);
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onComplete() {

                }
            });
        }
    }


}
