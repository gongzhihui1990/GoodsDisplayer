package koolpos.cn.goodsdisplayer.ui.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import koolpos.cn.goodsdisplayer.R;
import koolpos.cn.goodsdisplayer.mvcModel.ProductTestType;

/**
 * Created by caroline on 2017/5/29.
 */

public class TestTypePopupWindow extends PopupWindow {
    public interface OnSPUSelectedListener{
        public void onSelected(int index);
    }
    private Context mContext;

    private View view;

    private TextView btn_cancel;
//    @BindView(R.id.list_content_title)
    RecyclerView listSPU;

    public TestTypePopupWindow(Context mContext, int  selectedIndex, List<ProductTestType> typeList, final OnSPUSelectedListener listener) {

        this.view = LayoutInflater.from(mContext).inflate(R.layout.layout_select_type, null);
        listSPU = (RecyclerView) this.view.findViewById(R.id.list_spu);
        LinearLayoutManager titleLayoutManager = new LinearLayoutManager(mContext);
        titleLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        SpacesItemDecoration itemDecoration =new SpacesItemDecoration(10);
        listSPU.setLayoutManager(titleLayoutManager);
        listSPU.addItemDecoration(itemDecoration);
        OnSPUSelectedListener onSPUSelectedListener =new OnSPUSelectedListener() {
            @Override
            public void onSelected(int  selectedIndex) {
                listener.onSelected(selectedIndex);
                dismiss();
            }
        };
        GoodTestTypeAdapter typeAdapter=new GoodTestTypeAdapter(onSPUSelectedListener);
        typeAdapter.setData(typeList,selectedIndex);
        listSPU.setAdapter(typeAdapter);
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
    public class GoodTestTypeAdapter extends RecyclerView.Adapter<GoodTestTypeAdapter.GoodTypeViewHolder>{

        private List<ProductTestType> data=new ArrayList<>();
        private int curIndex = -1;
        private OnSPUSelectedListener onSPUSelectedListener;
        public GoodTestTypeAdapter(OnSPUSelectedListener onSPUSelectedListener) {
            this.onSPUSelectedListener=onSPUSelectedListener;
        }

        public void setData(List<ProductTestType> data, int selectedIndex) {
            this.data = data;
            this.curIndex =selectedIndex;
            myNotifyDataSetChanged();
        }

        public void myNotifyDataSetChanged(){
            //mGridAdapter.setDataByType(data.get(curIndex).getTypeName());
            notifyDataSetChanged();
        }
        @Override
        public GoodTestTypeAdapter.GoodTypeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_good_title,parent,false);
            return new GoodTestTypeAdapter.GoodTypeViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final GoodTestTypeAdapter.GoodTypeViewHolder holder, int position) {
            ProductTestType dataTmp = data.get(position);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (curIndex!=holder.getAdapterPosition()){
                        curIndex =holder.getAdapterPosition();
                        if (onSPUSelectedListener!=null){
                            onSPUSelectedListener.onSelected(curIndex);
                        }
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

        class GoodTypeViewHolder extends RecyclerView.ViewHolder{
            TextView good_type;
            private GoodTypeViewHolder(View itemView){
                super(itemView);
                good_type= (TextView) itemView.findViewById(R.id.good_type);
            }
        }
    }
}
