package koolpos.cn.goodsdisplayer.ui.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import koolpos.cn.goodsdisplayer.R;
import koolpos.cn.goodsdisplayer.mvcModel.ProductCategory;

/**
 * Created by caroline on 2017/5/29.
 */

public class CategoryPop extends PopupWindow {
    public interface OnSPUSelectedListener {
        public void onSelected(ProductCategory categorySelect);
    }

    private Context mContext;

    private View view;

    private TextView btn_cancel;
    RecyclerView listCategroies;

    public CategoryPop(Context context, List<ProductCategory> typeList, final OnSPUSelectedListener listener) {
        this.mContext=context;
        this.view = LayoutInflater.from(mContext).inflate(R.layout.layout_select_type, null);
        listCategroies = (RecyclerView) this.view.findViewById(R.id.list_spu);
        LinearLayoutManager titleLayoutManager = new LinearLayoutManager(mContext);
        titleLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        SpacesItemDecoration itemDecoration = new SpacesItemDecoration(10);
        listCategroies.setLayoutManager(titleLayoutManager);
        listCategroies.addItemDecoration(itemDecoration);
        OnSPUSelectedListener onSPUSelectedListener = new OnSPUSelectedListener() {
            @Override
            public void onSelected(ProductCategory categorySelect) {
                listener.onSelected(categorySelect);
                dismiss();
            }
        };
        CategoryAdapter typeAdapter = new CategoryAdapter(onSPUSelectedListener);
        typeAdapter.setData(typeList);
        listCategroies.setAdapter(typeAdapter);
        // 设置外部可点击
        this.setOutsideTouchable(true);
        // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        this.view.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = view.findViewById(R.id.list_spu).getTop();

                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });


    /* 设置弹出窗口特征 */
        // 设置视图
        this.setContentView(this.view);
        // 设置弹出窗体的宽和高
        this.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
        this.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);

        // 设置弹出窗体可点击
        this.setFocusable(true);

        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x66000000);
        // 设置弹出窗体的背景
        this.setBackgroundDrawable(dw);

        // 设置弹出窗体显示时的动画，从底部向上弹出
        this.setAnimationStyle(R.style.take_photo_anim);

    }

    public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.GoodTypeViewHolder> {

        private List<ProductCategory> data = new ArrayList<>();
        private int curIndex = -1;
        private OnSPUSelectedListener onSPUSelectedListener;

        public CategoryAdapter(OnSPUSelectedListener onSPUSelectedListener) {
            this.onSPUSelectedListener = onSPUSelectedListener;
        }

        public void setData(List<ProductCategory> data) {
            this.data = data;
            this.curIndex = 0;
            myNotifyDataSetChanged();
        }

        public void myNotifyDataSetChanged() {
            notifyDataSetChanged();
        }

        @Override
        public CategoryAdapter.GoodTypeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_good_title, parent, false);
            return new CategoryAdapter.GoodTypeViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final CategoryAdapter.GoodTypeViewHolder holder, int position) {
            final ProductCategory dataTmp = data.get(position);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (curIndex != holder.getAdapterPosition()) {
                        curIndex = holder.getAdapterPosition();
                        if (onSPUSelectedListener != null) {
                            onSPUSelectedListener.onSelected(dataTmp);
                        }
                        myNotifyDataSetChanged();
                    }
                }
            });
            holder.good_type.setSelected(curIndex == position);
            holder.good_type.setText(data.get(position).getName());
            Glide.with(mContext).load(data.get(position).getIconUrl())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.mipmap.downloading)
                    .animate(R.anim.zoom_in)
                    .fitCenter()
                    .error(R.mipmap.download_error)
                    .into(holder.category_icon);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class GoodTypeViewHolder extends RecyclerView.ViewHolder {
            TextView good_type;
            ImageView category_icon;

            private GoodTypeViewHolder(View itemView) {
                super(itemView);
                good_type = (TextView) itemView.findViewById(R.id.good_type);
                category_icon = (ImageView) itemView.findViewById(R.id.category_icon);
            }
        }
    }
}
